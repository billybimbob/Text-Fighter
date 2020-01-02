package main;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.lang.reflect.Constructor;

import assets.*;
import assets.items.*;
import combat.moves.*;

public class Index {

	private static class EntityList <T extends Entity> {
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

	private static class Moves { //for grouping
		static final Map<String, String> aliases = new HashMap<>(); //for name to move
		static final Map<String, Constructor<? extends Ability>> constrs = new HashMap<>();
	}
	private static final EntityList<Monster> monsters = new EntityList<>();
	private static final EntityList<Potion> potions = new EntityList<>();
	private static final EntityList<Armor> armors = new EntityList<>();

	static {
		mapMoves();
		readPotions();
		readArmors();
		readMonsters();
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

				var move = tok[0].toLowerCase();
				var path = new StringBuilder("combat.moves.");
				if (tok.length > 2)
					path.append( tok[2].strip().concat(".") );
				path.append(tok[1].strip());

				var classRef = Class.forName(path.toString()).asSubclass(Ability.class);
				var constructor = classRef.getDeclaredConstructor(Monster.class);

				Moves.constrs.put(move, constructor);
				Moves.aliases.put(classRef.getName(), move);
			}

		} catch (IOException e) {
			throw new RuntimeException("Issue reading moves\n" + e.getMessage());
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			throw new RuntimeException("Issue finding listed move\n" + e.getMessage());
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
			throw new RuntimeException("Issue reading potions\n" + e.getMessage());
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
			throw new RuntimeException("Issue reading armors\n" + e.getMessage());
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
					List<String> moves = Arrays.stream(tok[3].split(","))
						.map(String::toLowerCase)
						.collect(Collectors.toList());

					if (parseLen >= 5) { //has passive
						String passive = tok[4].toLowerCase();
						monsters.add(new Monster(name, defltAggro, attType, stats, moves, passive));

					} else
						monsters.add(new Monster(name, defltAggro, attType, stats, moves));

				} else //basic
					monsters.add(new Monster(name, defltAggro, attType, stats));
			}

		} catch (IOException e) {
			throw new RuntimeException("Issue reading monsters\n" + e.getMessage());
		}

	}

	//uses index attrs
	public static Ability createAbility(String name, Monster user) { //wrapper for getting and apply
		try {
			return Moves.constrs.get(name).newInstance(user);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Issue creating ability\n" + e.getMessage());
		}
	}

	private static String abilityAlias (Ability ability) {
		return Moves.aliases.get(ability.getClass().getName());
	}

	public static Ability createAbility(Ability copy, Monster user) {
		return createAbility(abilityAlias(copy), user);
	}

	public static Ability createPassive(String name, Monster user) {
		Ability ability = createAbility(name, user);
		if (!ability.isPassive())
			throw new RuntimeException("given abilty is not passive");

		return ability;
	}
	public static Ability createPassive(Ability copy, Monster user) {
		return createPassive(abilityAlias(copy), user);
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
