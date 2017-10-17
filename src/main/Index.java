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
		
		BasicAttack baseAtt = new BasicAttack();
		ChargeAttack charge = new ChargeAttack();
		SpinAttack spin = new SpinAttack();
		MagicBlast blast = new MagicBlast(); //start of magic abilites
		Freeze froze = new Freeze();
		Attacks[] attStore = {baseAtt, charge, spin, blast, froze};
		attackList = attStore;
		
		// lvl ,hp, mp, atk, def, magic, mres, speed, crit, special attack
		Monsters mon1 = new Monsters("Bandit", false, true, 1, 15, 3, 3, 3, 3, 6, 6, 1);
		Monsters mon2 = new Monsters("Spider", false, false, 15, 15, 6, 2, 3, 3, 4, 4, 4);
		Monsters mon3 = new Monsters("Slime", false, false, 20, 10, 3, 5, 3, 3, 1, 1, 3);
		Monsters[] monStore = {mon1, mon2, mon3};
		monsterList = monStore;
	}
	public void use () { //dummy method
		
	}
}
