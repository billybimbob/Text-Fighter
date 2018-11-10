package main;

import java.util.Scanner;

import assets.*;
import combat.*;
import combat.magic.*;
import combat.magic.shapeshift.*;
import combat.melee.*;
import combat.passive.*;

public class Index {

	public static Ability[] attackList, passiveList;
	public static Potions[] potionsList;
	public static Monsters[] monsterList;
	public static Monsters[] shiftMonList;
	
	public Index(Scanner keyboard) {
		Potions hpPotion = new Potions("hp");  //0, Potion indices
		Potions mpPotion = new Potions("mp");  //1
		Potions atPotion = new Potions("att"); //2
		Potions dfPotion = new Potions("def"); //3
		Potions mgPotion = new Potions("mag"); //4
		Potions mrPotion = new Potions("magR");//5
		Potions spPotion = new Potions("spe"); //6
		Potions crPotion = new Potions("crit");//7
		Potions[] potStore = {hpPotion, mpPotion, atPotion, dfPotion, mgPotion, mrPotion, spPotion, crPotion};
		potionsList = potStore;
		
		Ability baseA = new BasicAttack();
		Ability charg = new ChargeAttack(); //start of melee abilites, 1
		Ability disrt = new Disrupt();
		Ability spins = new SpinAttack();
		Ability shock = new Shock();   //start of magic abilites, 4
		Ability drain = new LifeDrain();
		Ability froze = new Freeze();
		Ability polym = new Polymorph();
		Ability reflt = new Reflect();
		Ability sheep = new SheepAttacks();
		Ability shift = new ChangeForm(keyboard);
		Ability[] attStore = {baseA, charg, disrt, spins, shock, drain, froze, polym, reflt, sheep, shift};
		attackList = attStore;
		
		Ability flury = new Flurry();
		Ability intim = new Intimidate();
		Ability[] passStore = {flury, intim};
		passiveList = passStore;
		
		// hp, mp, atk, def, magic, mres, speed, crit, special attack
		Monsters mon1 = new Monsters("Bandit", false, true, new float[]{15, 15, 3, 3, 3, 3, 6, 6}, 1);
		Monsters mon2 = new Monsters("Spider", false, false, new float[]{10, 15, 6, 2, 3, 3, 4, 4}, 5);
		Monsters mon3 = new Monsters("Slime", false, true, new float[]{20, 10, 3, 5, 3, 3, 1, 1}, 6);
		Monsters mon4 = new Monsters("Eagle", true, true, new float[]{15, 15, 8, 2, 2, 2, 18, 5}, 2); //temporary, should add shapeshifter constructor
		Monsters mon5 = new Monsters("Pangolin", true, true, new float[]{30, 15, 2, 10, 1, 10, 3, 5}, 1);
		Monsters mon6 = new Monsters("Salamander", true, false, new float[]{25, 15, 1, 5, 5, 6, 5, 5}, 5);
		Monsters mon7 = new Monsters("Sheep", false, true, new float[]{5, 5, 1, 1, 1, 1, 1, 1}, 9);
		Monsters[] monStore = {mon1, mon2, mon3, mon4, mon5, mon6};
		Monsters[] shapeStore = {mon4, mon5, mon6, mon7};
		
		monsterList = monStore;
		shiftMonList = shapeStore;
		
		for (int i = 0; i <= passiveList.length-1; i++) {//temporary
			try {
				shiftMonList[i].setPassive((Ability)passiveList[i].clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}
	public void use () { //dummy method
		
	}
}
