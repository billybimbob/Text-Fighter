package assets;

import java.util.ArrayList;
import java.util.List;

import assets.chars.Equipment;
import assets.chars.Monster;
import assets.chars.Equipment.Slot;

public abstract class Item implements Comparable<Item> {

	protected static class ModInfo {
		private Stat stat;
		private int mod;

		ModInfo(Stat stat, int mod){
			this.stat = stat;
			this.mod = mod;
		}

		Stat getStat() { return stat; }
		int getMod() { return mod; }
	}

	protected String name;
	protected Equipment.Slot slot;
	protected List<ModInfo> statMod;


	protected Item() {
		statMod = new ArrayList<>();
	}

	protected int qualityTier (String tier, int modVal) {
		switch (tier) {
			case "basic":
				modVal += 5;
				break;
			case "greater":
				modVal += 10;
				break;
			case "ultimate":
				modVal += 15;
				break;
		}
		return modVal;
	}
	
	protected void useInfo(Monster user, ModInfo info, boolean remove) {
		int modVal = remove ? -info.mod : info.mod;
		user.modStat(info.stat, false, modVal); //want to mod max
	}

	public String getName() { return name; }
	public Slot getSlot() { return slot; }

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null
			&& this.getClass().equals(other.getClass())
			&& this.name.equals( ((Item)other).name );
	}

	@Override
	public int compareTo(Item other) {
		return this.name.compareTo(other.name);
	}

	public void use (Monster user, boolean remove) {
		for (ModInfo info: statMod)
			useInfo(user, info, remove);
	}


}
