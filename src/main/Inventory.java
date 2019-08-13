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
		return inventoryList.keySet().toArray(Item[]::new);
	}


	public boolean empty() {
		return slotsUsed == 0;
	}
	
	public Item getItem(int idx) {
		Item getting = this.listItems()[idx];
		this.removeItems(getting);
		return getting;
	}

	public void addItems (Item item, int numItems) { //Adds a specified item and amount, need to add control when at max capacity
		int amount = item.getSpace() * numItems;
		int remain = inventSpace-slotsUsed;
		
		int canAdd = remain >= amount
			? amount
			: remain - (remain % item.getSpace());

		ItemInfo info = inventoryList.get(item);
		if (info == null) {
			info = new ItemInfo(canAdd);
			inventoryList.put(item, info);
		} else
			info.addAmount(canAdd);
		
		slotsUsed += canAdd;

		if (canAdd != amount)
			Interface.writeOut("Your inventory is full, and cannot fit " 
				+ (amount-canAdd) + " " + item.getName() + " (s)\n");
	}

	public void removeItems (Item item) { //Removes one item; could change to param amounts
		
		ItemInfo info = inventoryList.get(item);
		if (info == null) {
			Interface.writeOut("Item does not exist");
		} else {
			slotsUsed -= item.getSpace();
			if (info.getAmount() == 1)
				inventoryList.remove(item);
			else
				info.addAmount(-1); //sub by one
		}

	}
	
	public Iterator<Item> iterator() { //Goes through the inventory, accounting for duplicates, and sets each item to an index in an Array
		List<Item> ret = new ArrayList<>();
		
		for (Map.Entry<Item, ItemInfo> entry: inventoryList.entrySet())
			for (int i = 0; i < entry.getValue().getAmount(); i++)
				ret.add(entry.getKey());
		
		return ret.iterator();
	}

	public String[] accessNames() {
		return inventoryList.entrySet().stream()
			.map(entry -> entry.getKey().getName() + " x " + entry.getValue().getAmount())
			.toArray(String[]::new);
	}
	
}