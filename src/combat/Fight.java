package combat;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import assets.*;
import main.*;

public class Fight {
	
	public static final String[] FIGHTCHOICES = {"Fight", "Dodge", "Inventory", "View Equipment"};

	private FightLog log;
	private List<Monster> fighters;
	private boolean fightControl; //indicate fight running and if to skip turns

	public Fight(List<Monster> fighters) {
		this.log = new FightLog();
		this.fighters = new ArrayList<>(fighters); //for random access
		this.fightControl = false;
	}
	
	public int getTurnNum() { return log.roundCount()-1; }
	public FightLog getLogs() { return log; }


	public void run() {
		if (fightControl) {
			System.err.println("fight is already running");
			return;
		}

		fightControl = true;
		do {
			newRound();
			
			//decides the turns
			for (Monster fighter: fighters)
				fighter.setTurn( otherFighters(fighter) ); //could determine nonSelf in setTurn
			
			priorities();
			
			for (int i = 0; fightControl && i < fighters.size(); i++) { //idx loop from removing
				Monster fighter = fighters.get(i);
				runTurn(fighter);

				for(int idx: removeDead()) {
					if (idx <= i) i--;
				}
			}

			/*
			//Goes through the move of each fighter
			for (Monster fighter: fighters)
				runTurn(fighter);

			//remove after to not mess up prior targets
			removeDead();*/

			if (fightControl && noEnemies()) { //Check if all Monster are killed
				Interface.writeOut("There are no more enemies, you win!");
				fightControl = false;
			}

		} while(fightControl);

		Interface.writeOut("Exiting fight");
		log.clear();
	}


	//private helpers
	private List<Monster> otherFighters (Monster user) { //inefficient maybe
		return this.fighters.stream()
			.filter(mon -> !mon.equals(user))
			.collect(Collectors.toList());
	}

	
	private void newRound() {
		log.newRound();
		attackOrder(); //Orders the fighters by speed

		StringBuilder lstFighters = new StringBuilder(Interface.LINESPACE + "\n");
		fighters.forEach(fighter -> {
			fighter.resetStatusChecks();
			lstFighters.append(fighter + "\n");
		});
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

		if (attacker.getStat(Stat.HP) <= 0) { //got rid of flee, maybe temporary; should not occur
			System.err.println(attacker.getName() + " should be dead");
			return;
		}
		
		attacker.usePassive( otherFighters(attacker) ); //runs passive and updates targets
		
		//could put status in array
		statusCheck(attacker, Status.BURN);
		statusCheck(attacker, Status.FRENZY);
		statusCheck(attacker, Status.POTION); //not sure if should be end of turn or beginning
		statusCheck(attacker, Status.REFLECT);
		statusCheck(attacker, Status.STUN);

		if (fightControl) 
			/**
			 * fightControl can be switched off
			 * from stun
			 * includes heroTurn, overriden
			 */
			attacker.executeTurn();


		//end of turn
		statusCheck(attacker, Status.CONTROL);
		statusCheck(attacker, Status.DODGE);
		statusCheck(attacker, Status.POISON);
		statusCheck(attacker, Status.SHIFT);
		
		fightControl = true; //not sure
		attacker.clearTurn();
		attacker.modStat(Stat.MP, true, 1); //passive mp gain, could change the val
		
		if (!attacker.statusUpdated()) { //might be temporary
			System.err.println("status failed to updated");
			for (Status status: attacker.getNotChecked())
				statusCheck(attacker, status);
		}

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {}
		
		Interface.writeOut(" ");
	}	


	private List<Integer> removeDead() {
		List<Integer> removedIdx = new ArrayList<>();
		Iterator<Monster> iter = fighters.iterator(); 
		
		for (int i = 0; iter.hasNext(); i++) {
			Monster fighter = iter.next();
			
			if (fighter.getStat(Stat.HP) <= 0) {
				if (fighter.getClass().equals(Hero.class)) {
					fightControl = false;
					Interface.writeOut("You have received a fatal blow, and have died");
					removedIdx.clear();
					return removedIdx;
				} else {
					iter.remove();
					removedIdx.add(i);
					Interface.writeOut(fighter.getName() + " has died\n");
				}
			}
		}
		return removedIdx;
	}
		
	/**
	 * each turn effects, while status is active
	 */
	private void statusCheck (Monster checking, Status status) {
		int check = checking.updateStatus(status);
	
		if (check == 0) { //finished
			switch(status) {
				case BURN:
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

				case REFLECT:
					Interface.writeOut(checking.getName() + "'s reflect has worn off");
					break;

				case SHIFT:
					Interface.writeOut(checking.getName() + " reverted back");
					break;
					
				case DODGE:
				case FRENZY:
				case POTION:
				case STUN:
					break;
			}

		} else if (check > -1) { //active
			switch(status) {
				case BURN:
					int burnDam = (int)(checking.getStat(Stat.HP)*0.1f*Math.random());
					burnDam = burnDam == 0 ? 1 : burnDam; //floor to 1
					checking.modStat(Stat.HP, false, -burnDam);
					Interface.writeOut(checking.getName() + " is burned, and takes " + burnDam + " damage");
					break;

				case DODGE:
					Interface.writeOut(checking.getName() + " has increased evasiveness");
					break;

				case FRENZY:
					checking.setRandomTargets( otherFighters(checking) ); //nonself might be slow
					Interface.writeOut(checking.getName() + " is frenzied, targets are random");
					break;

				case POISON:
					int poiDam = (int)(checking.getStat(Stat.HP)*0.1*(getTurnNum()%10));
					checking.modStat(Stat.HP, false, -poiDam);
					Interface.writeOut(checking.getName() + " is poisoned, and takes " + poiDam + " damage");
					break;

				case POTION:
					Equipment.overTime(checking);
					break;

				case STUN:
					fightControl = false; //not sure
					Interface.writeOut(checking.getName() + " cannot move"); //multiTurn attack still carries on
					break;

				case CONTROL:
				case REFLECT:
				case SHIFT:
					break;
			}
		}
	}

	private boolean noEnemies() {
		for (Monster mon: fighters) {
			if (mon.getAggro()) //does not account for temp no aggro
				return false;
		}
		return true;
	}

}