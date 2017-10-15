package main;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import combat.*;

public class Fight {
	
	private static final ArrayList<String> fightChoices = new ArrayList<>(Arrays.asList("Fight", "Inventory", "Flee"));
	public static int turnCount;
	public static boolean potionBuff = false;

	public static void fighting (Scanner keyboard, ArrayList<Monsters> fighters) throws InterruptedException {
		System.out.println("Press enter when you are ready to fight");
		keyboard.nextLine();
		
		Monsters target = null;
		boolean fightControl = true, flee = false;
		turnCount = 0;
		Items pick = null;
		int choice = 2, pickNum = 0;
		Attacks turnMove = null;
		ArrayList<Monsters> heroTargets = new ArrayList<>(); //need to clear later
		
		while (fightControl) {
			ArrayList<String> monFightersName = new ArrayList<>();
			ArrayList<Monsters> monFighters = new ArrayList<>();
			
			Fight.attackOrder(fighters); //Orders the fighters by speed
			System.out.println("-----------------------------------------------");
			for (int i = 0; i <= fighters.size()-1; i++) { //Determine which is the hero, may change later, also prints each fighter and stats
				System.out.println(fighters.get(i).name + " - " + fighters.get(i).hp + " hp" + " - " + fighters.get(i).mp + " mp");
				
				if (fighters.get(i).aggro) {
					target = fighters.get(i);
				} else {
					monFighters.add(fighters.get(i));
					monFightersName.add(fighters.get(i).name);
				}
			}
			while (!Interface.heroAction){ //Hero user input/determine hero actions
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
							
							heroTargets.clear();
							if (turnMove.aoe || turnMove.numTar == monFighters.size()) { //attacks all monsters if aoe attack or if only one option
								for (int i = 0; i <= monFighters.size()-1; i++)
									heroTargets.add(monFighters.get(i));
								Interface.heroAction = true;
							} else { //single target attacks
								for (int i = 0; i <= turnMove.numTar-1; i++) {
									String tarPrompt = "Which monster would you want to target?";
									int tarNum = Interface.choiceInput(keyboard, true, monFightersName, tarPrompt);
									if (tarNum == 0) {//have to change how to implement
										Interface.heroAction = false;
										break;
									}
									heroTargets.add(monFighters.get(tarNum-1));
									Interface.heroAction = true;
								}
							}
						} while (!Interface.heroAction);
						break;
					case 2: //Check inventory
						ArrayList<String> inventNames = Inventory.access();
						if (Inventory.empty) {
							System.out.println("You have no items in your inventory\n");
						} else {
							String itemPrompt = "Which item do you want to use?";
							pickNum = Interface.choiceInput(keyboard, true, inventNames, itemPrompt);
							if (pickNum == 0)
								break selection;
							else if (potionBuff && Potions.timeLength >= (turnCount-Potions.turnStart)) { //Will trigger debuff
								String usePrompt = "Another buff is still active, and will be canceled by this potion\nAre you sure you want to do this?";
								int confirmUse = Interface.choiceInput(keyboard, false, Interface.responseOptions, usePrompt);
								if (confirmUse == 1)
									Potions.turnStart = turnCount+Potions.timeLength;
								else
									break selection;
							}
							Interface.heroAction = true;
						}
						break;
					case 3: //temporarily raises evasion
						Interface.hero.eva += 5;
						Interface.heroAction = true;
				}
			}
			System.out.println("-----------------------------------------------");
			
			for (int i = 0; i <= fighters.size()-1; i++) { //Goes through the move of each fighter
				Monsters attacker = fighters.get(i);
				if (target.hp <= 0 || flee)
					break;
				else if (attacker.stun) { //checks if fighter is stunned, if so, skips turn
					System.out.println(attacker.name + " is stunned");
					attacker.stun = false;
					
				} else if (!attacker.aggro) { //Monster attacks
					int monMoveNum = (int)(Math.random()*attacker.moveList.length);
					Attacks monMove = null;
					if (attacker.skip) {
						monMove = attacker.moveList[attacker.store];
						attacker.store = 0;
					} else
						monMove = attacker.moveList[monMoveNum];
					attacker.store = monMoveNum; //does previous turn move
					monMove.setTarget(target); //doesn't account for multiple targets, maybe do rng to select other targets?
					monMove.execute();
					
				} else { //Hero action
					Interface.heroAction = false; //sets default value, will by default ask for user input
					if (potionBuff)
						Potions.buffCheck (target, pick);
					switch (choice) {
						case 1: //attacks inputed target
							for (int j = 0; j <= turnMove.numTar-1; j++) {
								if (j < heroTargets.size())
									turnMove.setTarget(heroTargets.get(j));
								else
									turnMove.setTarget(null);
							}
							turnMove.execute();
							break;
						case 2: //use inputed item
							int inventIndex = 0; //this part is here in order to account for potion overrides
							for (int j = 0; j < pickNum-1; j++) //Not great, searching for index multiple times
								inventIndex += Inventory.inventoryList[inventIndex].numAmount;
							
							pick = Inventory.inventoryList[inventIndex];
							potionBuff = true;
							pick.useItem(attacker);
							break;
						case 3: //Try to flee
							double escapeCheck = Math.random() + (attacker.spe*0.1-monFighters.get(0).spe*0.1); //Escape check based on speed of hero, against fastest enemy, and RNG
							if (escapeCheck > 1)
								flee = true;
							else
								System.out.println("You fail to escape");
							Interface.hero.eva += 5;
					}
					for (int j = 0; j < monFighters.size(); j++) { //check if any monster died
						if (monFighters.get(j).hp <= 0) {
							if (j < i)
								i--;
							fighters.remove(monFighters.get(j));
							System.out.println("\n" + monFighters.get(j).name + " has died");
							monFighters.remove(monFighters.get(j));
						}
					}
					
				}
				attacker.mp += 1;
				System.out.println("");
				TimeUnit.SECONDS.sleep(2);
			}
			turnCount++; //turn counter
			
			if (target.hp <= 0) { //Check if hero hp is zero
				System.out.println("You have received a fatal blow, and have died");
				fightControl = false;
			} else if (monFighters.size() == 0) { //Check if all monsters are killed
				System.out.println("All of the monsters have been killed, you win!");
				fightControl = false;
			} else if (flee) {
				System.out.println("You have successfully escaped");
				fightControl = false;
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
}