package assets;

import combat.Status;
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

		turnStart = Interface.FIGHT.getTurnNum();
		if (statMod.equals(Stat.HP) || statMod.equals(Stat.MP)) {
			user.modStat(this.statMod, true, this.modVal);
			Interface.writeOut(user.getName() + " has used " + this.name + " and gained " + modVal + " " + statMod +" \nand will also gain " + statMod + " over time");
		} else {
			Interface.writeOut(user.getName() + " has used " + this.name + " and gained " + modVal + " " + statMod);	
			user.modStat(this.statMod, false, this.modVal);
		}
	}

	public static void buffCheck (Hero user, boolean finished) { //Checks if buff wears off/ updates healing over time only for hero
		Items used = user.getPick();
		
		//Gain over time, could be better, scattered between here and useItem method

		switch (used.statMod) { //could make switch over something else
		case HP:	
		case MP: //regen pots
			float capOver = user.modStat(used.statMod, true, used.modVal);
			//find out how to print past max val
			Interface.writeOut("You gain " + (used.modVal-capOver) + " " + used.statMod + " from the " + used.name);
			used.modVal = used.baseModVal;
			break;

		default:
			if (finished)
				user.modStat(used.statMod, false, -used.modVal);
			
			break;
		}

		if (finished) {
			Interface.writeOut(used.name + " has worn off");
			user.setStatus(Status.POTION, false);
		}
		
	}
}
