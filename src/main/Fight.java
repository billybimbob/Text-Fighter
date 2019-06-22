package main;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import combat.*;
import combat.magic.shapeshift.*;

public class Fight {
	
	private static final String[] FIGHTCHOICES = {"Fight", "Dodge", "Inventory"};
	private static Items pick; //temporary
	public static int turnCount;
	public static List<Monster> fighters;

	public static void fighting (List<Monster> fightersIn) throws InterruptedException {
		fighters = fightersIn;
		Monster target = null;
		boolean fightControl = true, flee = false;
		turnCount = 0;
		pick = null;
		List<Monster> monFighters = new ArrayList<>();
		String[] monFightersName;
		
		Interface.writeOut("Press enter when you are ready to fight");
		Interface.confirm();

		while (fightControl) {
			monFighters.clear(); //not sure, might be ineffcient
			monFightersName = null;
			turnCount++; //turn counter
			
			attackOrder(fighters); //Orders the fighters by speed
			determineEnemies(monFighters);

			monFightersName = new String[monFighters.size()];
			for (int i = 0; i < monFighters.size(); i++)
				monFightersName[i] = monFighters.get(i).getName();

			heroChoice(monFightersName);

			//decides the turns of the Monster
			for (Monster mon: monFighters) {
				mon.setTurn();
				mon.addTarget(target);
			}
			
			priorities();
			
			//Goes through the move of each fighter, if attacking, target set here
			Monster attacker;
			for (int i = 0; i < fighters.size(); i++) {
				
				runTurn(fighters.get(i), monFighters);

				for (int j = 0; j < monFighters.size(); j++) { //check if any monster died, immediately after hero's turn
					//System.out.print(monFighters.get(j).getName() + j + " " + monFighters.size());
					if (monFighters.get(j).getStat("hp") <= 0) {
						if (j < i)
							i--;
						fighters.remove(monFighters.get(j));
						Interface.writeOut("\n" + monFighters.get(j).getName() + " has died");
						monFighters.remove(j--);
					}
				}
			}

			
			if (monFighters.size() == 0) { //Check if all Monster are killed
				Interface.writeOut("All of the Monster have been killed, you win!");
				fightControl = false;
			} else if (target.getStat("hp") <= 0) { //Check if hero hp is zero
				Interface.writeOut("You have received a fatal blow, and have died");
				fightControl = false;
			/*} else if (flee) {
				Interface.writeOut("You have successfully escaped");
				fightControl = false;*/
			}
		}
	}
	
	public static void attackOrder (List<Monster> list) { //orders the combatants from highest speed to lowest
		int count = 0;
		while (count < list.size()-1) {
			if (list.get(count).getStat("spe") < list.get(count+1).getStat("spe")) {
				
				for (int i = list.size()-1; i >= 0; i--) {
					if (list.get(count).getStat("spe") < list.get(i).getStat("spe")) {
						list.add(i, list.remove(count));
						count = 0;
						break;
					}
				}
			} else {
				count += 1;
			}
		}
	}
	public static void determineEnemies(List<Monster> monFighters) {
		StringBuilder lstFighters = new StringBuilder();
		lstFighters.append(Interface.LINESPACE);
		for (Monster fighter: fighters) { //Determine which is the hero, may change later, also prints each fighter and stats
			lstFighters.append(fighter);
			
			if (fighter.getAggro()) {
				//target = fighter;
			} else {
				monFighters.add(fighter);
			}
		}
		Interface.writeOut(lstFighters.toString());}

	public static void priorities() {
		//check for priority, need to check what happens if speed is same with 2 priorities
		for (int src = 0; src < fighters.size(); src++) {
			Monster priAttacker = fighters.get(src);

			if (priAttacker.getPriority() && src != 0) { //if priority and first, no need to move
				// pastPriMon = 0; //dst is location of where to swap, pastPriorMon is the number of priority Monster past
				for (int dst = 0; dst < src; dst++) { //finds where to switch, as highest speed priority is 1st
					Monster priCheck = fighters.get(dst);
					if (!priCheck.getPriority() || (priCheck.getPriority() && (priCheck.getStat("spe") < priAttacker.getStat("spe")))) {	
						fighters.add(dst, fighters.remove(src)); //moves mon to dst, and scoots down rest
						break;
					}
				}
			}
		}
	}

