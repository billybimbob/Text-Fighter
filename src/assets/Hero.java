package assets;

import java.util.List;
import combat.*;
import main.*;

public class Hero extends Monster {

	private Inventory inventory;
	private int choice;
	private Items pick;
	private boolean action;
	
	public Hero (String name, int classes) { //if classes true, warrior
		super(name, true, true, new float[]{25, 20, 5, 5, 5, 5, 5, 5});
		level = 1;
		inventory = new Inventory();
		
		switch(classes) {
		case 1: //warrior
			Ability[] moveStore1 = {
					Index.attackList.get("basic").apply(this), Index.attackList.get("charg").apply(this),
					Index.attackList.get("disrt").apply(this), Index.attackList.get("spins").apply(this)};
			moveList = moveStore1;
			attType = true;
			setStat("maxHp", 40.0f); //try iterating later
			setStat("hp", getStat("maxHp"));
			setStat("att", 7.0f);
			setStat("mag", 1.0f);
			setStat("def", 7.0f);
			break;
			
		case 2: //mage
			moveList = new Ability[]{
					Index.attackList.get("basic").apply(this), Index.attackList.get("shock").apply(this),
					Index.attackList.get("drain").apply(this), Index.attackList.get("froze").apply(this),
					Index.attackList.get("polym").apply(this), Index.attackList.get("reflt").apply(this)};
			attType = false;
			setStat("mag", 7.0f);
			setStat("att", 1.0f);
			setStat("magR", 7.0f);
			setStat("def", 4.0f);
			setStat("mag", 7.0f);
			break;
		case 3: //shifter
			moveList = new Ability[]{Index.attackList.get("shfit").apply(this),};
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
		
	}

	public Items getPick() {
		return pick;
	}
	
	public void setTurn(List<Monster> targets) { //look at respone idx
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
					int attNum = Interface.choiceInput(true, this.getMoveNames(), attPrompt);
					if (attNum == 0)
						break selection;
					
					super.setTurn(attNum);
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
				String[] inventNames = inventory.accessNames();
				if (inventory.empty())
					Interface.writeOut("You have no items in your inventory\n");
				
				else {
					String itemPrompt = "Which item do you want to use?";
					int pickNum = Interface.choiceInput(true, inventNames, itemPrompt);
					if (pickNum == 0)
						break selection;
					else if (this.getStatus("potion").getStart()!=0 && Potions.timeLength >= (Fight.turnNum()-Potions.turnStart)) { //will trigger debuff
						String usePrompt = "Another buff is still active, and will be canceled by this potion\nAre you sure you want to do this?";
						int confirmUse = Interface.choiceInput(false, Interface.RESPONSEOPTIONS, usePrompt);
						if (confirmUse == 1) {
							Potions.turnStart = Fight.turnNum()+Potions.timeLength;
						} else
							break selection;
					}
					pick = inventory.getItem(pickNum);
					action = true;
				}
			}
		}
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
			modStat("mp", -3);
			modStat("spe", 7);
			Interface.writeOut("You try dodge all incoming attacks, increasing evasion by 7");
			break;

		case 3: //use inputed item
			setStatus("potion", true);
			pick.useItem(this);
		}
	}

	public void addItems (Items added, int amount) {
		inventory.addItems(added, amount);
	}
}
