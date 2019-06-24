package main;

import java.util.*;
import java.util.function.Function;

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
	public static Monster[] monsterList;
	public static Monster[] shiftMonList;
	
	public static void createVals() {
		List<Potions> potionsSto = new ArrayList<>();
		for (Stat stat: Stat.values())
			if (stat != Stat.MAXHP && stat != Stat.MAXMP)
				potionsSto.add(new Potions(stat));

		potionsList = potionsSto.toArray(new Potions[potionsSto.size()]);
		

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
		
		// hp, mp, atk, def, magic, mres, speed, crit, special attack
		Monster mon1 = new Monster("Bandit", false, true, new float[]{15, 15, 3, 3, 3, 3, 6, 6}, Move.CHARGE);
		Monster mon2 = new Monster("Spider", false, false, new float[]{10, 15, 6, 2, 3, 3, 4, 4}, Move.DRAIN);
		Monster mon3 = new Monster("Slime", false, true, new float[]{20, 10, 3, 5, 3, 3, 1, 1}, Move.FREEZE);
		Monster mon4 = new Monster("Eagle", true, true, new float[]{15, 15, 8, 2, 2, 2, 18, 5}, Move.DISRUPT); //temporary, should add shapeshifter constructor
		Monster mon5 = new Monster("Pangolin", true, true, new float[]{30, 15, 2, 10, 1, 10, 3, 5}, Move.CHARGE);
		Monster mon6 = new Monster("Salamander", true, false, new float[]{25, 15, 1, 5, 5, 6, 5, 5}, Move.DRAIN);
		Monster mon7 = new Monster("Sheep", false, true, new float[]{5, 5, 1, 1, 1, 1, 1, 1}, Move.SHEEP);
		monsterList = new Monster[]{mon1, mon2, mon3, mon4, mon5, mon6};
		shiftMonList = new Monster[]{mon4, mon5, mon6, mon7};

	}

	public static Ability createAbility(Move name, Monster user) { //wrapper for getting
		return attackList.get(name).apply(user);
	}
	public static Ability createPassive(Move name, Monster user) {
		return passiveList.get(name).apply(user);
	}
}
