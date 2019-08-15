package assets;

import java.util.List;

import combat.*;
import main.*;

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
		action = this.getTurnMove() != null;

		while (!action) {
			final String fightPrompt = "Which action would you like to do?";
			choice = Interface.choiceInput(false, Fight.FIGHTCHOICES, fightPrompt);
			
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
			int attNum = Interface.choiceInput(true, this.getMoves(), attPrompt);
			if (attNum == 0)
				return;
			
			this.setTurnMove(attNum-1); //start at 0th idx
			Interface.writeOut("Move selected: " + this.getTurnMove().getName() + "\n");
			
			//determine the targets of hero move
			action = true;
			if (checkAutoTar(possTargets))
				continue;
				
			int numOptions = possTargets.size();
			while (numOptions-possTargets.size() < this.getNumTar()) { //loop until amount selected enough

				String[] monNames = possTargets.stream().map(Monster::getName).toArray(String[]::new);
				final String tarPrompt = "Which monster would you want to target?";
				int tarNum = Interface.choiceInput(true, monNames, tarPrompt);
				
				if (tarNum == 0) {//have to change how to implement
					action = false;
					this.setTurnMove(-1); //set to null
					possTargets.addAll(this.targets);
					this.targets.clear();
					break;
				}
				this.targets.add(possTargets.remove(tarNum-1));
			}

		} while (!action);
	}

	private void pickItem() {
		if (inventory.empty()) {
			Interface.writeOut("You have no items in your inventory\n");
			return;
		}

		String[] inventNames = inventory.accessNames();
		final String itemPrompt = "Which item do you want to use?";
		int pickNum = Interface.choiceInput(true, inventNames, itemPrompt);
		
		if (pickNum == 0)
			return;

		if (this.getStatus(Status.POTION) > 0) { //potin still active
			final String usePrompt = "Another buff is still active, "
				+ "and will be canceled by this potion"
				+ "\nAre you sure you want to do this?";

			int confirmUse = Interface.choiceInput(false, Interface.RESPONSEOPTIONS, usePrompt);
			if (confirmUse == 2) //could change to string; or stay in function
				return;
		}

		item = inventory.getItem(pickNum-1);
		action = true;
	}

	private void viewEquipment() {
		String[] equipList = Equipment.showEquipped(this);
		final String unequipPrompt = "Select a slot to unequip an item";
		do {
			int pickNum = Interface.choiceInput(true, equipList, unequipPrompt);
			if (pickNum == 0)
				return;

			slot = Equipment.numToSlot(pickNum-1);

			if (Equipment.checkSlot(this, slot) == null) {
				Interface.writeOut("No item is in the selected slot");
				continue;
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
				Interface.writeOut("You try dodge all incoming attacks, increasing evasiveness");
				break;

			case 3: //use inputed item
				Equipment.equip(this, item); //can't remove item
				item = null;
				break;

			case 4: //unequip item
				Equipment.unequip(this, slot);
				slot = null;
				break;
		}
	}

	public void addItems (Item added, int amount) {
		inventory.addItems(added, amount);
	}

	public void addItem (Item added) {
		addItems(added, 1);
	}
}
