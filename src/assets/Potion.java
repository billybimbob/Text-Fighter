package assets;

import java.util.List;
import java.util.stream.Collectors;

import assets.Equipment.Slot;
import combat.Status;
import main.*;

public class Potion extends Item {
	
	private int duration = 5;
	private boolean overTime;
	private boolean start;
	
	public Potion(String name, List<Stat> modStats, int modVal, int duration, boolean overTime) {
		this.slot = Slot.POTION;
		this.space = 1; //could maybe paramerize
		this.name = name;
		this.duration = duration;
		this.overTime = overTime;
		modStats.forEach(stat -> mods.add(new ModInfo(stat, modVal)));
	}

	
	@Override
	public void use(Monster user, boolean remove) { //override for printing

		if (remove) {
			user.setStatus(Status.POTION, false);
			Interface.writeOut(this.getName() + " has worn off");

		} else if (start = user.getStatus(Status.POTION) == -1) { //potential issue; potion can't refresh
			user.setStatus(Status.POTION, duration);

			if (this.overTime) {
				String modNames = mods.stream()
					.map(info -> info.getStat().toString())
					.collect(Collectors.joining(", "));

				Interface.writeOut(user.getName() + " has used " 
					+ this.getName() + " and will gain " + modNames + " over time");
					
			} else
				Interface.writeOut(user.getName() + " has used " + this.getName());
		}
		
		super.use(user, remove);
	}
	
	
	protected void statMod (Stat stat, int modVal) {

		if (this.overTime && !this.remove) { //for start and overTime
			float capOver = currentUser.modStat(stat, true, modVal);
			Interface.writeOut(currentUser.getName() + " gain " 
				+ (modVal-capOver) + " " + stat + " from the " + this.getName());

		} else { //nothing done if not start or remove

			if (this.remove)
				currentUser.modStat(stat, false, -modVal);
			
			else if (this.start) {
				currentUser.modStat(stat, false, modVal);
				Interface.writeOut(currentUser.getName() + " and gained " + modVal + " " + stat);
			}
		}
	}

}
