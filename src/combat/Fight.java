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
	
	public void addLog(Monster attacker, Monster target, float damage) {
		log.addLog(attacker, target, damage);
	}

	public int getTurnNum() { return log.roundCount(); }

	public float getTurnDamage(int round, Monster target) {
		float totalDam = 0;
		for(FightLog.LogInfo info: log.getInfo(round, target)) {
			totalDam += info.getDamage();
		}
		return totalDam;
	}

	public void start() {
		boolean fightControl = true; //could add flee back
		
		while (fightControl) {
			List<Monster> monFighters = newRound();

			//decides the turns
			for (Monster fighter: fighters) {
				fighter.setTurn(nonSelf(fighter, fighters)); //could determine nonSelf in setTurn
			}
			
			priorities();
			
			//Goes through the move of each fighter
			for (int i = 0; i < fighters.size(); i++) {
				
				runTurn(fighters.get(i), monFighters); //not sure

				for (int j = 0; j < monFighters.size(); j++) { //check if any monster died, immediately after hero's turn
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


	private List<Monster> newRound () {
		log.newRound();
				
		Interface.writeOut(Interface.LINESPACE);

		attackOrder(); //Orders the fighters by speed
		List<Monster> monFighters = determineEnemies();

		Interface.writeOut(Interface.LINESPACE);
		return monFighters;
	}
	
	private void attackOrder () { //orders the combatants from highest speed to lowest
		Collections.sort(fighters, new Comparator<Monster>() {
			@Override
			public int compare(Monster a, Monster b) {	//highest speed first
				Float aSpeed = a.getStat(Stat.SPEED), bSpeed = b.getStat(Stat.SPEED);
				return bSpeed.compareTo(aSpeed);
			}
		});
	}

	private List<Monster> determineEnemies() {
		StringBuilder lstFighters = new StringBuilder();
		for (Monster fighter: fighters) //Determine which is the hero, may change later, also prints each fighter and stats
			lstFighters.append(fighter + "\n");
		
		Interface.writeOut(lstFighters.toString());

		return nonSelf(Interface.HERO, fighters);
	}

	private void priorities() {
		//check for priority, need to check what happens if speed is same with 2 priorities
		for (int src = 0; src < fighters.size(); src++) {
			Monster priAttacker = fighters.get(src);

			if (priAttacker.getPriority() && src != 0) { //if priority and first, no need to move
				for (int dst = 0; dst < src; dst++) { //finds where to switch, as highest speed priority is dst
					Monster priCheck = fighters.get(dst);
					if (!priCheck.getPriority() || (priCheck.getPriority() 
						&& (priCheck.getStat(Stat.SPEED) < priAttacker.getStat(Stat.SPEED)))) {	
						
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
		
		attacker.usePassive(nonSelf(attacker, fighters));
		statusCheck(attacker, Status.BURN);
		statusCheck(attacker, Status.FRENZY);
		statusCheck(attacker, Status.POTION); //not sure if should be end of turn or beginning
		statusCheck(attacker, Status.REFLECT);
		statusCheck(attacker, Status.STUN);

		if (!skipTurn) 
			/**
			 * might be wrong attack since priority order different
			 * doesn't account for multiple targets, maybe do rng to select other targets?
			 * includes heroTurn, overriden
			 */
			attacker.executeTurn();


		//end of turn
		statusCheck(attacker, Status.CONTROL);
		statusCheck(attacker, Status.DODGE);
		statusCheck(attacker, Status.POISON);
		statusCheck(attacker, Status.SHIFT);
		
		attacker.clearTurn();

		attacker.modStat(Stat.MP, true, 1); //passive mp gain, could change the val

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {}
		
		Interface.writeOut(" ");
	}	
		
	/**
	 * each turn effects, while status is active
	 */
	private void statusCheck (Monster checking, Status status) {
		int check = checking.getStatus(status);

		if (check > -1) { //status active
			switch(status) {
				case BURN:
					int burnDam = (int)(checking.getStat(Stat.HP)*0.1);
					checking.modStat(Stat.HP, false, -burnDam);
					Interface.writeOut(checking.getName() + " is burned, and takes " + burnDam + " damage");
					if (check == 0) {
						checking.setStatus(status, false);
						Interface.writeOut(checking.getName() + " is no longer burned");
					}
					break;

				case CONTROL:
					if (check == 0) {
						checking.setStatus(status, false);
						Interface.writeOut(checking.getName() + " is no longer controlled");
					}
					break;

				case DODGE:
					if (check == 0) //done
						checking.setStatus(status, false);
					else
						Interface.writeOut(checking.getName() + " has increased evasiveness");
					break;

				case FRENZY:
					if (check == 0)
						checking.setStatus(status, false);
					else {
						checking.setRandomTargets(nonSelf(checking, fighters)); //nonself might be slow
						Interface.writeOut(checking.getName() + " is frenzied, targets are random");
					}	
					break;

				case POISON:
					int poiDam = (int)(checking.getStat(Stat.HP)*0.01*(getTurnNum()%10));
					checking.modStat(Stat.HP, false, -poiDam);
					Interface.writeOut(checking.getName() + " is poisoned, and takes " + poiDam + " damage");
					break;

				case POTION:
					Hero user = (Hero)checking;
					boolean finished = check == 0;
					Potions.buffCheck(user, finished);
					break;

				case REFLECT: //try to go from turn to turn
					for (FightLog.LogInfo info: log.getInfo(getTurnNum()-1, checking)) { //checking all damage recieved from last round
						Monster attacker = info.getAttacker();
						double damDeal = info.getDamage();
						float refDam = (int)(damDeal*0.5);
						attacker.modStat(Stat.HP, false, -refDam);
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
					skipTurn = true;
					Interface.writeOut(checking.getName() + " is stunned"); //multiTurn attack still carries on
					if (check == 0)
						checking.setStatus(status, false);
					break;
			}
		}
	}

	 //not sure if should be all enemies or all non-user, inefficient
	public static List<Monster> nonSelf (Monster user, List<Monster> allFighters) {
		List<Monster> sto = new ArrayList<>();
		for (Monster mon: allFighters) {
			if (!mon.equals(user))
				sto.add(mon);
		}
		return sto;
	}

}