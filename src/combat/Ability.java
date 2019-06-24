package combat;

import assets.*;
import main.Interface;

public abstract class Ability implements Cloneable {

	protected String name, description;
	protected float manaCost, baseDam, baseDamMod;
	protected Monster attacker;
	protected int numTar, duration; //no limit is -1, 0 is self
	protected boolean attType, priority, passive; //aoe attacks can't work with Monster
	
	public Ability(Monster user) {
		this.priority = false;
		this.passive = false;
		this.numTar = 1;
		this.duration = 1;
		this.attacker = user;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Ability clone(Monster attacker) throws CloneNotSupportedException { //can't do copy constructor becuase of subclasses
		Ability newAbility = (Ability)this.clone();
		newAbility.attacker = attacker;
		return newAbility;
	}


	/* 
	 * combat calculations, helper functions
	 */

	protected boolean enoughMana() {
		return attacker.getStat("mp") >= manaCost;
	}

	protected boolean attackHit(Monster target, double checkMod) { //an attack damage check based on either the att or mag stat
		String hitStat = attType?"att":"mag", blockStat = attType?"def":"magR";
		double checkNum = Math.random()*attacker.getStat(hitStat) - Math.random()*target.getStat("spe")*.5;
		return checkNum > target.getStat(blockStat)*checkMod;
	}

	protected boolean critCheck() {
		double check = Math.random();
		boolean critHit = check < attacker.getStat("crit")*0.02;		
		return critHit;
	}

	protected boolean damDealt() {
		return baseDam < 0;
	}

	public boolean resolved() { //check if multi turn, see if ability finished
		return duration == 1;
	}

	protected void baseDamage() { //determines the damage if either a melee or magic attack
		String hitStat = attType ? "att" : "mag";
		baseDam = (int)(Math.random()*(attacker.getStat(hitStat)*baseDamMod)+1);
	}

	protected void targetReduct(Monster target) {
		String blockStat = attType ? "def" : "magR";
		baseDam -= (int)(Math.random()*(target.getStat(blockStat)*.65));
		
		int minDam = target.minDam(attacker, attType);
		if (baseDam < minDam)
			baseDam = minDam;
	}


	/*
	 * getters
	 */

	public String getName() {
		return name;
	}
	public double getCost() {
		return manaCost;
	}
	public int getNumTar() {
		return numTar;
	}
	public boolean getPriority() {
		return priority;
	}
	public boolean getPassive() {
		return passive;
	}
	public boolean targeted() {
		return numTar > 0;
	}

	@Override
	public String toString() {
		return name + " - " + manaCost + " mana\n\t" + description;
	}

	
	public static void dealDam (Monster attacker, Monster target, float damage) {
		target.modStat("hp", -damage);
		target.addDamTurn(damage);
		Interface.FIGHT.addLog(attacker, target, damage);
	}

	public abstract void execute();

}