package main;

import assets.*;
import combat.*;

public class Index {

	public static Attacks[] attackList;
	public static Potions[] potionsList;
	public static Monsters[] monsterList;
	
	public Index() {
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
		
		Attacks baseA = new BasicAttack();
		Attacks charg = new ChargeAttack(); //start of melee abilites, 1
		Attacks disrt = new Disrupt();
		Attacks spins = new SpinAttack();
		Attacks blast = new MagicBlast();   //start of magic abilites, 4
		Attacks drain = new LifeDrain();
		Attacks froze = new Freeze();
		Attacks reflt = new Reflect();
		Attacks[] attStore = {baseA, charg, disrt, spins, blast, drain, froze, reflt};
		attackList = attStore;
		
		// lvl ,hp, mp, atk, def, magic, mres, speed, crit, special attack
		Monsters mon1 = new Monsters("Bandit", false, true, 15, 15, 3, 3, 3, 3, 6, 6, 1);
		Monsters mon2 = new Monsters("Spider", false, false, 10, 15, 6, 2, 3, 3, 4, 4, 5);
		Monsters mon3 = new Monsters("Slime", false, true, 20, 10, 3, 5, 3, 3, 1, 1, 6);
		Monsters[] monStore = {mon1, mon2, mon3};
		monsterList = monStore;
	}
	public void use () { //dummy method
		
	}
}
