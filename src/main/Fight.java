package main;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import combat.magic.shapeshift.*;

public class Fight {
	
	public static final String[] FIGHTCHOICES = {"Fight", "Dodge", "Inventory"};

	private int turnCount;
	private List<Monster> fighters;
	private boolean skipTurn;

	public Fight(List<Monster> fighters) {
		turnCount = 0;
		this.fighters = fighters;
	}
	
	public int getTurnNum() { return turnCount; }

	public void start() {
		boolean fightControl = true; //could add flee back
		List<Monster> monFighters = new ArrayList<>();

		while (fightControl) {
			turnCount++; //turn counter
				
			Interface.writeOut(Interface.LINESPACE);

			attackOrder(fighters); //Orders the fighters by speed
			determineEnemies(monFighters);
			Interface.HERO.setTurn(monFighters);

			Interface.writeOut(Interface.LINESPACE);

			//decides the turns of the Monster
			for (Monster mon: monFighters) {
				mon.setTurn();
				mon.addTarget(Interface.HERO);
			}
			
			priorities();
			
			//Goes through the move of each fighter, if attacking, target set here
			for (int i = 0; i < fighters.size(); i++) {
				
				runTurn(fighters.get(i), monFighters); //not sure

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
			} else if (Interface.HERO.getStat("hp") <= 0) { //Check if hero hp is zero
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

	private void determineEnemies(List<Monster> monFighters) {
		monFighters.clear(); //not sure, might be ineffcient
		StringBuilder lstFighters = new StringBuilder();
		
		for (Monster fighter: fighters) { //Determine which is the hero, may change later, also prints each fighter and stats
			lstFighters.append(fighter + "\n");
			
			if (fighter.getAggro()) {
				//target = fighter;
			} else {
				monFighters.add(fighter);
			}
		}
		Interface.writeOut(lstFighters.toString());
	}

	private void priorities() {
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

	private void runTurn(Monster attacker, List<Monster> enemies) {
		skipTurn = false;
		if (attacker.getStat("hp") <= 0) //got rid of flee, maybe temporary
			return;
		
		attacker.usePassive(nonSelf(attacker));
		statusCheck(attacker, "burn");
		statusCheck(attacker, "potion"); //not sure if should be end of turn or beginning
		statusCheck(attacker, "stun");
		
		if (!skipTurn)
			//might be wrong attack since priority order different
			attacker.executeTurn(); //doesn't account for multiple targets, maybe do rng to select other targets?
			//includes heroTurn, overriden


		//end of turn
		statusCheck(attacker, "poison");
		statusCheck(attacker, "reflect");
		statusCheck(attacker, "shapeshift");
		
		attacker.clearTurn();

		if (attacker.getStat("mp") < attacker.getStat("maxMp")) //passive mp gain, could change the val
			attacker.modStat("mp", 1);

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {}
		
		Interface.writeOut();
	}	
	
	private Monster[] nonSelf (Monster user) { //not sure if should be all enemies or all non-user, inefficient
		List<Monster> sto = new ArrayList<>();
		for (Monster mon: fighters) {
			if (mon != user)
				sto.add(mon);
		}
		return sto.toArray(new Monster[sto.size()]);
	}


		
	public void statusCheck (Monster checking, String statusName) { //each turn effects
		
		int check = checking.checkStatus(statusName, turnCount);
		if (check > -1) {
			switch(statusName) {
			case "burn":
				int burnDam = (int)(checking.getStat("hp")*0.1);
				checking.modStat("hp", -burnDam);
				Interface.writeOut(checking.getName() + " is burned, and takes " + burnDam + " damage");
				if (check == 0) {
					checking.setStatus("burn", false);
					Interface.writeOut(checking.getName() + " is no longer burned");
				}
				break;

			case "poison":
				int poiDam = (int)(checking.getStat("hp")*0.01*((turnCount-check)%10));
				checking.modStat("hp", -poiDam);
				Interface.writeOut(checking.getName() + " is poisoned, and takes " + poiDam + " damage");
				break;

			case "potion":
				Hero user = (Hero)checking;
				Potions.buffCheck(user);
				break;

			case "reflect":
				float refDam = (int)(damDeal*0.5); 
				attacker.modStat("hp", -refDam);
				Interface.writeOut(target.getName() + " reflects " + refDam + " damage to " + attacker.getName());
				if (check == 0) { //finished
					checking.setStatus("reflect", false);
					Interface.writeOut(checking.getName() + "'s reflect has worn off");
				}
				break;

			case "shapeshift":
				if (check == 0) { //triggered by shapeshift
					ShapeShift.revert(checking);
					Interface.writeOut(checking.getName() + " reverted back");
				}
				break;

			case "stun":
				//triggered by chargeatt, magblast, disrupt
				Interface.writeOut(checking.getName() + " is stunned");
				skipTurn = true;
				checking.setStatus("stun", false);
				break;
			}
		}
	}


}