package assets;

import main.*;

public class Potions extends Items {
	
	public static int turnStart, timeLength = 5;
	
	public Potions (String stat) {
		statMod = stat;
		numAmount = 0;
		switch (stat) {
			case "hp":
				name = "Health Potion";
				modVal = 2;
				baseModVal = modVal;
				break;
			case "mp":
				name = "Mana Potion";
				modVal = 2;
				baseModVal = modVal;
				break;
			case "att":
				name = "Potion of Offense";
				modVal = 7;
				break;
			case "def":
				name = "Ironskin Potion";
				modVal = 10;
				break;
			case "mag":
				name = "Potion of Elements";
				modVal = 7;
				break;
			case "magR":
				name = "Element Barrier Potion";
				modVal = 10;
				break;
			case "spe":
				name = "Swiftness Potion";
				modVal = 10;
				break;
			case "crit":
				name = "Lucky Potion";
				modVal = 10;
				break;
		}
	}
	public void useItem (Monsters user) {
		switch (statMod) {
			case "hp":
				user.att += modVal*2;
				break;
			case "mp":
				user.mp +=  modVal*2;
				break;
			case "att":
				user.att += modVal;
				break;
			case "def":
				user.def += modVal;
				break;
			case "mag":
				user.mag += modVal;
				break;
			case "magR":
				user.magR += modVal;
				break;
			case "spe":
				user.spe += modVal;
				break;
			case "crit":
				user.crit += modVal;
				break;
		}
		turnStart = Fight.turnCount;
		Inventory.removeItems(this);
		if (!(statMod.equals("hp") || statMod.equals("mp")))
			System.out.println(user.name + " has used " + this.name + " and gained " + modVal + " " + statMod);
		else
			System.out.println(user.name + " has used " + this.name + " and gained " + modVal*2 + " " + statMod +" \nand will also gain " + statMod + " over time");
	}
	public static void buffCheck (Monsters user, Items used) { //Checks if buff wears off/ updates healing over time
		if (Potions.timeLength <= Math.abs(Fight.turnCount-Potions.turnStart)) {
			switch (used.statMod) {
			case "att":
				user.att -= used.modVal;
				break;
			case "def":
				user.def -= used.modVal;
				break;
			case "mag":
				user.mag -= used.modVal;
				break;
			case "magR":
				user.magR -= used.modVal;
				break;
			case "spe":
				user.spe -= used.modVal;
				break;
			case "crit":
				user.crit -= used.modVal;
				break;
			}
			System.out.println(used.name + " has worn off");
			user.setStatus("potion", false);
		} else if (used.statMod.equals("hp") || used.statMod.equals("mp")){ //Gain over time, could be better, scattered between here and useItem method
			switch (used.statMod) {
				case "hp":
					double tempHp = user.hp;
					user.hp += used.modVal;
					if (user.hp > user.maxHp) {
						user.hp = user.maxHp;
						used.modVal = (int)(user.hp - tempHp);
						System.out.println("You cannot be healed past max health");
					}
					break;
				case "mp":
					double tempMp = user.mp;
					user.mp += used.modVal;
					if (user.mp > user.maxMp) {
						user.mp = user.maxMp;
						used.modVal = (int)(user.mp - tempMp);
						System.out.println("You cannot gain past max mana");
					}
					break;
			}
			System.out.println("You gain " + used.modVal + " " + used.statMod + " from the " + used.name);
			used.modVal = used.baseModVal;
		}
	}
}
