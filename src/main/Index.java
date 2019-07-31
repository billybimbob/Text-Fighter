package main;

import java.util.*;
import java.util.function.Function;
import java.io.*;

import assets.*;
import assets.chars.Monster;
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

	private static final Map<Move, Function<Monster, Ability>> attackList = new HashMap<>();
	private static final Map<Move, Function<Monster, Ability>> passiveList = new HashMap<>();
	public static Potions[] potionsList;
	private static final List<Monster> monsterList = new ArrayList<>(); //all monsters
	private static final Map<String, Integer> monIds = new HashMap<>(); //name to list idx
	
	public static void createVals() {
		
		createPotions();
		mapMoves();

		// hp, mp, atk, def, magic, mres, speed, crit, special attack
		readMonsters();
	}

	private static void createPotions() {
		List<Potions> potionsSto = new ArrayList<>();
		for (Stat stat: Stat.values())
			potionsSto.add(new Potions(stat));

		potionsList = potionsSto.toArray(Potions[]::new);
	}

	private static void mapMoves() {
		
		//could split different class abilities
		attackList.put(Move.BASIC,   BasicAttack::new);
		attackList.put(Move.BURST,   FlameBurst::new);
		attackList.put(Move.CHARGE,  ChargeAttack::new);
		attackList.put(Move.DISRUPT, Disrupt::new);
		attackList.put(Move.DRAIN,   LifeDrain::new);
		attackList.put(Move.FREEZE,  Freeze::new);
		attackList.put(Move.FRENZY,  Frenzy::new);
		attackList.put(Move.POLY,    Polymorph::new);
		attackList.put(Move.POSSESS, Possess::new);
		attackList.put(Move.REFLECT, Reflect::new);
		attackList.put(Move.REVENGE, Revenge::new);
		attackList.put(Move.SHEEP,   SheepAttacks::new);
		attackList.put(Move.SHIFT,   ChangeForm::new);
		attackList.put(Move.SHOCK,   Shock::new);
		attackList.put(Move.SPIN,    SpinAttack::new);

		passiveList.put(Move.INTIM,  Intimidate::new);
		passiveList.put(Move.RAGE,   Rage::new);

	}

	private static void readMonsters() {

		try (BufferedReader reading = new BufferedReader(new FileReader("src/assets/chars/monster.txt"));) {
			final boolean defltAggro = false;
			String line;
			while((line = reading.readLine()) != null) {
				String[] tok = line.split(", ");
				final int parseLen = tok.length;

				if (parseLen < 3) //skip line
					continue;

				String name = tok[0];
				boolean attType = Boolean.parseBoolean(tok[1]);
				
				List<Integer> stats = new ArrayList<>();
				for (String numTok: tok[2].split(","))
					stats.add(Integer.parseInt(numTok));

				if (parseLen >= 4) { //has special moves
					List<Move> moves = new ArrayList<>();
					for (String moveTok: tok[3].split(","))
						moves.add(Move.valueOf(moveTok.toUpperCase()));

					if (parseLen >= 5) { //has passive
						Move passive = Move.valueOf(tok[4].toUpperCase());
						monsterList.add(new Monster(name, defltAggro, attType, stats, moves, passive));
					} else
						monsterList.add(new Monster(name, defltAggro, attType, stats, moves));

				} else
					monsterList.add(new Monster(name, defltAggro, attType, stats));

				monIds.put(name, monsterList.size()-1); //should be the list idx
			} 
		} catch (IOException e) {
			System.err.println("Issue reading file");
		}

	}

	public static Ability createAbility(Move name, Monster user) { //wrapper for getting and apply
		return attackList.get(name).apply(user);
	}
	public static Ability createPassive(Move name, Monster user) {
		return passiveList.get(name).apply(user);
	}


	public static Monster getMonBase(int id) {
		return monsterList.get(id);
	}
	public static Monster getMonBase(String name) {
		return monsterList.get( monIds.get(name) );
	}

	public static Monster createMonster(int id) {
		return new Monster( getMonBase(id) );
	}
	public static Monster createMonster(String name) {
		return new Monster( getMonBase(name) );
	}

	public static Monster randomMonster() {
		int id = (int)(Math.random()*monsterList.size());
		return createMonster(id);
	}
}
