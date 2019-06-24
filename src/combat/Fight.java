package combat;

import java.util.*;
import java.util.concurrent.TimeUnit;
import assets.*;
import main.*;
import combat.moves.magic.shapeshift.*;

public class Fight {
	
	public static final String[] FIGHTCHOICES = {"Fight", "Dodge", "Inventory"};

	private FightLog log;
	private List<Monster> fighters;
	private boolean skipTurn;

	public Fight(List<Monster> fighters) {
		this.log = new FightLog();
		this.fighters = fighters;
	}
	
	public int getTurnNum() { return log.roundCount(); }

	void addLog(Monster attacker, Monster target, double damage) {
		log.addLog(attacker, target, damage);
	}

	public void start() {
		boolean fightControl = true; //could add flee back
		List<Monster> monFighters = new ArrayList<>();

		while (fightControl) {
			log.newRound();
				
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
					if (monFighters.get(j).getStat(Stat.HP) <= 0) {
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
			} else if (Interface.HERO.getStat(Stat.HP) <= 0) { //Check if hero hp is zero
				Interface.writeOut("You have received a fatal blow, and have died");
				fightControl = false;
			/*} else if (flee) {
				Interface.writeOut("You have successfully escaped");
				fightControl = false;*/
			}
		}
		Interface.writeOut("Exiting fight");
		log.clear();
	}

	public static void attackOrder (List<Monster> list) { //orders the combatants from highest speed to lowest
		Collections.sort(list);
		/*
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
		}*/
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
					if (!priCheck.getPriority() || (priCheck.getPriority() && (priCheck.getStat(Stat.SPEED) < priAttacker.getStat(Stat.SPEED)))) {	
						fighters.add(dst, fighters.remove(src)); //moves mon to dst, and scoots down rest
						break;
					}
				}
			}
		}
	}

	private void runTurn(Monster attacker, List<Monster> enemies) {
		skipTurn = false;
		if (attacker.getStat(Stat.HP) <= 0) //got rid of flee, maybe temporary
			return;
		
		attacker.usePassive(nonSelf(attacker));
		statusCheck(attacker, Status.BURN);
		statusCheck(attacker, Status.POTION); //not sure if should be end of turn or beginning
		statusCheck(attacker, Status.STUN);
		
		if (!skipTurn) 
			//might be wrong attack since priority order different
			attacker.executeTurn(); //doesn't account for multiple targets, maybe do rng to select other targets?
			//includes heroTurn, overriden


		//end of turn
		statusCheck(attacker, Status.POISON);
		statusCheck(attacker, Status.REFLECT);
		statusCheck(attacker, Status.SHIFT);
		
		attacker.clearTurn();

		if (attacker.getStat(Stat.MP) < attacker.getStat(Stat.MAXMP)) //passive mp gain, could change the val
			attacker.modStat(Stat.MP, 1);

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


		
	public void statusCheck (Monster checking, Status status) { //each turn effects
		int check = checking.checkStatus(status, getTurnNum());

		if (check > -1) { //status active
			switch(status) {
			case BURN:
				int burnDam = (int)(checking.getStat(Stat.HP)*0.1);
				checking.modStat(Stat.HP, -burnDam);
				Interface.writeOut(checking.getName() + " is burned, and takes " + burnDam + " damage");
				if (check == 0) {
					checking.setStatus(status, false);
					Interface.writeOut(checking.getName() + " is no longer burned");
				}
				break;

			case POISON:
				int poiDam = (int)(checking.getStat(Stat.HP)*0.01*((getTurnNum()-check)%10));
				checking.modStat(Stat.HP, -poiDam);
				Interface.writeOut(checking.getName() + " is poisoned, and takes " + poiDam + " damage");
				break;

			case POTION:
				Hero user = (Hero)checking;
				Potions.buffCheck(user);
				break;

			case REFLECT: //try to go from turn to turn
				for (FightLog.LogInfo info: log.getInfo(getTurnNum()-1, checking)) { //checking all damage recieved from last round
					Monster attacker = info.getAttacker();
					double damDeal = info.getDamage();
					float refDam = (int)(damDeal*0.5);
					attacker.modStat(Stat.HP, -refDam);
					Interface.writeOut(checking.getName() + " reflects " + refDam + " damage to " + attacker.getName());
				}

				if (check == 0) { //finished
					checking.setStatus(status, false);
					Interface.writeOut(checking.getName() + "'s reflect has worn off");
				}
				break;

			case SHIFT:
				if (check == 0) { //triggered by shapeshift
					ShapeShift.revert(checking);
					Interface.writeOut(checking.getName() + " reverted back");
				}
				break;

			case STUN:
				//triggered by chargeatt, magblast, disrupt
				Interface.writeOut(checking.getName() + " is stunned");
				skipTurn = true;
				checking.setStatus(status, false);
				break;
			}
		}
	}


}