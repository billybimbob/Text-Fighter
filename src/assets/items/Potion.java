package assets.items;

import java.util.List;
import java.util.stream.Collectors;

import combat.Status;
import assets.*;
import main.*;

public class Potion extends Item {
	
	private int duration = 5;
	private boolean regen; //regen will not lose buff after gone
	private boolean start;
	
	public Potion(String name, List<Stat> modStats, int modVal, int duration, boolean regen) {
		this.slot = Slot.POTION;
		this.space = 1; //could maybe paramerize
		this.name = name;
		this.duration = duration;
		this.regen = regen;
		modStats.forEach(stat -> mods.add(new ModInfo(stat, modVal)));
	}

	@Override
	protected boolean checkState(Monster user, boolean remove) { //accounts for overtime
		return super.checkState(user, remove) || !remove && using.contains(user); //contains called twice, inefficent
	}

	@Override
	protected void usePrompts() { //override for printing
		if (this.remove) {
			currentUser.setStatus(Status.POTION, false);
			Interface.writeOut(this.getName() + " has worn off");

		} else if (this.start = currentUser.getStatus(Status.POTION)==-1) { //potential issue; potion can't refresh
			currentUser.setStatus(Status.POTION, duration);
			String modNames = this.getModNames();

			if (this.regen)
				Interface.writeOut(currentUser.getName() + " has used " 
					+ this.getName() + " and will gain " + modNames + " over time");
			else
				Interface.writeOut(currentUser.getName() + " has used " + this.getName()
					+ " and gains a boost in " + modNames);
		}
	}
	
	protected void statMod (Stat stat, int modVal) {
		if (this.regen && !this.remove) { //for start and regen
			float capOver = currentUser.modStat(stat, true, modVal);
			Interface.writeOut(currentUser.getName() + " gain " 
				+ (modVal-capOver) + " " + stat + " from the " + this.getName());

		} else if (!this.regen) { //nothing done if not start or remove

			if (this.remove)
				currentUser.modStat(stat, false, -modVal);
			else if (this.start) {
				currentUser.modStat(stat, false, modVal);
				Interface.writeOut(currentUser.getName() + " gained " + modVal + " " + stat);
			}
		}
	}

	private String modStr() {
		return mods.stream()
		.map(mod -> "buffs " + mod.getStat() + " by " + mod.getMod())
		.collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		return name + ": that " + modStr() + " for " + duration + " turns";
	} 

}
