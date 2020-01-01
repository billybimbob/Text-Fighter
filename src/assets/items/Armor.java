package assets.items;

import java.util.List;
import main.Interface;
import assets.Stat;

public class Armor extends Item {

	private boolean attType; //could make Item attr

	public Armor(String name, Slot slot, boolean attType, int space, List<Stat> modStats, int modVal) {
		super();
		this.name = name;
		this.slot = slot;
		this.attType = attType;
		this.space = space;
		modStats.forEach(stat -> mods.add(new ModInfo(stat, modVal))); //might make modInfo param
	}

	@Override
	protected void usePrompts () {
		if (this.remove)
			Interface.writeOut(currentUser.getName() + " has removed " + this.name);
		else
			Interface.writeOut(currentUser.getName() + " equips " + this.name
				+ " and gains a boost in " + this.getModNames());
	}

	protected void statMod(Stat stat, int mod) {
		int modVal = this.remove ? -mod : mod;
		if (currentUser.getAttType() != this.attType)
			modVal *= 0.5;
		currentUser.modStatMax(stat, modVal);
	}

	public boolean getAttType() { return attType; }
}