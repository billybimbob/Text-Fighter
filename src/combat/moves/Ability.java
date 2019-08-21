package combat.moves;

import java.util.List;
import java.util.ArrayList;

import assets.Stat;
import assets.Monster;
import combat.Status;
import combat.FightLog.Log;
import main.Interface;

public abstract class Ability implements Cloneable {

	private Monster target; //changes with useAbility
	protected String name, description;
	protected float manaCost, attMod, damage, damageMod;
	protected Monster attacker;
	protected int numTar;
	protected boolean attType, priority, passive; //aoe attacks can't work with Monster
	protected List<Status> afflicted; //could be expensive

	protected Ability() {
		this.priority = false;
		this.passive = false;
		this.numTar = 1;
		this.manaCost = 0;
		this.damage = 0;
		this.attMod = 0.01f;
		this.damageMod = 1;
		this.afflicted = new ArrayList<>();
	}

	/**
	 * set default values for an abiltiy
	 */
	protected Ability(Monster user) {
		this();
		this.attacker = user;
	}

	public Object clone(Monster attacker) { //can't do copy constructor becuase of subclasses
		try {
			Ability newAbility = (Ability)super.clone();
			newAbility.attacker = attacker;
			newAbility.damage = 0;
			newAbility.afflicted = new ArrayList<>(); //not sure
			return newAbility;
		} catch (CloneNotSupportedException e) {
			System.err.println("issue cloning ability");
			return null;
		}
	}


	/* 
	 * combat calculations, helper functions
	 */

	/**
	 * checks if attacker has enough mana cost for ability; uses mana on success and prints prompt on failure
	 * @return true if attacker has enough mana, false if not
	 */
	protected boolean enoughMana(String successPrompt) {
		boolean check = attacker.getStat(Stat.MP) >= manaCost;
		if (check) {//not sure if I want to pair; immediately revmoves cost if able
			attacker.modStat(Stat.MP, true, -manaCost);
			if (successPrompt != null)
				Interface.writeOut(successPrompt);

		} else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
			
		return check;
	}

	protected boolean enoughMana() {
		return enoughMana(null);
	}

	/**
	 * checks whether attacks lands on target; based on att/mag, attMod, and speed
	 * damage calculated on success;
	 * attack damage check based on either the att or mag stat
	 * @param failPrompt prints prompt if attack misses; {@code null} prints nothing on miss
	 * @return {@code true} if attack hits, {@code false} if attack misses
	 */
	protected boolean attackHit(String failPrompt) {
		Stat hitStat = Stat.getHitStat(attType);
		Stat blockStat = Stat.getBlockStat(attType);
		double checkNum = Math.random()*attacker.getStat(hitStat) - Math.random()*target.getStat(Stat.SPEED)*0.5;
		boolean check = checkNum > target.getStat(blockStat)*attMod;
		
		if (check) //determines damage if successful
			damage = (int)(Math.random()*(attacker.getStat(hitStat)*damageMod)+1);
		else if (failPrompt != null)
			Interface.writeOut(failPrompt);

		return check;
	}

	/**
	 * checks whether attacks lands on target; damage calculated on success; nothing done on fail
	 * @see {@link Ability#attackHit(Monster, String)}
	 */
	protected boolean attackHit() {
		return attackHit(null);
	}
	
	/**
	 * checks if attack is a critical hit; prints prompt on success
	 * @return true if critical check was successful, false if not
	 */
	protected boolean critCheck() {
		double check = Math.random();
		boolean critHit = check < attacker.getStat(Stat.CRIT)*0.02;
		if (critHit) {
			damage *= 2;
			Interface.prompt("Critical Hit! ");
		}

		return critHit;
	}

	/**
	 * modfies(lowers) damage value based on target's defense stat
	 * @param blockedPrompt prints out the value if return value is {@code true}
	 * @return {@code true} if modified damage value is less than or equal to 0
	 */
	protected boolean targetReduct(String blockedPrompt) { //maybe look over
		Stat hitStat = Stat.getHitStat(attType), blockStat = Stat.getBlockStat(attType);
		damage -= (int)(Math.random()*(target.getStat(blockStat)));
		
		int minDam = (int)attacker.getStat(hitStat);
		float stat = target.getStat(blockStat);
		minDam -= ((int)stat/2);

		damage = minDam > damage ? minDam : damage; //floor to minDam
		boolean blocked = damage <= 0;
		if (blocked)
			Interface.writeOut(blockedPrompt);

		return blocked;
	}

	/**
	 * modifies attMod value
	 * @param tempVal new value to change attMod to
	 * @return old value of attMod
	 * */
	protected float setAttMod(float newVal) {
		float ret = attMod;
		attMod = newVal;
		return ret;
	}

	protected boolean preExecute() {
		return enoughMana();
	}

	/**use ability on specific target */
	protected abstract void execute();


	/*
	 * getters
	 */
	protected Monster getTarget() {
		return this.target;
	}
	public String getName() {
		return name;
	}
	public float getManaCost() {
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
	public boolean resolved() { //check if multi turn, see if ability finished
		return true;
	}


	/**
	 * can return null if state inconsistent
	 */
	private Log createLog() {
		return target == null
			? null
			: new Log(attacker, target, this, damage, afflicted);
	}
	
	/**
	 * attacker deals damage to target, and the damage is logged
	 */
	public void dealDamage (String damPrompt) {
		target.modStat(Stat.HP, true, -damage);
		Interface.writeOut(damPrompt);

		if (target.getStatus(Status.REFLECT) > -1) { //not sure if I want to check every time
			float refDam = (int)(damage*0.75f);
			attacker.modStat(Stat.HP, true, -refDam);
			Interface.writeOut(target.getName() + " reflects " + refDam 
				+ " damage to " + attacker.getName());
		}
			
		Interface.currentFight().getLogs()
			.addLog(this.createLog());
	}

	public void useAbility() { //not sure if needs to be static
		if (this.preExecute()) {
			for (Monster target: attacker.getTargets()) {
				this.target = target;
				this.damage = 0;
				this.execute();
				afflicted.clear();
			}
			this.target = null;
		}
	}
	
	@Override
	public String toString() {
		return name + " - " + manaCost + " mana\n\t" + description;
	}

}