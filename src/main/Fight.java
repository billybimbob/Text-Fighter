package main;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import combat.*;
import combat.magic.shapeshift.*;

public class Fight {
	
	private static final String[] fightChoices = {"Fight", "Dodge", "Inventory"};
	private static boolean skipTurn;
	private static Items pick; //temporary
	public static int turnCount;
	public static ArrayList<Monsters> fighters;

	public static void fighting (Scanner keyboard, ArrayList<Monsters> fightersIn) throws InterruptedException {
		fighters = fightersIn;
		Monsters target = null;
		boolean fightControl = true, flee = false;
		turnCount = 0;
		pick = null;
		int choice = 2, pickNum = 0;
		Ability turnMove = null;
		ArrayList<Monsters> heroTargets = new ArrayList<>(); //need to clear later
		
		System.out.println("Press enter when you are ready to fight");
		keyboard.nextLine();
		while (fightControl) {
			String[] monFightersName;
			ArrayList<Monsters> monFighters = new ArrayList<>();
			
			turnCount++; //turn counter
			
			attackOrder(fighters); //Orders the fighters by speed
			System.out.println("-----------------------------------------------");
			for (int i = 0; i <= fighters.size()-1; i++) { //Determine which is the hero, may change later, also prints each fighter and stats
				System.out.println(fighters.get(i).name + " - " + fighters.get(i).getStat("hp") + " hp" + " - " + fighters.get(i).getStat("mp") + " mp" + " - " + fighters.get(i).getStat("spe") + " speed");
				
				if (fighters.get(i).aggro) {
					target = fighters.get(i);
				} else {
					monFighters.add(fighters.get(i));
				}
				if (fighters.get(i).name == "Slime")
					fighters.get(i).priority = true;
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
						} else if (turnMove.getAoe() || turnMove.getTargets().length == monFighters.size()) {//attacks all monsters, might change later
							turnMove.setAllTar(monFighters);
							Interface.heroAction = true;
						/*} else if (turnMove.getAoe() || turnMove.getTargets().length==monFighters.size()) { //attacks all monsters if aoe attack or if only one option
							for (Monsters enemy: monFighters)
								heroTargets.add(enemy);
							Interface.heroAction = true;*/
						} else { //attacks with numTar less then available targets
							heroTargets.clear();
							for (int i = 0; i < turnMove.getTargets().length; i++) {
								String tarPrompt = "Which monster would you want to target?";
								int tarNum = Interface.choiceInput(keyboard, true, monFightersName, tarPrompt);
								if (tarNum == 0) {//have to change how to implement
									Interface.heroAction = false;
									break;
								}
								turnMove.setTarget(monFighters.get(tarNum-1));
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
						else if (Interface.hero.getStatus("potion")[0]!=0 && Potions.timeLength >= (turnCount-Potions.turnStart)) { //will trigger debuff
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
			Ability[] monMoves = new Ability[monFighters.size()];
			for (int i = 0; i < monFighters.size(); i++) {
				if (monFighters.get(i).storeTurn==null) {
					monMoves[i] = monFighters.get(i).moveList[(int)(Math.random()*monFighters.get(i).moveList.length)];
					//System.out.println(monMoves[i].getName());
					//monMoves[i].setTarget(target); //doesn't account for multiple targets, maybe do rng to select other targets?
					if (monMoves[i].getPriority())
						monFighters.get(i).priority = true;
				} else {
					monMoves[i] = monFighters.get(i).storeTurn;
				}
			}
			System.out.println("-----------------------------------------------");
			
			//check for priority, need to check what happens if speed is same with 2 priorities
			boolean pastHero = false;
			for (int src = 0; src < fighters.size(); src++) {
				Monsters priAttacker = fighters.get(src);
				if (priAttacker.aggro) {
					pastHero = true;
				}
				if (priAttacker.priority && src != 0) { //if priority and first, no need to move
					/*if (fighters.get(i-1).priority) {
						//priorCount++;
						break;
					}*/
					Monsters stoFitr = null, stoFitr2;
					int dst;// pastPriMon = 0; //swapCount is location of where to swap, pastPriorMon is the number of priority monsters past
					boolean pastHero2 = false;
					for (dst = 0; dst < fighters.size(); dst++) { //finds where to switch, as highest speed priority is 1st
						Monsters priCheck = fighters.get(dst);
						if (priCheck.aggro)
							pastHero2 = true;
						if (!priCheck.priority || (priCheck.priority && (priCheck.getStat("spe") < priAttacker.getStat("spe")))) {	
							//if (priCheck.priority && !priCheck.aggro) { //swaps the monster attack in move list associated with priority shifts
							//	pastPriMon++;
							//}
							if (!priAttacker.aggro) {
								int monSrc = src, monDst = dst;
								if (pastHero)
									monSrc -= 1;
								if (pastHero2 && dst!=0)
									monDst -= 1;
								
								Ability stoMove = monMoves[monDst], stoMove2;
								monMoves[monDst] = monMoves[monSrc];	
								for (int j = monDst+1; j <= monSrc; j++) {
									stoMove2 = monMoves[j];
									monMoves[j] = stoMove;
									stoMove = stoMove2;
								}
							}
							stoFitr = priCheck;
							fighters.set(dst, priAttacker);
							break;
						}
					}
					for (int j = dst+1; j <= src; j++) { //switches priority and scoots down rest behind, don't use add method because don't want to scoot entire list
						stoFitr2 = fighters.get(j);
						fighters.set(j, stoFitr);
						stoFitr = stoFitr2;
					}
				}
			}
			
			//Goes through the move of each fighter, if attacking, target set here
			int monCount = 0;
			for (int i = 0; i <= fighters.size()-1; i++) {
				//System.out.println(turnMove.getTargets()[0].name);
				Monsters attacker = fighters.get(i);
				skipTurn = false;
				if (target.getStat("hp") <= 0) //got rid of flee, maybe temporary
					break;
				
				//status effect check of each monster
				/*for (int j = 0; j <= attacker.status.length-1; j++) {
					int statTurn = attacker.status[j][0], duration = attacker.status[j][1];
				}*/
				passiveCheck(attacker);
				statusCheck(attacker, "burn");
				statusCheck(attacker, "potion"); //not sure if should be end of turn or beginning
				statusCheck(attacker, "stun");
				
				if (!attacker.aggro) { //Monster attacks
					//might be wrong attack since priority order different
					if (!skipTurn) {
						Ability monMove = null;
						if (attacker.storeTurn != null) {
							monMove = attacker.storeTurn; //does previous turn move
						} else {
							monMove = monMoves[monCount];
						}
						monMoves[monCount].setTarget(target); //doesn't account for multiple targets, maybe do rng to select other targets?
						monMove.execute();
					}
					monCount++;
				} else if (!skipTurn && attacker.aggro){ //Hero action, attacks target set here or then targets somehow get overridden
					Interface.heroAction = false; //sets default value, will by default ask for user input
					if (flee)
						attacker.modStat("spe", -7);
					switch (choice) {
					case 1: //attacks inputed target
						/*if (!turnMove.getSelfTar()) {
							for (int j = 0; j < turnMove.getTargets().length; j++) {
								if (j < heroTargets.size()) {
									turnMove.setTarget(heroTargets.get(j));
									//startHp[j] = heroTargets.get(j).hp;
								}
							}
						}*/
						turnMove.execute();
						break;
					case 2: //Try to flee
						//double escapeCheck = Math.random() + (attacker.spe*0.1-monFighters.get(0).spe*0.1); //Escape check based on speed of hero, against fastest enemy, and RNG
						Interface.hero.modStat("mp", -3);
						Interface.hero.modStat("spe", 7);
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
						if (monFighters.get(j).getStat("hp") <= 0) {
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
				statusCheck(attacker, "reflect");
				statusCheck(attacker, "shapeshift");
				
				attacker.damTurn = 0; //resets the amount of damage taken in one turn
				if (attacker.getStat("mp") < attacker.getStat("maxMp")) //passive mp gain, could change the val
					attacker.modStat("mp", 1);
				System.out.println();
				TimeUnit.SECONDS.sleep(2);
			}
			
			if (target.getStat("hp") <= 0) { //Check if hero hp is zero
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
	
	public static void statusCheck (Monsters checking, String statusName) { //each turn effects
		int[] statusData = checking.getStatus(statusName);
		int startTurn = statusData[0], duration = statusData[1]; //statTurn is turn activated
		switch(statusName) {
		case "burn":
			if (startTurn != 0) {
				int burnDam = (int)(checking.getStat("hp")*0.1);
				checking.setStat("hp", -burnDam);
				System.out.println(checking.name + " is burned, and takes " + burnDam + " damage");
				if (turnCount-startTurn == duration) {
					checking.setStatus("burn", false);
					System.out.println(checking.name + " is no longer burned");
				}
			}
			break;
		case "poison":
			if (startTurn != 0) {
				int poiDam = (int)(checking.getStat("hp")*0.01*((turnCount-startTurn)%10));
				checking.setStat("hp", -poiDam);
				System.out.println(checking.name + " is poisoned, and takes " + poiDam + " damage");
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
				System.out.println(checking.name + "'s reflect has worn off");
			}
			break;
		case "shapeshift":
			if (startTurn != 0 && turnCount-startTurn >= duration) { //triggered by shapeshift
				ShapeShift.revert(checking);
				System.out.println(checking.name + " reverted back");
			}
			break;
		case "stun":
			if (startTurn != 0) { //triggered by chargeatt, magblast, disrupt
				System.out.println(checking.name + " is stunned");
				skipTurn = true;
				checking.setStatus("stun", false);
			}
			break;
		}
	}
	public static void statusCheck(Monsters attacker, Monsters target, String statusName, double damDeal) { //target based status checks, might move to monsters class
		int[] statusData = target.getStatus(statusName);
		int startTurn = statusData[0], duration = statusData[1]; //statTurn is turn activated
		switch (statusName) {
		case "reflect":
			if (startTurn != 0) {
				double refDam = (int)(damDeal*0.5); 
				attacker.modStat("hp", -refDam);
				System.out.println(target.name + " reflects " + refDam + " damage to " + attacker.name);
			}
			if (startTurn != 0 && turnCount-startTurn >= duration) {
				target.setStatus(statusName, false);
				System.out.println(target.name + "'s reflect has worn off");
			}
			break;
		}
	}
	public static void passiveCheck (Monsters user) { //not sure if should be all enemies or all non-user, inefficient
		if (user.passive != null) {
			if (user.passive.getAoe()) {
				ArrayList<Monsters> nonSelf = new ArrayList<Monsters>();
				for (Monsters mon: fighters) {
					if (mon != user)
						nonSelf.add(mon);
				}
				user.passive.setAllTar(nonSelf);
			}
			user.passive.execute();
		}
	}
}