package combat;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import assets.*;
import assets.chars.*;
import main.*;

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

	public int getTurnNum() { return log.roundCount()-1; }

	public float getTurnDamage(int round, Monster target) {
		float totalDam = 0;
		for(FightLog.LogInfo info: log.getInfo(round, target))
			totalDam += info.getDamage();
		
		return totalDam;
	}

	public void start() {
		boolean fightControl = true; //could add flee back
		
		do {
			newRound();
			
			//decides the turns
			for (Monster fighter: fighters)
				fighter.setTurn(nonSelf(fighter, fighters)); //could determine nonSelf in setTurn
			
			priorities();
			
			//Goes through the move of each fighter
			for (Monster fighter: fighters)
				runTurn(fighter);

			for (Iterator<Monster> i = fighters.iterator(); i.hasNext(); ) {
				Monster fighter = i.next();
				
				if (fighter.getStat(Stat.HP) <= 0) {
					if (fighter.getClass().equals(Hero.class)) {
						fightControl = false;
						Interface.writeOut("You have received a fatal blow, and have died");
						break;
					} else {
						i.remove();
						Interface.writeOut(fighter.getName() + " has died");
					}
				}
			}

			if (fightControl && nonSelf(Interface.HERO, fighters).size() == 0) { //Check if all Monster are killed
				Interface.writeOut("All of the Monster have been killed, you win!");
				fightControl = false;
			}

		} while(fightControl);

		Interface.writeOut("Exiting fight");
		log.clear();
	}


	private void newRound() {
		log.newRound();
		
		StringBuilder lstFighters = new StringBuilder(Interface.LINESPACE + "\n");

		attackOrder(); //Orders the fighters by speed
		//determine which is the hero, may change later, also prints each fighter and stats
		fighters.forEach(fighter -> lstFighters.append(fighter + "\n"));

		lstFighters.append(Interface.LINESPACE + "\n");
		Interface.writeOut(lstFighters.toString());
	}
	
	private void attackOrder () { //orders the combatants from highest speed to lowest
		fighters.sort(new Comparator<Monster>() {
			@Override
			public int compare(Monster a, Monster b) {	//highest speed first
				return Monster.speedCompare(a, b);
			}
		});
	}

	private void priorities() {
		//check for priority, need to check what happens if speed is same with 2 priorities
		fighters.sort(new Comparator<Monster>() {
			@Override
			public int compare(Monster a, Monster b) {
				if (a.getPriority() && !b.getPriority())
					return -1;
				else if (!a.getPriority() && b.getPriority())
					return 1;
				else
					return Monster.speedCompare(a, b);
			}
		});
	}

	private void runTurn(Monster attacker) {
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
	
		if (check == 0) { //finished
			checking.setStatus(status, false);

			switch(status) {
				case BURN:
					int burnDam = (int)(checking.getStat(Stat.HP)*0.1);
					checking.modStat(Stat.HP, false, -burnDam);
					Interface.writeOut(checking.getName() + " is burned, and takes " + burnDam + " damage");
					Interface.writeOut(checking.getName() + " is no longer burned");
					break;

				case CONTROL:
					Interface.writeOut(checking.getName() + " is no longer controlled");
					break;

				case POISON:
					checking.setStatus(status, true);
					int poiDam = (int)(checking.getStat(Stat.HP)*0.01*(getTurnNum()%10));
					checking.modStat(Stat.HP, false, -poiDam);
					Interface.writeOut(checking.getName() + " is poisoned, and takes " + poiDam + " damage");
					break;

				case POTION:
					Potions.buffCheck((Hero)checking, true);
					break;

				case REFLECT:
					Interface.writeOut(checking.getName() + "'s reflect has worn off");
					break;

				case SHIFT:
					ShapeShift.revert(checking);
					Interface.writeOut(checking.getName() + " reverted back");
					break;
					
				case DODGE:
				case FRENZY:
				case STUN:
					break;
			}

		} else if (check > -1) { //active
			switch(status) {
				case BURN:
					int burnDam = (int)(checking.getStat(Stat.HP)*0.1);
					checking.modStat(Stat.HP, false, -burnDam);
					Interface.writeOut(checking.getName() + " is burned, and takes " + burnDam + " damage");
					break;

				case DODGE:
					Interface.writeOut(checking.getName() + " has increased evasiveness");
					break;

				case FRENZY:
					checking.setRandomTargets(nonSelf(checking, fighters)); //nonself might be slow
					Interface.writeOut(checking.getName() + " is frenzied, targets are random");
					break;

				case POISON:
					int poiDam = (int)(checking.getStat(Stat.HP)*0.1*(getTurnNum()%10));
					checking.modStat(Stat.HP, false, -poiDam);
					Interface.writeOut(checking.getName() + " is poisoned, and takes " + poiDam + " damage");
					break;

				case POTION:
					Potions.buffCheck((Hero)checking, false);
					break;

				case REFLECT:
					for (FightLog.LogInfo info: log.getInfo(getTurnNum()-1, checking)) { //checking all damage recieved from last round
						Monster attacker = info.getAttacker();
						float refDam = (int)(info.getDamage()*0.5);
						attacker.modStat(Stat.HP, false, -refDam);
						Interface.writeOut(checking.getName() + " reflects " + refDam + " damage to " + attacker.getName());
					}
					break;

				case STUN:
					skipTurn = true;
					Interface.writeOut(checking.getName() + " is stunned"); //multiTurn attack still carries on
					break;

				case CONTROL:
				case SHIFT:
					break;
			}
		}
	}

	 //not sure if should be all enemies or all non-user, inefficient
	public static List<Monster> nonSelf (Monster user, List<Monster> allFighters) {
		return allFighters.stream()
			.filter(mon -> !mon.equals(user))
			.collect(Collectors.toList());
	}

}