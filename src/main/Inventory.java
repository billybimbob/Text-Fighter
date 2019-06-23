package main;

import java.util.*;
import assets.Items;

public class Inventory implements Iterable<Items> {
	
	private static class ItemInfo {
		private Items item;
		private int amount;

		public ItemInfo(Items item, int amount) {
			this.item = item;
			this.amount = amount;
		}
		public Items getItem() { return item; }
		public int getAmount() { return amount; }
		public void addAmount(int amount) {
			this.amount += amount;
		}
	}
	private static int inventSpace = 25, slotsUsed = 0;
	private static Map<String, ItemInfo> inventoryList; //Inventory space limit of 25, can increase with cloning
	
	public Inventory() {
		inventSpace = 25;
		slotsUsed = 0;
		inventoryList = new HashMap<>();
	}
	public Inventory(int space) {
		this();
		inventSpace = space;
	}

	public boolean empty() {
		return slotsUsed == 0;
	}
	
	public Items getItem(int idx) {
		Items getting = this.accessItems()[idx];
		this.removeItems(getting);
		return getting;
	}

	public void addItems (Items added, int amount) { //Adds a specified item and amount, need to add control when at max capacity
		int remain = inventSpace-slotsUsed;
		int canAdd = remain >= amount ? amount : remain;

		ItemInfo info = inventoryList.get(added.name);
		if (info == null)
			info = new ItemInfo(added, canAdd);
		else
			info.addAmount(canAdd);;
		
			slotsUsed += canAdd;

		if (canAdd != amount)
			Interface.writeOut("Your inventory is full, and cannot fit " + (amount-canAdd) + " " + added.name + " (s)\n");
	}

	public void removeItems (Items remove) { //Removes one item
		String name = remove.name;
		ItemInfo info = inventoryList.get(name);

		if (info == null) {
			Interface.writeOut("Item does not exist");
		} else {
			slotsUsed -= 1;
			int val = info.getAmount();
			if (val == 0) {
				inventoryList.remove(name);
			} else {
				info.addAmount(-1);; //sub by one
			}
		}

	}
	
	public Iterator<Items> iterator() { //Goes through the inventory, accounting for duplicates, and sets each item to an index in an Array
		List<Items> ret = new ArrayList<>();
		
		for (Map.Entry<String, ItemInfo> entry: inventoryList.entrySet())
			for (int i = 0; i < entry.getValue().getAmount(); i++)
				ret.add(entry.getValue().getItem());
		
		return ret.iterator();
	}

	private Items[] accessItems() { //convert to array
		List<Items> ret = new ArrayList<>();
		
		for (Map.Entry<String, ItemInfo> entry: inventoryList.entrySet())
			for (int i = 0; i < entry.getValue().getAmount(); i++)
				ret.add(entry.getValue().getItem());

		/*for (Items item: this) //acall iterator
			ret.add(item);*/

		return ret.toArray(new Items[ret.size()]);
	}

	public String[] accessNames() {
		List<String> names = new ArrayList<>();

		for (Map.Entry<String, ItemInfo> entry: inventoryList.entrySet())
			names.add(entry.getKey() + " x " + entry.getValue().getAmount());

		return names.toArray(new String[names.size()]);
	}
	
}