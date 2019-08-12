package main;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.*;

import assets.*;
import combat.moves.*;
import combat.moves.magic.*;
import combat.moves.melee.*;
import combat.moves.passive.*;

public class Index {

	public static enum Move { 
		BASIC, SHEEP, //atcks
		BURST, DRAIN, FREEZE, FRENZY, POLY, POSSESS, REFLECT, SHIFT, SHOCK, //magic
		CHARGE, DISRUPT, REVENGE, SPIN, //melee
		INTIM, RAGE; //passives
	}


	private static class NameList <T extends Entity> {
		final List<T> lst = new ArrayList<>();
		final Map<Integer, Integer> ids = new HashMap<>(); //name to list idx
		final Map<String, Integer> names = new HashMap<>();

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
	
	
	private static class Abilities { //used to lump togeter
		static final Map<Move, Function<Monster, Ability>> attacks = new HashMap<>();
		static final Map<Move, Function<Monster, Ability>> passives = new HashMap<>();

		static void addPassive(Move move, Function<Monster, Ability> construct) {
			passives.put(move, construct);
		}
		static void addAttack(Move move, Function<Monster, Ability> construct) {
			attacks.put(move, construct);
		}
		
		static Function<Monster, Ability> getPassive(Move move) { return passives.get(move); }
		static Function<Monster, Ability> getAttack(Move move) { return attacks.get(move); }
	}

	private static final NameList<Monster> monsters = new NameList<>();
	private static final NameList<Potion> potions = new NameList<>();
	

	private Index() { }

	public static void createVals() {
		mapMoves();
		createPotions();

		// hp, mp, atk, def, magic, mres, speed, crit, special attack
		readMonsters();
	}


	private static void mapMoves() {
		//could split different class abilities
		Abilities.addAttack(Move.BASIC,   BasicAttack::new);
		Abilities.addAttack(Move.BURST,   FlameBurst::new);
		Abilities.addAttack(Move.CHARGE,  ChargeAttack::new);
		Abilities.addAttack(Move.DISRUPT, Disrupt::new);
		Abilities.addAttack(Move.DRAIN,   LifeDrain::new);
		Abilities.addAttack(Move.FREEZE,  Freeze::new);
		Abilities.addAttack(Move.FRENZY,  Frenzy::new);
		Abilities.addAttack(Move.POLY,    Polymorph::new);
		Abilities.addAttack(Move.POSSESS, Possess::new);
		Abilities.addAttack(Move.REFLECT, Reflect::new);
		Abilities.addAttack(Move.REVENGE, Revenge::new);
		Abilities.addAttack(Move.SHEEP,   SheepAttacks::new);
		Abilities.addAttack(Move.SHIFT,   ChangeForm::new);
		Abilities.addAttack(Move.SHOCK,   Shock::new);
		Abilities.addAttack(Move.SPIN,    SpinAttack::new);

		Abilities.addPassive(Move.INTIM,  Intimidate::new);
		Abilities.addPassive(Move.RAGE,   Rage::new);

	}


	private static void createPotions() {
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
		}

	}

	public static Ability createAbility(Move name, Monster user) { //wrapper for getting and apply
		return Abilities.getAttack(name).apply(user);
	}
	public static Ability createPassive(Move name, Monster user) {
		return Abilities.getPassive(name).apply(user);
	}

	public static Potion getPotion(int id) { return potions.get(id); }
	public static Potion getPotion(String name) { return potions.get(name); }


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
