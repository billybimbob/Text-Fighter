package combat;

import assets.*;
import main.Interface;

public abstract class Ability implements Cloneable {

	protected String name, description;
	protected float manaCost, damage, damageMod;
	protected Monster attacker;
	protected int numTar, duration; //no limit is -1, 0 is self
	protected boolean attType, priority, passive; //aoe attacks can't work with Monster
	
	public Ability(Monster user) {
		this.priority = false;
		this.passive = false;
		this.numTar = 1;
		this.duration = 1;
		this.attacker = user;
		this.damage = 0;
		this.damageMod = 1;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Ability clone(Monster attacker) throws CloneNotSupportedException { //can't do copy constructor becuase of subclasses
		Ability newAbility = (Ability)this.clone();
		newAbility.attacker = attacker;
		damage = 0;
		return newAbility;
	}


	/* 
	 * combat calculations, helper functions
	 */

	protected boolean enoughMana() {
		return attacker.getStat(Stat.MP) >= manaCost;
	}

	protected boolean attackHit(Monster target, double checkMod) { //an attack damage check based on either the att or mag stat
		Stat hitStat = Monster.getHitStat(attType);
		Stat blockStat = Monster.getBlockStat(attType);
		double checkNum = Math.random()*attacker.getStat(hitStat) - Math.random()*target.getStat(Stat.SPEED)*.5;
		return checkNum > target.getStat(blockStat)*checkMod;
	}

	protected void baseDamage() { //determines the damage if either a melee or magic attack
		Stat hitStat = Monster.getHitStat(attType);
		damage = (int)(Math.random()*(attacker.getStat(hitStat)*damageMod)+1);
	}
	
	protected boolean critCheck() {
		double check = Math.random();
		boolean critHit = check < attacker.getStat(Stat.CRIT)*0.02;
		damage = critHit ? damage*2 : damage; //not sure if here or execute
		return critHit;
	}

	protected void targetReduct(Monster target) {
		Stat blockStat = Monster.getBlockStat(attType);
		damage -= (int)(Math.random()*(target.getStat(blockStat)*.65));
		
		int minDam = target.minDam(attacker, attType);

		damage = minDam > damage ? minDam : damage; //floor to minDam
	}

	protected boolean blocked() { //could change to minDam check
		return damage <= 0;
	}

	public boolean resolved() { //check if multi turn, see if ability finished
		return duration == 1;
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
	public boolean isPassive() {
		return passive;
	}
	public boolean targeted() {
		return numTar > 0;
	}

	@Override
	public String toString() {
		return name + " - " + manaCost + " mana\n\t" + description;
	}

	
	public static void dealDamage (Monster attacker, Monster target, float damage) {
		target.modStat(Stat.HP, -damage);
		target.addDamTurn(damage);
		Interface.FIGHT.addLog(attacker, target, damage);
	}

	public abstract void execute();

}