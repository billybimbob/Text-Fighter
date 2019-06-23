package main;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import combat.magic.shapeshift.*;

public class Fight {
	
	private static boolean skipTurn;
	
	public static final String[] FIGHTCHOICES = {"Fight", "Dodge", "Inventory"};
	private static int turnCount;
	private static List<Monster> fighters;


	public static void fighting (List<Monster> fightersIn) throws InterruptedException {
		fighters = fightersIn;
		boolean fightControl = true; //could add flee back
		turnCount = 0;
		List<Monster> monFighters = new ArrayList<>();
		
		Interface.writeOut("Press enter when you are ready to fight");
		Interface.confirm();

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
			} else if (Interface.HERO.getStat("hp") <= 0) { //Check if hero hp is zero
				Interface.writeOut("You have received a fatal blow, and have died");
				fightControl = false;
			/*} else if (flee) {
				Interface.writeOut("You have successfully escaped");
				fightControl = false;*/
			}
		}
	}

	public static int turnNum() {
		return turnCount;
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

	public static void runTurn(Monster attacker, List<Monster> enemies) throws InterruptedException {
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

		Interface.writeOut();
		TimeUnit.SECONDS.sleep(2);
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
				Hero user = (Hero)checking;
				Potions.buffCheck (user);
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

	public static Monster[] nonSelf (Monster user) { //not sure if should be all enemies or all non-user, inefficient
		List<Monster> sto = new ArrayList<>();
		for (Monster mon: fighters) {
			if (mon != user)
				sto.add(mon);
		}
		return sto.toArray(new Monster[sto.size()]);
	}

}