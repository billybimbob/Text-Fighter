package assets;

import java.util.List;
import combat.*;
import main.*;

public class Hero extends Monster {

	private Inventory inventory;
	private int choice;
	private Items pick;
	private boolean action;
	
	public Hero (String name, int classChoice) { //if classes true, warrior
		super(getFightClass(classChoice));
		this.name = name;
		inventory = new Inventory();
		
	}

	private static Monster getFightClass(int option) {
		Monster fightClass = null;
		
		switch(option) {
			case 1: //warrior
				fightClass = Index.getMonBase("Warrior");
				break;
			case 2: //mage
				fightClass = Index.getMonBase("Mage");
				break;
			case 3: //shifter
				fightClass = Index.getMonBase("Shifter");
				break;
		}

		return fightClass;
	}

	public Items getPick() { return pick; }
	
	public void setTurn(List<Monster> targets) { //look at respone idx
		//Hero user input/determine hero actions
		action = false;

		while (!action) {
			String fightPrompt = "Which action would you like to do?";
			choice = Interface.choiceInput(false, Fight.FIGHTCHOICES, fightPrompt);
			
			switch (choice) {
			case 1: //Attack a prompted target
				selectAttack(targets);
				break;

			case 2: //temporarily raises evasion, and costs 2 mana
				action = true;
				break;

			case 3: //Check inventory
				pickItem();
				break;
			}
		}
	}

	private void selectAttack(List<Monster> targets) { //array generate every time
		String[] monNames = new String[targets.size()];
		for (int i = 0; i<targets.size(); i++)
			monNames[i] = targets.get(i).getName();

		do { //probably okay?
			String attPrompt = "Which attack do you want to use?";
			int attNum = Interface.choiceInput(true, this.getMoveNames(), attPrompt);
			if (attNum == 0)
				return;
			
			super.setTurn(attNum-1); //start at 0th idx
			
			//determine the targets of hero move
			int numTar = this.getNumTar();
			action = true;
			for (int i = 0; i < numTar; i++) { //gets targets if needed
				String tarPrompt = "Which monster would you want to target?";
				int tarNum = Interface.choiceInput(true, monNames, tarPrompt);
				if (tarNum == 0) {//have to change how to implement
					action = false;
					break;
				}
				this.addTarget(targets.get(tarNum-1));
			}

		} while (!action);
	}

	private void pickItem() {
		if (inventory.empty()) {
			Interface.writeOut("You have no items in your inventory\n");
			return;
		}

		String[] inventNames = inventory.accessNames();
		int turnNum = currentTurn(); //keep eye on
		String itemPrompt = "Which item do you want to use?";
		int pickNum = Interface.choiceInput(true, inventNames, itemPrompt);
		
		if (pickNum == 0)
			return;

		if (this.checkStatus(Status.POTION) > 0) { //potin still active
			String usePrompt = "Another buff is still active, "
				+ "and will be canceled by this potion"
				+ "\nAre you sure you want to do this?";

			int confirmUse = Interface.choiceInput(false, Interface.RESPONSEOPTIONS, usePrompt);
			if (confirmUse == 2) //could change to string; or stay in function
				return;
		}

		Potions.turnStart = turnNum + Potions.timeLength;
		pick = inventory.getItem(pickNum-1);
		action = true;
	}


	@Override
	public void executeTurn() {
		action = false; //sets default value, will by default ask for user input

		switch (choice) {
		case 1: //attacks inputed target
			super.executeTurn();
			break;

		case 2: //Try to flee
			//double escapeCheck = Math.random() + (attacker.spe*0.1-monFighters.get(0).spe*0.1); //Escape check based on speed of hero, against fastest enemy, and RNG
			setStatus(Status.DODGE, 2);
			Interface.writeOut("You try dodge all incoming attacks, increasing evasion by 7");
			break;

		case 3: //use inputed item
			setStatus(Status.POTION, true);
			pick.useItem(this);
		}
	}

	public void addItems (Items added, int amount) {
		inventory.addItems(added, amount);
	}
}
