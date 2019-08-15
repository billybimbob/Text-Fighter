package main;

import java.util.*;
import assets.Item;

public class Inventory implements Iterable<Item> {
	
	private static class ItemInfo {
		private int amount;

		public ItemInfo(int amount) {
			this.amount = amount;
		}

		public int getAmount() { return amount; }
		public void addAmount(int amount) {
			this.amount += amount;
		}
	}

	private int inventSpace, slotsUsed;
	private Map<Item, ItemInfo> inventoryList; //Inventory space limit of 25
	
	public Inventory() {
		inventSpace = 25;
		slotsUsed = 0;
		inventoryList = new TreeMap<>(); //to make sorted
	}
	public Inventory(int space) {
		this();
		inventSpace = space;
	}

	private Item[] listItems() {
		return inventoryList.keySet().toArray(Item[]::new); //same order as toString because of TreeMap
	}
	private void removeItem (Item item) { //removes one item; could change to param amounts
		
		ItemInfo info = inventoryList.get(item);
		if (info == null)
			System.err.println("Item does not exist");
		else {
			slotsUsed -= item.getSpace();
			if (info.getAmount() == 1)
				inventoryList.remove(item);
			else
				info.addAmount(-1); //sub by one
		}

	}


	public boolean empty() {
		return slotsUsed == 0;
	}
	
	public Item getItem(int idx) {
		Item getting = this.listItems()[idx];
		this.removeItem(getting);
		return getting;
	}

	public void addItems (Item item, int numItems) { //Adds a specified item and amount, need to add control when at max capacity
		int amount = item.getSpace() * numItems;
		int remain = inventSpace-slotsUsed;
		
		int canAdd = remain >= amount
			? numItems
			: remain / item.getSpace();

		ItemInfo info = inventoryList.get(item);
		if (info == null) {
			info = new ItemInfo(canAdd);
			inventoryList.put(item, info);
		} else
			info.addAmount(canAdd);
		
		slotsUsed += (canAdd * item.getSpace());

		if (canAdd != numItems)
			Interface.writeOut("Your inventory is full, and cannot fit " 
				+ (numItems-canAdd) + " " + item.getName() + " (s)\n");
	}

	
	public Iterator<Item> iterator() { //Goes through the inventory, accounting for duplicates, and sets each item to an index in an Array
		List<Item> ret = new ArrayList<>();
		
		for (Map.Entry<Item, ItemInfo> entry: inventoryList.entrySet())
			for (int i = 0; i < entry.getValue().getAmount(); i++)
				ret.add(entry.getKey());
		
		return ret.iterator();
	}

	/**
	 * returns array of string representation of the items in the inventory
	 */
	public String[] accessNames() {
		return this.toString().split("\n");
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		for (Map.Entry<Item, ItemInfo> entry: inventoryList.entrySet())
			str.append(entry.getKey().getName() 
				+ " x " + entry.getValue().getAmount() + "\n");
		
		return str.toString();
	}
	
}