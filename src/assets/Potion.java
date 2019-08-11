package assets;

import java.util.stream.Collectors;

import assets.chars.Monster;
import assets.chars.Equipment.Slot;
import combat.Status;
import main.*;

public class Potion extends Item {
	
	private int duration = 5;
	private boolean overTime;
	private boolean start;
	
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

	@Override
	public void use(Monster user, boolean remove) { //override for printing
		
		if (remove) {
			user.setStatus(Status.POTION, false);
			Interface.writeOut(this.name + " has worn off");

		} else {
			start = user.getStatus(Status.POTION) == -1; //potential issue; potion can't refresh

			if (start) {
				user.setStatus(Status.POTION, duration);

				if (this.overTime) {
					String modNames = mods.stream()
						.map(info -> info.getStat().toString())
						.collect(Collectors.joining(", "));

					Interface.writeOut(user.getName() + " has used " 
						+ this.name + " and will gain " + modNames + " over time");
						
				} else
					Interface.writeOut(user.getName() + " has used " + this.name);
				
			}
		}
		
		super.use(user, remove);
	}
	
	protected void statMod (Stat stat, int modVal) {

		if (this.overTime && !this.remove) { //for start and overTime
			float capOver = currentUser.modStat(stat, true, modVal);
			Interface.writeOut(currentUser.getName() + " gain " 
				+ (modVal-capOver) + " " + stat + " from the " + this.name);
		} else { //nothing done if not start or remove
			
			if (this.remove)
				currentUser.modStat(stat, false, -modVal);
			
			else if (start) {
				currentUser.modStat(stat, false, modVal);
				Interface.writeOut(currentUser.getName() + " and gained " + modVal + " " + stat);
			}
		}
	}

}