	public static void runTurn(Monster attacker, List<Monster> enemies) {
		boolean skipTurn = false;
		if (attacker.getStat("hp") <= 0) //got rid of flee, maybe temporary
			return;
		
		passiveCheck(attacker);
		statusCheck(attacker, "burn");
		statusCheck(attacker, "potion"); //not sure if should be end of turn or beginning
		statusCheck(attacker, "stun");
		
		
		if (!skipTurn && !attacker.getAggro()) { //Monster attacks
			//might be wrong attack since priority order different
			attacker.executeTurn(); //doesn't account for multiple targets, maybe do rng to select other targets?

		} else if (!skipTurn && attacker.getAggro()){ //Hero action, attacks target set here or then targets somehow get overridden
			heroTurn(choice, enemies.toArray(new Monster[enemies.size()]));
		}

		attacker.clearTurn();

		//end of turn
		statusCheck(attacker, "poison");
		statusCheck(attacker, "reflect");
		statusCheck(attacker, "shapeshift");
		
		attacker.resetDamage();
		if (attacker.getStat("mp") < attacker.getStat("maxMp")) //passive mp gain, could change the val
			attacker.modStat("mp", 1);

		Interface.writeOut();
		TimeUnit.SECONDS.sleep(2);

	}

	public static void heroChoice(List<Monster> targets) {
		//Hero user input/determine hero actions
		String[] monNames = new String[targets.size()];
		for (int i = 0; i<targets.size(); i++)
			monNames[i] = targets.get(i).getName();

		while (!Interface.heroAction) {
			
			String fightPrompt = "Which action would you like to do?";
			int choice = Interface.choiceInput(false, FIGHTCHOICES, fightPrompt);
			selection:
			switch (choice) {
			case 1: //Attack a prompted target
				do { //probably change, flow is really bad and confusing
					String attPrompt = "Which attack do you want to use?";
					int attNum = Interface.choiceInput(true, Interface.hero.getMoveNames(), attPrompt); //Temporary
					if (attNum == 0)
						break selection;
					
					Interface.hero.setTurn(attNum);
					//determine the targets of hero move
					int numTar = Interface.hero.getNumTar();
					Interface.heroAction = true;
					for (int j = 0; j < numTar; j++) { //gets targets if needed
						String tarPrompt = "Which monster would you want to target?";
						int tarNum = Interface.choiceInput(true, monNames, tarPrompt);
						if (tarNum == 0) {//have to change how to implement
							Interface.heroAction = false;
							break;
						}
						Interface.hero.addTarget(targets.get(tarNum));
					}

				} while (!Interface.heroAction);

				break;
			case 2: //temporarily raises evasion, and costs 2 mana
				Interface.heroAction = true;
				break;
			case 3: //Check inventory
				String[] inventNames = Inventory.access();
				if (Inventory.empty) {
					Interface.writeOut("You have no items in your inventory\n");
				} else {
					String itemPrompt = "Which item do you want to use?";
					int pickNum = Interface.choiceInput(true, inventNames, itemPrompt);
					if (pickNum == 0)
						break selection;
					else if (Interface.hero.getStatus("potion").getStart()!=0 && Potions.timeLength >= (turnCount-Potions.turnStart)) { //will trigger debuff
						String usePrompt = "Another buff is still active, and will be canceled by this potion\nAre you sure you want to do this?";
						int confirmUse = Interface.choiceInput(false, Interface.RESPONSEOPTIONS, usePrompt);
						if (confirmUse == 1) {
							Potions.turnStart = turnCount+Potions.timeLength;
						} else
							break selection;
					}
					Interface.heroAction = true;
				}
			}
		}
		Interface.writeOut(Interface.LINESPACE);
	}

