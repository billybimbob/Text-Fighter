package main;

import java.util.*;
import assets.Item;

public class Inventory implements Iterable<Item> {
	
	private static class ItemInfo {
		private Item item;
		private int amount;

		public ItemInfo(Item item, int amount) {
			this.item = item;
			this.amount = amount;
		}
		public Item getItem() { return item; }
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
		return inventoryList.entrySet().stream()
			.map(entry -> entry.getValue().getItem())
			.toArray(Item[]::new);
	}

	public boolean empty() {
		return slotsUsed == 0;
	}
	
	public Item getItem(int idx) {
		Item getting = this.listItems()[idx];
		this.removeItems(getting);
		return getting;
	}

	public void addItems (Item added, int amount) { //Adds a specified item and amount, need to add control when at max capacity
		int remain = inventSpace-slotsUsed;
		int canAdd = remain >= amount ? amount : remain;

		ItemInfo info = inventoryList.get(added);
		if (info == null) {
			info = new ItemInfo(added, canAdd);
			inventoryList.put(added, info);
		} else
			info.addAmount(canAdd);
		
		slotsUsed += canAdd;

		if (canAdd != amount)
			Interface.writeOut("Your inventory is full, and cannot fit " 
				+ (amount-canAdd) + " " + added.getName() + " (s)\n");
	}

	public void removeItems (Item remove) { //Removes one item; could change to param amounts
		
		ItemInfo info = inventoryList.get(remove);
		if (info == null) {
			Interface.writeOut("Item does not exist");
		} else {
			slotsUsed -= 1;
			int val = info.getAmount();
			if (val == 1)
				inventoryList.remove(remove);
			else
				info.addAmount(-1); //sub by one
		}

	}
	
	public Iterator<Item> iterator() { //Goes through the inventory, accounting for duplicates, and sets each item to an index in an Array
		List<Item> ret = new ArrayList<>();
		
		for (Map.Entry<Item, ItemInfo> entry: inventoryList.entrySet())
			for (int i = 0; i < entry.getValue().getAmount(); i++)
				ret.add(entry.getValue().getItem());
		
		return ret.iterator();
	}

	public String[] accessNames() {
		return inventoryList.entrySet().stream()
			.map(entry -> entry.getKey().getName() + " x " + entry.getValue().getAmount())
			.toArray(String[]::new);
	}
	
}