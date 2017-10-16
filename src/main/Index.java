package main;

import assets.*;
import combat.*;

public class Index {

	public static Attacks[] attackList;
	public static Potions[] potionsList;
	public static Monsters[] monsterList;
	
	public Index() {
		Potions hpPotion = new Potions("hp"); //Temporary
		Potions mpPotion = new Potions("mp");
		Potions atPotion = new Potions("att");
		Potions dfPotion = new Potions("def");
		Potions crPotion = new Potions("crit");
		Potions evPotion = new Potions("eva");
		Potions spPotion = new Potions("spe");
		Potions[] potStore = {hpPotion, mpPotion, atPotion, dfPotion, crPotion, evPotion, spPotion};
		potionsList = potStore;
		
		BasicAttack baseAtt = new BasicAttack();
		ChargeAttack charge = new ChargeAttack();
		SpinAttack spin = new SpinAttack();
		MagicBlast blast = new MagicBlast();
		Freeze froze = new Freeze();
		Attacks[] attStore = {baseAtt, charge, spin, blast, froze};
		attackList = attStore;
		
		// lvl ,hp, mp,  atk, def, magic, mr, crit, eva, speed, spcial attack
		Monsters mon1 = new Monsters("Bandit", false, true, 1, 15, 3, 3, 3, 3, 6, 6, 6, 1);
		Monsters mon2 = new Monsters("Spider", false, false, 15, 15, 6, 2, 3, 3, 4, 4, 4, 4);
		Monsters mon3 = new Monsters("Slime", false, false, 20, 10, 3, 5, 3, 3, 1, 1, 1, 3);
		Monsters[] monStore = {mon1, mon2, mon3};
		monsterList = monStore;
	}
	public void use () {
		
	}
}
