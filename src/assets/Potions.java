package assets;

import main.*;

public class Potions extends Items {
	
	public static int turnStart, timeLength = 5;
	
	public Potions (Stat stat) {
		statMod = stat;
		numAmount = 0;
		switch (stat) { //maybe add overtime pots
		case HP:
			name = "Health Potion";
			modVal = 2;
			baseModVal = modVal;
			break;
		case MP:
			name = "Mana Potion";
			modVal = 2;
			baseModVal = modVal;
			break;
		case ATT:
			name = "Potion of Offense";
			modVal = 7;
			break;
		case DEF:
			name = "Ironskin Potion";
			modVal = 10;
			break;
		case MAG:
			name = "Potion of Elements";
			modVal = 7;
			break;
		case MAGR:
			name = "Element Barrier Potion";
			modVal = 10;
			break;
		case SPEED:
			name = "Swiftness Potion";
			modVal = 10;
			break;
		case CRIT:
			name = "Lucky Potion";
			modVal = 10;
			break;
		default:
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
		
		//Gain over time, could be better, scattered between here and useItem method

		float max;
		switch (used.statMod) {
		case HP:	
			user.modStat(used.statMod, used.modVal);
			max = user.getStat(Stat.MAXHP);
			if (user.getStat(used.statMod) > max) {
				user.setStat(used.statMod, max);
				System.out.println("You cannot be healed past max health");
			}
			System.out.println("You gain " + used.modVal + " " + used.statMod + " from the " + used.name);
			used.modVal = used.baseModVal;
			break;
		case MP:
			user.modStat(used.statMod, used.modVal);
			max = user.getStat(Stat.MAXMP);
			if (user.getStat(used.statMod) > max) {
				user.setStat(used.statMod, max);
				System.out.println("You cannot gain past max mana");
			}
			System.out.println("You gain " + used.modVal + " " + used.statMod + " from the " + used.name);
			used.modVal = used.baseModVal;
			break;

		default:
			if (Potions.timeLength <= Math.abs(Interface.FIGHT.getTurnNum()-Potions.turnStart)) {
				user.modStat(used.statMod, -used.modVal);
				System.out.println(used.name + " has worn off");
				user.setStatus(Status.POTION, false);
			}
			break;
		}

		
	}
}
