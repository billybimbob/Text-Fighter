package assets;

import java.util.ArrayList;
import java.util.List;

import combat.*;
import combat.moves.Ability;
import main.Inventory;
import main.Index;
import static main.Interface.*; //not sure if to keep

public class Hero extends Monster {

	private Inventory inventory;
	private int choice;
	private Item item; //not sure
	private Slot slot; //not sure
	private boolean action;
	
	public Hero (String name, int classChoice) { //if classes true, warrior
		super(getFightClass(classChoice));
		this.name = name;
		this.aggro = true;
		this.inventory = new Inventory();
		
	}

	private static Monster getFightClass(int option) { //used only for copy constructor
		Monster fightClass = null;
		
		switch(option) {
			case 1: //warrior
				fightClass = Index.getMonBase("Warrior");
				break;
			case 2: //mage
				fightClass = Index.getMonBase("Mage");
				break;
			case 3: //cleric
				fightClass = Index.getMonBase("Cleric");
				break;
			case 4: //shifter
				fightClass = Index.getMonBase("Shifter");
				break;
		}

		return fightClass;
	}
	
	@Override
	public void setTurn(List<Monster> targets) { //look at respone idx
		//Hero user input/determine hero actions
		if (item != null) { //could change to switch case, happens if turn skipped
			this.addItem(item);
			item = null;
		}
		action = this.getTurnMove() != null;

		while (!action) {
			final String fightPrompt = "Which action would you like to do?";
			choice = choiceInput(false, Fight.FIGHTCHOICES, fightPrompt);
			
			switch (choice) {
				case 1: //Attack a prompted target
					selectAttack(targets);
					break;

				case 2: //temporarily raises evasion
					action = true;
					break;

				case 3: //Check inventory
					pickItem();
					break;

				case 4:
					viewEquipment();
					break;
			}
		}
	}

	private void selectAttack(List<Monster> possTargets) { //array generate every time
		do { //probably okay?
			final String attPrompt = "Which attack do you want to use?";
			int attNum = choiceInput(true, this.getMoves(), attPrompt);
			if (attNum == RESPONSENUM.get("Back"))
				return;
			
			this.setTurnMove(attNum-1); //start at 0th idx
			writeOut("Move selected: " + this.getTurnMove().getName() + "\n");
			
			//determine the targets of hero move
			action = true;
			if (Ability.checkAutoTar(getTurnMove(), possTargets))
				continue;
				
			List<Monster> targets = new ArrayList<>();
			int numOptions = possTargets.size();
			
			while (numOptions-possTargets.size() < this.getNumTar()) { //loop until amount selected enough

				String[] monNames = possTargets.stream().map(Monster::getName).toArray(String[]::new);
				final String tarPrompt = "Which monster would you want to target?";
				int tarNum = choiceInput(true, monNames, tarPrompt);
				
				if (tarNum == RESPONSENUM.get("Back")) {//have to change how to implement
					action = false;
					this.setTurnMove(-1); //set to null
					possTargets.addAll(targets);
					break;
				}
				targets.add(possTargets.remove(tarNum-1));
			}

			if (action)
				this.getTurnMove().setTargets(targets);

		} while (!action);
	}


	private void pickItem() {
		if (inventory.empty()) {
			writeOut("You have no items in your inventory\n");
			return;
		}
		String[] inventNames = inventory.accessNames();
		final String itemPrompt = "Which item do you want to use?";
		
		do {
			int pickNum = choiceInput(true, inventNames, itemPrompt);
			
			if (pickNum == RESPONSENUM.get("Back"))
				return;

			item = inventory.getItem(pickNum-1);
			if (item.getSlot().equals(Slot.POTION) && this.getStatus(Status.POTION) > 0) { //can check all item slots or just pots
				final String usePrompt = "Another buff is still active, "
					+ "and will be canceled by this potion, and"
					+ "\n the old potion will be lost"
					+ "\nAre you sure you want to do this?";

				int confirmUse = choiceInput(false, RESPONSEOPTIONS, usePrompt);
				if (confirmUse == RESPONSENUM.get("No")) {
					this.addItem(item);
					item = null;
					continue;
				}
			}
			action = true;

		} while(!action);
	}

	private void viewEquipment() {
		String[] equipList = Equipment.showEquipped(this);
		final String unequipPrompt = "Select a slot to unequip an item";
		
		do {
			int pickNum = choiceInput(true, equipList, unequipPrompt);
			if (pickNum == RESPONSENUM.get("Back"))
				return;

			slot = Equipment.numToSlot(pickNum-1);

			if (Equipment.checkSlot(this, slot) == null) {
				writeOut("No item is in the selected slot");
				continue;
			
			} else {
				final String confirmPrompt = "Are you sure you want to "
					+ "remove the item at " + slot.getName() + "?";
				
				int confirmUse = choiceInput(false, RESPONSEOPTIONS, confirmPrompt);
				if (confirmUse == RESPONSENUM.get("No")) {
					slot = null;
					continue;
				}
			}

			action = true;

		} while(!action);
	}


	@Override
	public void executeTurn() {

		switch (choice) {
			case 1: //attacks inputed target
				super.executeTurn();
				break;

			case 2: //Try to flee
				//double escapeCheck = Math.random() + (attacker.spe*0.1-monFighters.get(0).spe*0.1); //Escape check based on speed of hero, against fastest enemy, and RNG
				setStatus(Status.DODGE, true);
				writeOut("You try dodge all incoming attacks, increasing evasiveness");
				break;

			case 3: //use inputed item
				reAddItem( Equipment.equip(this, item) ); //can't remove item
				item = null;
				break;

			case 4: //unequip item
				reAddItem( Equipment.unequip(this, slot) );
				slot = null;
				break;
		}
	}

	private void reAddItem(Item item) { //assumed amount 1
		if (item != null && !item.getSlot().equals(Slot.POTION))
			this.addItem(item);
	}

	public void addItems (Item added, int amount) {
		inventory.addItems(added, amount);
	}

	public void addItem (Item added) {
		addItems(added, 1);
	}
}
