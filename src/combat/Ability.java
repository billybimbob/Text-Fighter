package combat;

import assets.*;
import main.Interface;

public abstract class Ability implements Cloneable {

	protected String name, description;
	protected float manaCost, attMod, damage, damageMod;
	protected Monster attacker;
	protected int numTar;
	protected boolean attType, priority, passive; //aoe attacks can't work with Monster
	
	/**
	 * set default values for an abiltiy
	 */
	protected Ability(Monster user) {
		this.priority = false;
		this.passive = false;
		this.numTar = 1;
		this.damage = 0;
		this.attMod = 0.01f;
		this.damageMod = 1;
		this.attacker = user;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Object clone(Monster attacker) throws CloneNotSupportedException { //can't do copy constructor becuase of subclasses
		Ability newAbility = (Ability)this.clone();
		newAbility.attacker = attacker;
		newAbility.damage = 0;
		return newAbility;
	}


	/* 
	 * combat calculations, helper functions
	 */

	protected boolean enoughMana() {
		boolean check = attacker.getStat(Stat.MP) >= manaCost;
		if (check) //not sure if I want to pair; immediately revmoves cost if able
			attacker.modStat(Stat.MP, true, -manaCost);
		else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
			
		return check;
	}

	protected boolean attackHit(Monster target, String failPrompt) { //an attack damage check based on either the att or mag stat
		Stat hitStat = Monster.getHitStat(attType);
		Stat blockStat = Monster.getBlockStat(attType);
		double checkNum = Math.random()*attacker.getStat(hitStat) - Math.random()*target.getStat(Stat.SPEED)*0.5;
		boolean check = checkNum > target.getStat(blockStat)*attMod;
		
		if (check) //determines damage if successful
			damage = (int)(Math.random()*(attacker.getStat(hitStat)*damageMod)+1);
		else
			Interface.writeOut(failPrompt);

		return check;
	}
	
	protected boolean critCheck() {
		double check = Math.random();
		boolean critHit = check < attacker.getStat(Stat.CRIT)*0.02;
		damage = critHit ? damage*2 : damage; //not sure if here or execute
		return critHit;
	}

	protected boolean targetReduct(Monster target, String blockedPrompt) { //maybe look over
		Stat hitStat = Monster.getHitStat(attType), blockStat = Monster.getBlockStat(attType);
		damage -= (int)(Math.random()*(target.getStat(blockStat)*.65));
		
		int minDam = (int)attacker.getStat(hitStat);
		float stat = target.getStat(blockStat);
		minDam -= ((int)stat/2);

		damage = minDam > damage ? minDam : damage; //floor to minDam
		boolean blocked = damage <= 0;
		if (blocked)
			Interface.writeOut(blockedPrompt);

		return blocked;
	}

	protected float setAttMod(float tempVal) {
		float ret = attMod;
		attMod = tempVal;
		return ret;
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
	public boolean resolved() { //check if multi turn, see if ability finished
		return true;
	}

	@Override
	public String toString() {
		return name + " - " + manaCost + " mana\n\t" + description;
	}

	/**
	 * attacker deals damage to target, and the damage is logged
	 */
	public static void dealDamage (Monster attacker, Monster target, float damage) {
		target.modStat(Stat.HP, true, -damage);
		target.addDamTurn(damage);
		Interface.FIGHT.addLog(attacker, target, damage);
	}

	public abstract void execute();

}