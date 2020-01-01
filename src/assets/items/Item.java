package assets.items;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;

import assets.*;

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

	protected String getModNames() { 
		return mods.stream()
			.map(info -> info.getStat().toString())
			.collect(Collectors.joining(", "));
	}

	/**determines if to remove or not; determines state */
	protected boolean defaultRemove (Monster user) {
		return using.contains(user);
	}

	/**
	 * prints item being used being modifying each stat
	 */
	protected abstract void usePrompts();

	/**
	 * modifies user's individual stat by value
	 */
	protected abstract void statMod (Stat stat, int modVal);
	

	protected boolean checkState(Monster user, boolean remove) {
		boolean contains = using.contains(user);
		boolean slotsUpdated = Equipment.useCheck(user, slot, remove);
		if (!slotsUpdated)
			System.err.println("item can't be used outside of Equipment contex");

		return slotsUpdated && (remove && contains || !remove && !contains);
	}

	/**
	 * modifies user's stats by the Item
	 * @param user monster using the item
	 * @param remove {@code true} if to remove stats from user
	 */
	public final void use (Monster user, boolean remove) { //should not be remove true first
		if (checkState(user, remove)) {
			this.currentUser = user;
			if (this.remove = remove)
				using.remove(user);
			else
				using.add(user);

			usePrompts();
			mods.forEach(info -> statMod(info.getStat(), info.getMod()));
		}
	}

	/**
	 * use method with the default remove value; 
	 * multiple calls of this {@code use} on the same 
	 * Item and user will toggle between
	 * the remove parameter first being off then on
	 * @param user user of the item
	 */
	public final void use (Monster user) {
		use(user, defaultRemove(user));			
	}
	
	public Slot getSlot() { return slot; }
	public int getSpace() { return space; }
	
	@Override
	public int compareTo(Entity other) {
		return this.name.compareTo(other.getName());
	}

}
