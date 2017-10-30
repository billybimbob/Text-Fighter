package main;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import combat.*;
import combat.magic.shapeshift.*;

public class Fight {
	
	private static final String[] fightChoices = {"Fight", "Dodge", "Inventory"};
	public static int turnCount;
	private static boolean skipTurn;
	private static Items pick; //temporary

	public static void fighting (Scanner keyboard, ArrayList<Monsters> fighters) throws InterruptedException {
		System.out.println("Press enter when you are ready to fight");
		keyboard.nextLine();
		
		Monsters target = null;
		boolean fightControl = true, flee = false;
		turnCount = 0;
		pick = null;
		int choice = 2, pickNum = 0;
		Ability turnMove = null;
		ArrayList<Monsters> heroTargets = new ArrayList<>(); //need to clear later
		
		while (fightControl) {
			String[] monFightersName;
			ArrayList<Monsters> monFighters = new ArrayList<>();
			
			turnCount++; //turn counter
			
			attackOrder(fighters); //Orders the fighters by speed
			System.out.println("-----------------------------------------------");
			for (int i = 0; i <= fighters.size()-1; i++) { //Determine which is the hero, may change later, also prints each fighter and stats
				System.out.println(fighters.get(i).name + " - " + fighters.get(i).hp + " hp" + " - " + fighters.get(i).mp + " mp" + " - " + fighters.get(i).spe+ " speed");
				
				if (fighters.get(i).aggro) {
					target = fighters.get(i);
				} else {
					monFighters.add(fighters.get(i));
				}
			}
			monFightersName = new String[monFighters.size()];
			for (int i = 0; i <= monFighters.size()-1; i++) {
				monFightersName[i] = monFighters.get(i).name;
			}
			
			//Hero user input/determine hero actions
			while (!Interface.heroAction){
				
				Interface.hero.moveListNames = new String[Interface.hero.moveList.length]; //updates the moveListNames of hero
				for (int i = 0; i <= Interface.hero.moveListNames.length-1; i++)
					Interface.hero.moveListNames[i] = Interface.hero.moveList[i].getName() + " - " + (int)Interface.hero.moveList[i].getCost() + " mana";
				
				String fightPrompt = "Which action would you like to do?";
				choice = Interface.choiceInput(keyboard, false, fightChoices, fightPrompt);
				selection:
				switch (choice) {
					case 1: //Attack a prompted target
						do { //probably change, flow is really bad and confusing
							String attPrompt = "Which attack do you want to use?";
							int attNum = Interface.choiceInput(keyboard, true, Interface.hero.moveListNames, attPrompt); //Temporary
							if (attNum == 0)
								break selection;
							
							turnMove = Interface.hero.moveList[attNum-1];
							if (turnMove.getPriority()) //check if attack is priority
								Interface.hero.priority = true;
							heroTargets.clear();
							
							//determine the targets of hero move
							if (turnMove.getSelfTar()){
								Interface.heroAction = true; //temporary, want to select transformation here
							} else if (turnMove.getAoe()) {//attacks all monsters, might change later
								turnMove.setNumTar(monFighters.size());
								//turnMove.targets = new Monsters[turnMove.numTar];
								for (int i = 0; i <= monFighters.size()-1; i++)
									heroTargets.add(monFighters.get(i));
								Interface.heroAction = true;
							} else if (turnMove.getTargets().length == monFighters.size()) { //attacks all monsters if aoe attack or if only one option
								for (int i = 0; i <= monFighters.size()-1; i++)
									heroTargets.add(monFighters.get(i));
								Interface.heroAction = true;
							} else { //single target attacks
								for (int i = 0; i <= turnMove.getTargets().length-1; i++) {
									String tarPrompt = "Which monster would you want to target?";
									int tarNum = Interface.choiceInput(keyboard, true, monFightersName, tarPrompt);
									if (tarNum == 0) {//have to change how to implement
										Interface.heroAction = false;
										break;
									}
									heroTargets.add(monFighters.get(tarNum-1));
									//System.out.println(turnMove.targets[i].name);
									Interface.heroAction = true;
								}
							}
						} while (!Interface.heroAction);
						break;
					case 2: //temporarily raises evasion, and costs 2 mana
						Interface.heroAction = true;
						break;
					case 3: //Check inventory
						String[] inventNames = Inventory.access();
						if (Inventory.empty) {
							System.out.println("You have no items in your inventory\n");
						} else {
							String itemPrompt = "Which item do you want to use?";
							pickNum = Interface.choiceInput(keyboard, true, inventNames, itemPrompt);
							if (pickNum == 0)
								break selection;
							else if (Interface.hero.status[2][0]!=0 && Potions.timeLength >= (turnCount-Potions.turnStart)) { //will trigger debuff
								String usePrompt = "Another buff is still active, and will be canceled by this potion\nAre you sure you want to do this?";
								int confirmUse = Interface.choiceInput(keyboard, false, Interface.responseOptions, usePrompt);
								if (confirmUse == 1)
									Potions.turnStart = turnCount+Potions.timeLength;
								else
									break selection;
							}
							Interface.heroAction = true;
						}
				}
			}
			//decides the turns of the monsters
			int[] monMoves = new int[monFighters.size()];
			for (int i = 0; i <= monFighters.size()-1; i++) {
				if (!(monFighters.get(i).multTurn || monFighters.get(i).status[3][0] != 0)) { //change later
					monMoves[i] = (int)(Math.random()*monFighters.get(i).moveList.length);
					//System.out.println(monMoves[i]);
					if (monFighters.get(i).moveList[monMoves[i]].getPriority())
						monFighters.get(i).priority = true;
				}
			}
			System.out.println("-----------------------------------------------");
			
			//check for priority, need to check what happens if speed is same with 2 priorities
			boolean pastHero = false;
			for (int i = 0; i <= fighters.size()-1; i++) {
				Monsters priorAttacker = fighters.get(i);
				if (priorAttacker.aggro) {
					pastHero = true;
				}
				if (priorAttacker.priority && i != 0) { //if priority and first, no need to move
					if (fighters.get(i-1).priority) {
						//priorCount++;
						break;
					}
					Monsters temp = null, temp2;
					int swapCount, pastPriorMon = 0; //swapCount is location of where to swap, pastPriorMon is the number of priority monsters past
					for (swapCount = 0; swapCount <= fighters.size()-1; swapCount++) { //finds where to switch, as highest speed priority is 1st
						Monsters priorCheck = fighters.get(swapCount);
						//System.out.println(priorCheck.name+priorCheck.spe);
						if (!priorCheck.priority || (priorCheck.priority && (priorCheck.spe < priorAttacker.spe))) {
							
							if (priorCheck.priority && !priorCheck.aggro) { //swaps the monster attack in move list associated with priority shifts
								//System.out.println("mehe");
								pastPriorMon++;
							}
							temp = priorCheck;
							fighters.set(swapCount, priorAttacker);
							if (!priorAttacker.aggro) {
								int swapAttr = i, stoMove = monMoves[pastPriorMon], stoMove2;
								if (pastHero) {
									//System.out.println("bleh");
									swapAttr -= 1;
								}
								//System.out.println(swapAtt + " " + swapAtt2);
								monMoves[pastPriorMon] = monMoves[swapAttr];
								
								for (int j = pastPriorMon+1; j <= swapAttr; j++) {
									stoMove2 = monMoves[j];
									//System.out.println("looping"+j+" "+stoMove+" "+stoMove2);
									monMoves[j] = stoMove;
									stoMove = stoMove2;
								}
							}
							break;
						}
					}
					for (int j = swapCount+1; j <= fighters.size()-1; j++) { //switches priority and scoots down rest behind
						temp2 = fighters.get(j);
						//System.out.println(temp2.name);
						fighters.set(j, temp);
						if (j == i)
							break;
						temp = temp2;
					}
				}
			}
			/*for (int i = 0; i <= monMoves.length-1; i++) {
				System.out.println(monMoves[i]);
			}
			for (int i = 0; i <= monFighters.size()-1; i++) {
				System.out.print(monFighters.get(i).name);
				for (int j = 0; j <= monFighters.get(i).moveList.length-1; j++) {
					System.out.print(monFighters.get(i).moveList[j].name+" ");
				}
				System.out.println("");
			}*/
			
			//Goes through the move of each fighter, if attacking, target set here
			int monCount = 0;
			for (int i = 0; i <= fighters.size()-1; i++) {
				Monsters attacker = fighters.get(i);
				skipTurn = false;
				if (target.hp <= 0) //got rid of flee, maybe temporary
					break;
				
				//status effect check of each monster
				/*for (int j = 0; j <= attacker.status.length-1; j++) {
					int statTurn = attacker.status[j][0], duration = attacker.status[j][1];
				}*/
				statusCheck(attacker, "passive");
				statusCheck(attacker, "burn");
				statusCheck(attacker, "potion");
				statusCheck(attacker, "stun");
				
				if (!(skipTurn || attacker.aggro)) { //Monster attacks
					int monMoveNum = monMoves[monCount]; //might be wrong attack since priority order different
					monCount++;
					Ability monMove = null;
					if (attacker.multTurn) {
						monMove = attacker.moveList[attacker.storeTurn]; //does previous turn move
						attacker.storeTurn = 0;
					} else {
						monMove = attacker.moveList[monMoveNum];
						attacker.storeTurn = monMoveNum;
						monMove.setTarget(target); //doesn't account for multiple targets, maybe do rng to select other targets?
					}
					
					//double startHp = monMove.getTargets()[0].hp;
					monMove.execute();
					//monMove.getTargets()[0].damTurn += (startHp - monMove.getTargets()[0].hp);
					//attacker.damTurn = 0; //resets the amount of damage taken
					
				} else if (!skipTurn && attacker.aggro){ //Hero action, attacks target set here or then targets somehow get overridden
					Interface.heroAction = false; //sets default value, will by default ask for user input
					if (flee)
						attacker.spe -= 7;
					switch (choice) {
						case 1: //attacks inputed target
							if (!turnMove.getSelfTar()) {
								for (int j = 0; j <= turnMove.getTargets().length-1; j++) {
									if (j < heroTargets.size()) {
										turnMove.setTarget(heroTargets.get(j));
										//startHp[j] = heroTargets.get(j).hp;
									} else
										turnMove.setTarget(null);
								}
							}
							turnMove.execute();
							break;
						case 2: //Try to flee
							//double escapeCheck = Math.random() + (attacker.spe*0.1-monFighters.get(0).spe*0.1); //Escape check based on speed of hero, against fastest enemy, and RNG
							Interface.hero.mp -= 3;
							Interface.hero.spe += 7;
							System.out.println("You try dodge all incoming attacks, increasing evasion by 7");
							flee = true;
							/*if (escapeCheck > 1)
								flee = true;
							else
								System.out.println("You fail to escape");*/
							break;
						case 3: //use inputed item
							int inventIndex = 0; //this part is here in order to account for potion overrides
							for (int j = 0; j < pickNum-1; j++) //Not great, searching for index multiple times
								inventIndex += Inventory.inventoryList[inventIndex].numAmount;
							
							pick = Inventory.inventoryList[inventIndex];
							attacker.setStatus("potion", true);
							pick.useItem(attacker);
					}
					for (int j = 0; j <= monFighters.size()-1; j++) { //check if any monster died, immediately after hero's turn
						//System.out.print(monFighters.get(j).name + j + " " + monFighters.size());
						if (monFighters.get(j).hp <= 0) {
							if (j < i)
								i--;
							fighters.remove(monFighters.get(j));
							System.out.println("\n" + monFighters.get(j).name + " has died");
							monFighters.remove(monFighters.get(j));
							j=-1; //probably temporary
						}
					}
				}
				//end of turn
				statusCheck(attacker, "poison");
				statusCheck(attacker, "shapeshift");
				
				attacker.damTurn = 0; //resets the amount of damage taken in one turn
				if (attacker.mp < attacker.maxMp)
					attacker.mp += 1;
				System.out.println("");
				TimeUnit.SECONDS.sleep(2);
			}
			
			if (target.hp <= 0) { //Check if hero hp is zero
				System.out.println("You have received a fatal blow, and have died");
				fightControl = false;
			} else if (monFighters.size() == 0) { //Check if all monsters are killed
				System.out.println("All of the monsters have been killed, you win!");
				fightControl = false;
			/*} else if (flee) {
				System.out.println("You have successfully escaped");
				fightControl = false;*/
			}
		}
	}
	
