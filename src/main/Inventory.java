package main;

import java.util.ArrayList;
import assets.Items;

public class Inventory {
	
	public static int inventSpace = 25;
	public static Items[] inventoryList = new Items[inventSpace]; //Inventory space limit of 25, can increase with cloning
	public static boolean empty;
	
	
	public static void addItems (Items added, int amount) { //Adds a specified item and amount, need to add control when at max capacity
		int amntAdded = 0;
		for (int i = 0; i < inventSpace; i++) {
			if (inventoryList[i] == null && amount != 0) {
				//for (int j = 0; j <= added.slotSize-1; j++) {
				inventoryList[i] = added;
				amount--;
				amntAdded++;
				//}
			}
		}
		added.numAmount += amntAdded;
		if (amount != 0)
			System.out.println("Your inventory is full, and cannot fit " + amount + " " + added.name + " (s)\n");
		sortInvent();
	}
	public static void removeItems (Items remove) { //Removes item and gets rid of null gaps
		for (int i = 0; i < inventoryList.length; i++) {
			if (inventoryList[i] != null && remove.name.equals(inventoryList[i].name)) {
				inventoryList[i] = null;
				int gap;
				for (gap = i; gap < inventoryList.length-1; gap++) { //shift all items down
					inventoryList[gap] = inventoryList[gap+1];
				}
				inventoryList[gap] = null;
				remove.numAmount--;
				break;
			}
		}
		sortInvent(); //Might not need this
	}
	private static void sortInvent () { //Sorts the items alphabetically, similar to the attackOrder method, doesn't account for null gaps; called every time item added or removed
		int count = 0;
		while (count < inventoryList.length-1) {
			if ( inventoryList[count+1] != null && inventoryList[count].name.compareTo(inventoryList[count+1].name) > 0) {
				
				for (int i = inventoryList.length-1; i >= 0; i--) {
					if (inventoryList[i] != null && inventoryList[count].name.compareTo(inventoryList[i].name) > 0) {
						Items temp = inventoryList[i];
						inventoryList[i] = inventoryList[count];
						inventoryList[count] = temp;
						count = 0;
						break;
					}
				}
			} else {
				count += 1;
			}
		}
	}
	
	public static String[] access () { //Goes through the inventory, accounting for duplicates, and sets each item to an index in an Array
		empty = true;
		ArrayList<String> inventStore = new ArrayList<>();
		for (int i = 0; i < inventSpace; i++) {
			if (inventoryList[i] != null) {
				inventStore.add(inventoryList[i].name);
				i += inventoryList[i].numAmount-1; //Accounts for the step increment of the for loop
				empty = false;
			}
		}
		String[] inventName = new String [inventStore.size()];
		for (int i = 0; i < inventName.length; i++) {
			inventName[i] = inventStore.get(i) + " x " + inventoryList[i].numAmount;
		}
		
		return inventName;
	}
	
}