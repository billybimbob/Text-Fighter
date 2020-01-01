package main;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import assets.*;
import assets.items.*;
import combat.moves.*;

public class Index {

	public static enum Move { //used to make map limited
		BASIC, SHEEP, //attacks
		BURST, DRAIN, FREEZE, FRENZY, POLY, POSSESS, REFLECT, SHIFT, SHOCK, //magic
		CHARGE, DISRUPT, REVENGE, SPIN, //melee
		INTIM, RAGE; //passives
	}


	private static class NameList <T extends Entity> {
		final List<T> lst = new ArrayList<>(); //can't index straight into
		final Map<Integer, Integer> ids = new HashMap<>(); //ids to list idx
		final Map<String, Integer> names = new HashMap<>(); //name to list idx

		void add(T adding) {
			lst.add(adding);
			ids.put(adding.getId(), lst.size()-1); //should be the list idx
			names.put(adding.getName(), lst.size()-1);
		}

		int size() { return lst.size(); }
		T idxGet(int idx) {return lst.get(idx); }

		T get(int id) { return this.idxGet( ids.get(id) ); }
		T get(String name) { return this.idxGet( names.get(name) ); }
	}
	
	//encapsulated away ability to add values after init
	private static final Map<String, Move> moveNames = new HashMap<>(); //for name to move
	private static final Map<Move, Constructor<? extends Ability>> moves = new HashMap<>();
	private static final NameList<Monster> monsters = new NameList<>();
	private static final NameList<Potion> potions = new NameList<>();
	private static final NameList<Armor> armors = new NameList<>();
	static {
		mapMoves();
		readPotions();
		readArmors();
		readMonsters();
		//mapMoveNames();
	}

	private Index() { }

	private static void mapMoves() {
		//could split different class abilities
		try (BufferedReader reader = new BufferedReader(new FileReader("src/data/moves.txt"))) {
			
			String line;
			while((line = reader.readLine()) != null) {
				String[] tok = line.split(",\\s+");
				if (tok.length < 2)
					continue;

				var move = Move.valueOf(tok[0].toUpperCase());

				var path = new StringBuilder("combat.moves.");
				if (tok.length > 2)
					path.append( tok[2].strip().concat(".") );
				path.append(tok[1].strip());

				var constructor = Class.forName(path.toString())
					.asSubclass(Ability.class)
					.getDeclaredConstructor(Monster.class);

				moves.put(move, constructor);
			}

		} catch (IOException e) {
			System.err.println("Issue reading moves");
			System.exit(-1);
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			System.err.println("Issue finding listed move\n" + e);
			System.exit(-1);
		}

	}


	private static void readPotions() {
		try (BufferedReader reader = new BufferedReader(new FileReader("src/data/potion.txt"))) {
			String line;

			while((line = reader.readLine()) != null) {
				String[] tok = line.split(",\\s+");
				if (tok.length < 5)
					continue;

				String name = tok[0];
				List<Stat> stats = Arrays.stream(tok[1].split(","))
					.map(str -> Stat.valueOf(str.toUpperCase()))
					.collect(Collectors.toList());
				int val = Integer.parseInt(tok[2]);
				int duration = Integer.parseInt(tok[3]);
				boolean overTime = Boolean.parseBoolean(tok[4]);

				potions.add(new Potion(name, stats, val, duration, overTime));
			}

		} catch (IOException e) {
			System.err.println("Issue reading potions");
			System.exit(-1);
		}
	}

	private static void readArmors() {
		try (BufferedReader reader = new BufferedReader(new FileReader("src/data/equip.txt"))) {
			String line;

			while((line = reader.readLine()) != null) {
				String[] toks = line.split(",\\s+");
				if (toks.length < 6)
					continue;

				String name = toks[0];
				Slot slot = Slot.valueOf(toks[1].toUpperCase());
				boolean attType = Boolean.parseBoolean(toks[2]);
				int space = Integer.parseInt(toks[3]);
				List<Stat> stats = Arrays.stream(toks[4].split(","))
					.map(stat -> Stat.valueOf(stat.toUpperCase()))
					.collect(Collectors.toList());
				int modVal = Integer.parseInt(toks[5]);

				armors.add(new Armor(name, slot, attType, space, stats, modVal));
			}

		} catch (IOException e) {
			System.err.println("Issue reading armors");
			System.exit(-1);		
		}
	}


	private static void readMonsters() {

		try (BufferedReader reader = new BufferedReader(new FileReader("src/data/monster.txt"));) {
			final boolean defltAggro = false;
			String line;
			
			while((line = reader.readLine()) != null) {
				String[] tok = line.split(",\\s+");
				final int parseLen = tok.length;

				if (parseLen < 3) //skip line
					continue;

				String name = tok[0];
				boolean attType = Boolean.parseBoolean(tok[1]);
				List<Integer> stats = Arrays.stream(tok[2].split(","))
					.map(numTok -> Integer.parseInt(numTok))
					.collect(Collectors.toList());

				if (parseLen >= 4) { //has special moves
					List<Move> moves = Arrays.stream(tok[3].split(","))
						.map(moveTok -> Move.valueOf(moveTok.toUpperCase()))
						.collect(Collectors.toList());

					if (parseLen >= 5) { //has passive
						Move passive = Move.valueOf(tok[4].toUpperCase());
						monsters.add(new Monster(name, defltAggro, attType, stats, moves, passive));

					} else
						monsters.add(new Monster(name, defltAggro, attType, stats, moves));

				} else //basic
					monsters.add(new Monster(name, defltAggro, attType, stats));
			} 

		} catch (IOException e) {
			System.err.println("Issue reading monsters");
			System.exit(-1);
		}

	}

	private static void mapMoveNames() {
		Monster dummyMon = new Monster(); //kind of hacky

		try {
			for (var entry: moves.entrySet()) {
				var tempAbility = entry.getValue().newInstance(dummyMon);
				moveNames.put(tempAbility.getName(), entry.getKey());
			}

		} catch (ReflectiveOperationException e) {
			System.err.println("Issue creating dummy Ability");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	//uses index attrs
	public static Ability createAbility(Move name, Monster user) { //wrapper for getting and apply
		//return attacks.get(name).apply(user);
		try {
			return moves.get(name).newInstance(user);

		} catch (ReflectiveOperationException e) {
			System.err.println("Issue creating ability");
			e.printStackTrace();
			System.exit(-1);
		}

		return null;
	}
	public static Ability createAbility(String name, Monster user) {
		return createAbility(moveNames.get(name), user);
	}

	public static Ability createPassive(Move name, Monster user) {
		return createAbility(name, user);
	}
	public static Ability createPassive(String name, Monster user) {
		return createPassive(moveNames.get(name), user);
	}

	public static Potion getPotion(int id) { return potions.get(id); }
	public static Potion getPotion(String name) { return potions.get(name); }

	public static Armor getArmor(int id) { return armors.get(id); }
	public static Armor getArmor(String name) { return armors.get(name); }

	public static Monster getMonBase(int id) { return monsters.get(id); }
	public static Monster getMonBase(String name) { return monsters.get(name); }


	public static Monster createMonster(int id) {
		return new Monster( getMonBase(id) );
	}
	public static Monster createMonster(String name) {
		return new Monster( getMonBase(name) );
	}


	public static Monster randomMonster() {
		int idx = (int)(Math.random()*monsters.size());
		return new Monster( monsters.idxGet(idx) );
	}
}
