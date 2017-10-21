package main;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import combat.*;

public class Fight {
	
	private static final ArrayList<String> fightChoices = new ArrayList<>(Arrays.asList("Attack", "Dodge", "Inventory"));
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
			
			attackOrder(fighters); //Orders the fighters by speed
			System.out.println("-----------------------------------------------");
			for (int i = 0; i <= fighters.size()-1; i++) { //Determine which is the hero, may change later, also prints each fighter and stats
				System.out.println(fighters.get(i).name + " - " + fighters.get(i).hp + " hp" + " - " + fighters.get(i).mp + " mp");
				
				if (fighters.get(i).aggro) {
					target = fighters.get(i);
				} else {
					monFighters.add(fighters.get(i));
					monFightersName.add(fighters.get(i).name);
					if (fighters.get(i).name == "Slime")
						fighters.get(i).priority = true;
				}
			}
			
			//Hero user input/determine hero actions
			while (!Interface.heroAction){
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
							if (turnMove.priority) //check if attack is priority
								Interface.hero.priority = true;
							heroTargets.clear();
							if (turnMove.aoe) {//attacks all monsters, might change later
								turnMove.numTar = monFighters.size();
								turnMove.targets = new Monsters[turnMove.numTar];
								for (int i = 0; i <= monFighters.size()-1; i++)
									heroTargets.add(monFighters.get(i));
								Interface.heroAction = true;
							} else if (turnMove.numTar == monFighters.size()) { //attacks all monsters if aoe attack or if only one option
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
				}
			}
			
			//decides the turns of the monsters
			int[] monMoves = new int[monFighters.size()];
			for (int i = 0; i <= monFighters.size()-1; i++) {
				monMoves[i] = (int)(Math.random()*monFighters.get(i).moveList.length);
				System.out.println(monMoves[i]);
				if (monFighters.get(i).moveList[monMoves[i]].priority)
					monFighters.get(i).priority = true;
			}
			
			System.out.println("-----------------------------------------------");
			
			//check for priority, need to check what happens if speed is same with 2 priorities
			int priorCount = 0;
			boolean pastHero;
			for (int i = 0; i <= fighters.size()-1; i++) {
				Monsters priorAttacker = fighters.get(i);
				if (priorAttacker.priority && priorCount != i) { //need to fix when first fighter has priority true
					if (fighters.get(i-1).priority) {
						priorCount++;
						//System.out.println("Got here");
						break;
					}
					Monsters temp = null, temp2;
					int swapCount;
					pastHero = false;
					for (swapCount = 0; swapCount <= fighters.size()-1; swapCount++) {
						//System.out.println("yes" + priorAttacker.name+priorAttacker.spe);
						Monsters priorCheck = fighters.get(swapCount);
						if (priorAttacker.aggro)
							pastHero = true;
						//System.out.println(priorCheck.name+priorCheck.spe);
						if (!priorCheck.priority || (priorCheck.priority && (priorCheck.spe < priorAttacker.spe))) {
							temp = priorCheck;
							//System.out.println("Got hered");
							fighters.set(swapCount, priorAttacker);
							if (!(priorAttacker.aggro || priorCheck.aggro)) {
								int stoMove = monMoves[swapCount];
								monMoves[swapCount] = monMoves[i];
								monMoves[i] = stoMove;
							}
							break;
						}
					}
					for (int j = swapCount+1; j <= fighters.size()-1; j++) {
						temp2 = fighters.get(j);
						//System.out.println(temp2.name);
						fighters.set(j, temp);
						if (j == i)
							break;
						temp = temp2;
					}
				}
			}
			
			int monCount = 0;
			for (int i = 0; i <= fighters.size()-1; i++) { //Goes through the move of each fighter, if attacking, target set here
				Monsters attacker = fighters.get(i);
				if (target.hp <= 0) //got rid of flee maybe temporary
					break;
				else if (attacker.stun) { //checks if fighter is stunned, if so, skips turn
					System.out.println(attacker.name + " is stunned");
					attacker.stun = false;
					
				} else if (!attacker.aggro) { //Monster attacks
					int monMoveNum = monMoves[monCount]; //might be wrong attack since priority order different
					monCount++;
					Attacks monMove = null;
					if (attacker.skip) {
						monMove = attacker.moveList[attacker.store];
						attacker.store = 0;
					} else {
						monMove = attacker.moveList[monMoveNum];
						attacker.store = monMoveNum; //does previous turn move
					}
					monMove.setTarget(target); //doesn't account for multiple targets, maybe do rng to select other targets?
					double startHp = monMove.targets[0].hp;
					monMove.execute();
					monMove.targets[0].damTurn += (startHp - monMove.targets[0].hp);
					attacker.damTurn = 0; //resets the amount of damage taken
					
				} else { //Hero action, attacks target set here or then targets somehow get overridden
					Interface.heroAction = false; //sets default value, will by default ask for user input
					if (potionBuff)
						Potions.buffCheck (attacker, pick);
					if (flee)
						attacker.spe -= 7;
					switch (choice) {
						case 1: //attacks inputed target
							double[] startHp = new double[heroTargets.size()];
							for (int j = 0; j <= turnMove.numTar-1; j++) {
								if (j < heroTargets.size()) {
									turnMove.setTarget(heroTargets.get(j));
									startHp[j] = heroTargets.get(j).hp;
								} else
									turnMove.setTarget(null);
							}
							turnMove.execute();
							for (int j = 0; j < startHp.length; j++) {
								heroTargets.get(j).damTurn += (startHp[j] - turnMove.baseDam); //doing the last damage of spin attack, need to change
							}
							attacker.damTurn = 0; //resets the amount of damage taken
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
							potionBuff = true;
							pick.useItem(attacker);
					}
					for (int j = 0; j < monFighters.size(); j++) { //check if any monster died, immediately after hero's turn
						if (monFighters.get(j).hp <= 0) {
							if (j < i)
								i--;
							fighters.remove(monFighters.get(j));
							System.out.println("\n" + monFighters.get(j).name + " has died");
							monFighters.remove(monFighters.get(j));
						}
					}
					
				}
				if (attacker.mp > attacker.maxMp)
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
	
	public static void fighterStatuses () {
		
	}
}