	public static void heroTurn(int choice, Monster[] targets) {
		Monster attacker = Interface.hero;
		Interface.heroAction = false; //sets default value, will by default ask for user input
		if (flee)
			attacker.modStat("spe", -7);
		switch (choice) {
		case 1: //attacks inputed target
			attacker.executeTurn();
			break;
		case 2: //Try to flee
			//double escapeCheck = Math.random() + (attacker.spe*0.1-monFighters.get(0).spe*0.1); //Escape check based on speed of hero, against fastest enemy, and RNG
			Interface.hero.modStat("mp", -3);
			Interface.hero.modStat("spe", 7);
			Interface.writeOut("You try dodge all incoming attacks, increasing evasion by 7");
			flee = true;
			/*if (escapeCheck > 1)
				flee = true;
			else
				Interface.writeOut("You fail to escape");*/
			break;
		case 3: //use inputed item
			int inventIndex = 0; //this part is here in order to account for potion overrides
			for (int j = 0; j < pickNum-1; j++) //Not great, searching for index multiple times
				inventIndex += Inventory.inventoryList[inventIndex].numAmount;
			
			pick = Inventory.inventoryList[inventIndex];
			attacker.setStatus("potion", true);
			pick.useItem(attacker);
		}
			
	}
		
	public static void statusCheck (Monster checking, String statusName) { //each turn effects
		Monster.StatusInfo data = checking.getStatus(statusName);
		int startTurn = data.getStart(), duration = data.getStart(); //statTurn is turn activated
		switch(statusName) {
		case "burn":
			if (startTurn != 0) {
				int burnDam = (int)(checking.getStat("hp")*0.1);
				checking.setStat("hp", -burnDam);
				Interface.writeOut(checking.getName() + " is burned, and takes " + burnDam + " damage");
				if (turnCount-startTurn == duration) {
					checking.setStatus("burn", false);
					Interface.writeOut(checking.getName() + " is no longer burned");
				}
			}
			break;
		case "poison":
			if (startTurn != 0) {
				int poiDam = (int)(checking.getStat("hp")*0.01*((turnCount-startTurn)%10));
				checking.setStat("hp", -poiDam);
				Interface.writeOut(checking.getName() + " is poisoned, and takes " + poiDam + " damage");
			}
			break;
		case "potion":
			if (startTurn != 0) { //triggered only by player
				Potions.buffCheck (checking, pick);
			}
			break;
		case "reflect":
			if (startTurn != 0 && turnCount-startTurn >= duration) {
				checking.setStatus("reflect", false);
				Interface.writeOut(checking.getName() + "'s reflect has worn off");
			}
			break;
		case "shapeshift":
			if (startTurn != 0 && turnCount-startTurn >= duration) { //triggered by shapeshift
				ShapeShift.revert(checking);
				Interface.writeOut(checking.getName() + " reverted back");
			}
			break;
		case "stun":
			if (startTurn != 0) { //triggered by chargeatt, magblast, disrupt
				Interface.writeOut(checking.getName() + " is stunned");
				skipTurn = true;
				checking.setStatus("stun", false);
			}
			break;
		}
	}
	public static void statusCheck(Monster attacker, Monster target, String statusName, double damDeal) { //target based status checks, might move to Monster class
		Monster.StatusInfo data = attacker.getStatus(statusName);
		int startTurn = data.getStart(), duration = data.getStart(); //statTurn is turn activated
		switch (statusName) {
		case "reflect":
			if (startTurn != 0) {
				float refDam = (int)(damDeal*0.5); 
				attacker.modStat("hp", -refDam);
				Interface.writeOut(target.getName() + " reflects " + refDam + " damage to " + attacker.getName());
			}
			if (startTurn != 0 && turnCount-startTurn >= duration) {
				target.setStatus(statusName, false);
				Interface.writeOut(target.getName() + "'s reflect has worn off");
			}
			break;
		}
	}

	public static void passiveCheck (Monster user) { //not sure if should be all enemies or all non-user, inefficient
		List<Monster> nonSelf = new ArrayList<>();
		for (Monster mon: fighters) {
			if (mon != user)
				nonSelf.add(mon);
		}
		user.usePassive(nonSelf.toArray(new Monster[nonSelf.size()])); //have to pass as array, bad, execute will handle if self tar	
	}

}