	public static void attackOrder (ArrayList<Monsters> list) { //orders the combatants from highest speed to lowest
		int count = 0;
		while (count < list.size()-1) {
			if (list.get(count).spe < list.get(count+1).spe) {
				
				for (int i = list.size()-1; i >= 0; i--) {
					if (list.get(count).spe < list.get(i).spe) {
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
	
	public static void statusCheck (Monsters checking, String statName) {
		int stat = Monsters.getStatNum(statName);
		int statTurn = checking.status[stat][0], duration = checking.status[stat][1];
		switch(stat) {
			case 0: //passive ability, should always go first, rest are alphabetized
				if (statTurn != 0) {
					checking.passive.execute();
				}
				break;
			case 1: //burn status
				if (statTurn != 0) {
					int burnDam = (int)(checking.hp*0.1);
					checking.hp -= burnDam;
					System.out.println(checking.name + " is burned, and takes " + burnDam + " damage");
					if (turnCount-statTurn == duration)
						checking.setStatus("burn", false);
				}
				break;
			case 2: //poison status
				if (statTurn != 0) {
					int poiDam = (int)(checking.hp*0.01*((turnCount-statTurn)%10));
					checking.hp -= poiDam;
					System.out.println(checking.name + " is burned, and takes " + poiDam + " damage");
				}
				break;
			case 3: //potion status
				if (statTurn != 0) { //triggered only by player
					Potions.buffCheck (checking, pick);
				}
				break;
			case 4: //shapeshift
				if (statTurn != 0 && turnCount-statTurn >= duration) { //triggered by shapeshift
					ShapeShift.revert(checking);
					System.out.println(checking.name + " reverted back");
				}
				break;
			case 5: //stun status
				if (statTurn != 0) { //triggered by chargeatt, magblast, disrupt
					System.out.println(checking.name + " is stunned");
					skipTurn = true;
					checking.setStatus("stun", false);
				}
				break;
		}
	}
	
}