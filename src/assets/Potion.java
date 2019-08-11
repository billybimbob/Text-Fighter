package assets;

import assets.chars.*;
import assets.chars.Equipment.Slot;
import combat.Status;
import main.*;

public class Potion extends Item {
	
	private int duration = 5;
	private boolean overTime;
	
	public Potion (Stat stat) {
		this.slot = Slot.POTION;
		overTime = false;

		int modVal = -1;
		switch (stat) { //maybe add overtime pots
		case HP:
			name = "Health Potion";
			modVal = 2;
			overTime = true;
			break;
		case MP:
			name = "Mana Potion";
			modVal = 2;
			overTime = true;
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

		mods.add(new Item.ModInfo(stat, modVal));
	}
	
	protected void useInfo (Monster user, boolean remove, ModInfo info) {
		int modVal = info.getMod();
		Stat stat = info.getStat();

		if (remove) {
			if (!overTime)
				user.modStat(stat, false, -modVal);
			
			user.setStatus(Status.POTION, false);
			Interface.writeOut(this.name + " has worn off");

		} else {
			boolean start = user.getStatus(Status.POTION) == -1;
			
			if (start) {
				user.setStatus(Status.POTION, duration);
				if (overTime) {
					float capOver = user.modStat(stat, true, modVal);
					Interface.writeOut(user.getName() + " has used " + this.name + " and gained " + (modVal-capOver)
						+ " " + stat + " \nAnd will also gain " + modVal + " " + stat + " over time");

				} else {
					user.modStat(info.getStat(), false, modVal);
					Interface.writeOut(user.getName() + " has used " + this.name + " and gained " + modVal + " " + stat);
				}

			} else if (!start && overTime) { //while active, overTime
				float capOver = user.modStat(info.getStat(), true, modVal);
				//find out how to print past max val
				Interface.writeOut("You gain " + (modVal-capOver) + " " + stat + " from the " + this.name);
			}
		}
	}

}
