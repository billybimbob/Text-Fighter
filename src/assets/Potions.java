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
	public void useItem (Monster user) {
		user.modStat(this.statMod, this.modVal);

		turnStart = Interface.FIGHT.getTurnNum();
		if (!(statMod.equals("hp") || statMod.equals("mp")))
			System.out.println(user.getName() + " has used " + this.name + " and gained " + modVal + " " + statMod);
		else
			System.out.println(user.getName() + " has used " + this.name + " and gained " + modVal + " " + statMod +" \nand will also gain " + statMod + " over time");
	}

	public static void buffCheck (Hero user) { //Checks if buff wears off/ updates healing over time only for hero
		Items used = user.getPick();
		
		if (used.statMod.equals("hp") || used.statMod.equals("mp")){ //Gain over time, could be better, scattered between here and useItem method
			user.modStat(used.statMod, used.modVal);

			float max;
			switch (used.statMod) {
			case "hp":
				max = user.getStat("maxHp");
				if (user.getStat(used.statMod) > max) {
					user.setStat(used.statMod, max);
					System.out.println("You cannot be healed past max health");
				}
				break;
			case "mp":
				max = user.getStat("maxMp");
				if (user.getStat(used.statMod) > max) {
					user.setStat(used.statMod, max);
					System.out.println("You cannot gain past max mana");
				}
				break;
			}
			System.out.println("You gain " + used.modVal + " " + used.statMod + " from the " + used.name);
			used.modVal = used.baseModVal;
		} else if (Potions.timeLength <= Math.abs(Interface.FIGHT.getTurnNum()-Potions.turnStart)) {
			user.modStat(used.statMod, -used.modVal);
			System.out.println(used.name + " has worn off");
			user.setStatus("potion", false);

		}
	}
}
