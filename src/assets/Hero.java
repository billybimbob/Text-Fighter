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
			moveList = new Ability[] {	getAbility("basic"), getAbility("charg"), 
										getAbility("disrt"), getAbility("spins") };

			attType = true;
			setStat(Stat.MAXHP, 40.0f); //try iterating later
			setStat(Stat.HP, getStat(Stat.MAXHP));
			setStat(Stat.ATT, 7.0f);
			setStat(Stat.MAG, 1.0f);
			setStat(Stat.DEF, 7.0f);
			break;
			
		case 2: //mage
			moveList = new Ability[] {	getAbility("basic"), getAbility("shock"),
										getAbility("drain"), getAbility("froze"),
										getAbility("polym"), getAbility("reflt") };

			attType = false;
			setStat(Stat.MAG, 7.0f);
			setStat(Stat.ATT, 1.0f);
			setStat(Stat.MAGR, 7.0f);
			setStat(Stat.DEF, 4.0f);
			setStat(Stat.MAG, 7.0f);
			break;

		case 3: //shifter
			moveList = new Ability[] {getAbility("shift")};
			attType = false;
			setStat(Stat.HP, 10.0f);
			setStat(Stat.MAXHP, getStat(Stat.HP));
			setStat(Stat.ATT, 1.0f);
			setStat(Stat.MAG, 1.0f);
			setStat(Stat.DEF, 1.0f);
			setStat(Stat.MAGR, 1.0f);
			setStat(Stat.SPEED, 7.0f);
			setStat(Stat.CRIT, 1.0f);
			break;
		}
		
	}

	public Items getPick() { return pick; }
	
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
					
					super.setTurn(attNum-1); //start at 0th idx
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
					int turnNum = Interface.FIGHT.getTurnNum(); //bad
					String itemPrompt = "Which item do you want to use?";
					int pickNum = Interface.choiceInput(true, inventNames, itemPrompt);
					
					if (pickNum == 0)
						break selection;
					else if (this.checkStatus(Status.POTION, turnNum) > 0) { //will trigger debuff
						String usePrompt = "Another buff is still active, and will be canceled by this potion\nAre you sure you want to do this?";
						int confirmUse = Interface.choiceInput(false, Interface.RESPONSEOPTIONS, usePrompt);
						if (confirmUse == 1) {
							Potions.turnStart = turnNum + Potions.timeLength;
						} else
							break selection;
					}
					pick = inventory.getItem(pickNum-1);
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
			modStat(Stat.MP, -3);
			modStat(Stat.SPEED, 7);
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
