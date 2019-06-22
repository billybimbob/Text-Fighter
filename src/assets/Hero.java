package assets;

import combat.*;
import java.util.List;
import main.*;

public class Hero extends Monster {

	private int choice;
	private int pickNum;
	private boolean action;
	
	public Hero (String name, int classes) { //if classes true, warrior
		super(name, true, true, new float[]{25, 20, 5, 5, 5, 5, 5, 5});
		level = 1;
		try { //redfine from starting vals
			switch(classes) {
			case 1: //warrior
				Ability[] moveStore1 = {
						Index.attackList[0].clone(this), Index.attackList[1].clone(this),
						Index.attackList[2].clone(this), Index.attackList[3].clone(this)};
				moveList = moveStore1;
				attType = true;
				setStat("maxHp", 40.0f); //try iterating later
				setStat("hp", getStat("maxHp"));
				setStat("att", 7.0f);
				setStat("mag", 1.0f);
				setStat("def", 7.0f);
				break;
			case 2: //mage
				Ability[] moveStore2 = {
						Index.attackList[0].clone(this), Index.attackList[4].clone(this),
						Index.attackList[5].clone(this), Index.attackList[6].clone(this),
						Index.attackList[7].clone(this), Index.attackList[8].clone(this)};
				moveList = moveStore2;
				attType = false;
				setStat("mag", 7.0f);
				setStat("att", 1.0f);
				setStat("magR", 7.0f);
				setStat("def", 4.0f);
				setStat("mag", 7.0f);
				break;
			case 3: //shifter
				Ability[] moveStore3 = {Index.attackList[10].clone(this)};
				moveList = moveStore3;
				attType = false;
				setStat("hp", 10.0f);
				setStat("maxHp", getStat("hp"));
				setStat("att", 1.0f);
				setStat("mag", 1.0f);
				setStat("def", 1.0f);
				setStat("magR", 1.0f);
				setStat("spe", 7.0f);
				setStat("crit", 1.0f);
				break;
			}
			
		} catch (CloneNotSupportedException c) {}
	}

	
	public void fightChoice(List<Monster> targets) {
		//Hero user input/determine hero actions
		String[] monNames = new String[targets.size()];
		for (int i = 0; i<targets.size(); i++)
			monNames[i] = targets.get(i).getName();

		while (!action) {
			
			String fightPrompt = "Which action would you like to do?";
			choice = Interface.choiceInput(false, Fight.FIGHTCHOICES, fightPrompt);
			selection:
			switch (choice) {
			case 1: //Attack a prompted target
				do { //probably change, flow is really bad and confusing
					String attPrompt = "Which attack do you want to use?";
					int attNum = Interface.choiceInput(true, this.getMoveNames(), attPrompt); //Temporary
					if (attNum == 0)
						break selection;
					
					this.setTurn(attNum);
					//determine the targets of hero move
					int numTar = this.getNumTar();
					action = true;
					for (int j = 0; j < numTar; j++) { //gets targets if needed
						String tarPrompt = "Which monster would you want to target?";
						int tarNum = Interface.choiceInput(true, monNames, tarPrompt);
						if (tarNum == 0) {//have to change how to implement
							action = false;
							break;
						}
						this.addTarget(targets.get(tarNum));
					}

				} while (!action);

				break;
			case 2: //temporarily raises evasion, and costs 2 mana
				action = true;
				break;
			case 3: //Check inventory
				String[] inventNames = Inventory.access();
				if (Inventory.empty) {
					Interface.writeOut("You have no items in your inventory\n");
				} else {
					String itemPrompt = "Which item do you want to use?";
					pickNum = Interface.choiceInput(true, inventNames, itemPrompt);
					if (pickNum == 0)
						break selection;
					else if (this.getStatus("potion").getStart()!=0 && Potions.timeLength >= (Fight.turnCount-Potions.turnStart)) { //will trigger debuff
						String usePrompt = "Another buff is still active, and will be canceled by this potion\nAre you sure you want to do this?";
						int confirmUse = Interface.choiceInput(false, Interface.RESPONSEOPTIONS, usePrompt);
						if (confirmUse == 1) {
							Potions.turnStart = Fight.turnCount+Potions.timeLength;
						} else
							break selection;
					}
					action = true;
				}
			}
		}
		Interface.writeOut(Interface.LINESPACE);
	}

	@Override
	public void executeTurn() {
		action = false; //sets default value, will by default ask for user input

		switch (choice) {
		case 1: //attacks inputed target
			executeTurn();
			break;
		case 2: //Try to flee
			//double escapeCheck = Math.random() + (attacker.spe*0.1-monFighters.get(0).spe*0.1); //Escape check based on speed of hero, against fastest enemy, and RNG
			modStat("mp", -3);
			modStat("spe", 7);
			Interface.writeOut("You try dodge all incoming attacks, increasing evasion by 7");
			break;
		case 3: //use inputed item
			int inventIndex = 0; //this part is here in order to account for potion overrides
			for (int j = 0; j < pickNum-1; j++) //Not great, searching for index multiple times
				inventIndex += Inventory.inventoryList[inventIndex].numAmount;
			
			Items pick = Inventory.inventoryList[inventIndex];
			setStatus("potion", true);
			pick.useItem(this);
		}
	}
}
