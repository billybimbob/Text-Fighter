package assets;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import assets.Equipment.Slot;

public abstract class Item extends Entity {

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

	protected Slot slot; //think how to handle 2h
	protected int space; //amount of space used up in inventory
	protected List<ModInfo> mods; //can modify multiple stats

	//values that change per use call
	protected Set<Monster> using; //not sure
	protected Monster currentUser; //potential issue with multiple threads
	protected boolean remove;

	
	protected Item() {
		mods = new ArrayList<>();
		using = new HashSet<>();
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


	/**determines if to remove or not; determines state */
	protected boolean defaultRemove (Monster user) {
		return using.contains(user);
	}

	/**
	 * modifies user's individual stat by value
	 */
	protected abstract void statMod (Stat stat, int modVal);
	

	/**
	 * modifies user's stats by the Item
	 * @param user monster using the item
	 * @param remove {@code true} if to remove stats from user
	 */
	protected void use (Monster user, boolean remove) { //not sure if should be public or not
		this.currentUser = user;
		if (this.remove = remove)
			using.remove(user);
		else
			using.add(user);
	
		mods.forEach(info -> statMod(info.getStat(), info.getMod()));
	}

	/**
	 * use method with the default remove value; 
	 * multiple calls of this {@code use} on the same 
	 * Item and user will toggle between
	 * the remove paramter first being off then on
	 * @param user user of the item
	 */
	protected void use (Monster user) {
		use(user, defaultRemove(user));			
	}

	
	public Slot getSlot() { return slot; }
	public int getSpace() { return space; }

	
	@Override
	public int compareTo(Entity other) {
		return this.name.compareTo(other.name);
	}

}
