package main;

import java.util.*;
import java.util.function.Function;
import java.io.*;

import assets.*;
import combat.Ability;
import combat.moves.*;
import combat.moves.magic.*;
import combat.moves.magic.shapeshift.*;
import combat.moves.melee.*;
import combat.moves.passive.*;

public class Index {

	public static enum Move { 
		BASIC, CHARGE, DISRUPT, SPIN, SHOCK, DRAIN, FREEZE, POLY, REFLECT, SHIFT, SHEEP, //attacks
		FLURRY, INTIM; //pasives
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
		createMonsters();
	}

	private static void createPotions() {
		List<Potions> potionsSto = new ArrayList<>();
		for (Stat stat: Stat.values())
			if (stat != Stat.MAXHP && stat != Stat.MAXMP)
				potionsSto.add(new Potions(stat));

		potionsList = potionsSto.toArray(new Potions[potionsSto.size()]);
	}

	private static void mapMoves() {

		//could split different class abilities
		attackList.put(Move.BASIC, (Monster user) -> new BasicAttack(user));
		attackList.put(Move.CHARGE, (Monster user) -> new ChargeAttack(user));
		attackList.put(Move.DISRUPT, (Monster user) -> new Disrupt(user));
		attackList.put(Move.SPIN, (Monster user) -> new SpinAttack(user));
		attackList.put(Move.SHOCK, (Monster user) -> new Shock(user));
		attackList.put(Move.DRAIN, (Monster user) -> new LifeDrain(user));
		attackList.put(Move.FREEZE, (Monster user) -> new Freeze(user));
		attackList.put(Move.POLY, (Monster user) -> new Polymorph(user));
		attackList.put(Move.REFLECT, (Monster user) -> new Reflect(user));
		attackList.put(Move.SHEEP, (Monster user) -> new SheepAttacks(user));
		attackList.put(Move.SHIFT, (Monster user) -> new ChangeForm(user));
		
		passiveList.put(Move.FLURRY, (Monster user) -> new Flurry(user));
		passiveList.put(Move.INTIM, (Monster user) -> new Intimidate(user));
	}

	private static void createMonsters() {
		try (BufferedReader reading = new BufferedReader(new FileReader("monster.txt"));) {
			String line;
			while((line = reading.readLine()) != null) {
				String[] tok = line.split(", ");

				String name = tok[0];
				boolean attType = Boolean.parseBoolean(tok[1]);
				
				List<Integer> stats = new ArrayList<>();
				for (String numTok: tok[2].split(","))
					stats.add(Integer.parseInt(numTok));

				List<Move> moves = new ArrayList<>();
				for (String moveTok: tok[3].split(","))
					moves.add(Move.valueOf(moveTok.toUpperCase()));

				monsterList.add(new Monster(name, false, attType, stats, moves));
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

	public static Monster getMonster(int id) {
		return monsterList.get(id);
	}
	public static Monster getMonster(String name) {
		return monsterList.get( monIds.get(name) );
	}
}
