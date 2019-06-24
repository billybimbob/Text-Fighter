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

	private static final Map<String, Function<Monster, Ability>> attackList = new HashMap<>();
	private static final Map<String, Function<Monster, Ability>> passiveList = new HashMap<>();
	public static Potions[] potionsList;
	public static Monster[] monsterList;
	public static Monster[] shiftMonList;
	
	public static void createVals() {
		Potions hpPotion = new Potions("hp");  //0, Potion indices
		Potions mpPotion = new Potions("mp");  //1
		Potions atPotion = new Potions("att"); //2
		Potions dfPotion = new Potions("def"); //3
		Potions mgPotion = new Potions("mag"); //4
		Potions mrPotion = new Potions("magR");//5
		Potions spPotion = new Potions("spe"); //6
		Potions crPotion = new Potions("crit");//7
		potionsList = new Potions[]{hpPotion, mpPotion, atPotion, dfPotion, mgPotion, mrPotion, spPotion, crPotion};
		

		//could split different class abilities
		attackList.put("basic", (Monster user) -> new BasicAttack(user));
		attackList.put("charg", (Monster user) -> new ChargeAttack(user));
		attackList.put("disrt", (Monster user) -> new Disrupt(user));
		attackList.put("spins", (Monster user) -> new SpinAttack(user));
		attackList.put("shock", (Monster user) -> new Shock(user));
		attackList.put("drain", (Monster user) -> new LifeDrain(user));
		attackList.put("froze", (Monster user) -> new Freeze(user));
		attackList.put("polym", (Monster user) -> new Polymorph(user));
		attackList.put("reflt", (Monster user) -> new Reflect(user));
		attackList.put("sheep", (Monster user) -> new SheepAttacks(user));
		attackList.put("shift", (Monster user) -> new ChangeForm(user));
		attackList.put("sheep", (Monster user) -> new SheepAttacks(user));
		
		passiveList.put("flury", (Monster user) -> new Flurry(user));
		passiveList.put("intim", (Monster user) -> new Intimidate(user));
		
		// hp, mp, atk, def, magic, mres, speed, crit, special attack
		Monster mon1 = new Monster("Bandit", false, true, new float[]{15, 15, 3, 3, 3, 3, 6, 6}, "charg");
		Monster mon2 = new Monster("Spider", false, false, new float[]{10, 15, 6, 2, 3, 3, 4, 4}, "drain");
		Monster mon3 = new Monster("Slime", false, true, new float[]{20, 10, 3, 5, 3, 3, 1, 1}, "froze");
		Monster mon4 = new Monster("Eagle", true, true, new float[]{15, 15, 8, 2, 2, 2, 18, 5}, "disrt"); //temporary, should add shapeshifter constructor
		Monster mon5 = new Monster("Pangolin", true, true, new float[]{30, 15, 2, 10, 1, 10, 3, 5}, "charg");
		Monster mon6 = new Monster("Salamander", true, false, new float[]{25, 15, 1, 5, 5, 6, 5, 5}, "drain");
		Monster mon7 = new Monster("Sheep", false, true, new float[]{5, 5, 1, 1, 1, 1, 1, 1}, "sheep");
		monsterList = new Monster[]{mon1, mon2, mon3, mon4, mon5, mon6};
		shiftMonList = new Monster[]{mon4, mon5, mon6, mon7};

	}

	public static Ability createAbility(String name, Monster user) { //wrapper for getting
		return attackList.get(name).apply(user);
	}
	public static Ability createPassive(String name, Monster user) {
		return passiveList.get(name).apply(user);
	}
}
