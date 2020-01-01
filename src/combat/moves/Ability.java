package combat.moves;

import java.util.List;
import java.util.ArrayList;

import assets.Stat;
import assets.Monster;
import combat.Status;
import combat.FightLog.Log;
import main.Interface;

public abstract class Ability implements Cloneable {

	private List<Monster> targets;
	private Monster attacker, currTarget; //changes with useAbility
	private List<Status> applied; //maybe add removed status list?

	protected String name, description;
	protected float manaCost, attMod, damage, damageMod;
	protected int numTar;
	protected boolean attType, priority, passive; //aoe attacks can't work with Monster

	protected Ability() {
		this.priority = false;
		this.passive = false;
		this.numTar = 1;
		this.manaCost = 0;
		this.damage = 0;
		this.attMod = 0.01f;
		this.damageMod = 1;
		this.applied = new ArrayList<>();
		this.targets = new ArrayList<>();
	}

	/**
	 * set default values for an abiltiy
	 */
	protected Ability(Monster user) {
		this();
		this.attacker = user;
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
		double checkNum = Math.random()*attacker.getStat(hitStat)
			- Math.random()*currTarget.getStat(Stat.SPEED)*0.5;
		boolean check = checkNum > currTarget.getStat(blockStat)*attMod;
		
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
		damage -= (int)(Math.random()*(currTarget.getStat(blockStat)));
		
		int minDam = (int)attacker.getStat(hitStat);
		float stat = currTarget.getStat(blockStat);
		minDam -= ((int)stat/2);

		damage = minDam > damage ? minDam : damage; //floor to minDam
		boolean blocked = this.damage <= 0;
		if (blocked) {
			this.damage = 0;
			Interface.writeOut(blockedPrompt);
		}

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

	/**
	 * use ability on specific target;
	 * combat damage is determined here
	 */
	protected abstract void execute();


	/*
	 * getters
	 */
	protected Monster getAttacker() { return this.attacker; }
	protected Monster currentTarget() { return this.currTarget;	}

	public String getName() { return name; }
	public float getManaCost() { return manaCost; }
	public int getNumTar() { return numTar;	}
	public boolean getPriority() { return priority;	}
	public boolean isPassive() { return passive; }
	public boolean resolved() { return true; } //check if multi turn, see if ability finished

	void setAttacker(Monster attacker) { this.attacker = attacker; }
	
	/**
	 * can return null if state inconsistent
	 */
	private Log createLog() {
		return currTarget == null
			? null
			: new Log(attacker, currTarget, this, damage, applied);
	}
	
	/**
	 * attacker deals damage to target, can maybe reflect
	 */
	protected void dealDamage (String damPrompt, boolean checkRef) {
		currTarget.modStat(Stat.HP, true, -damage);
		Interface.writeOut(damPrompt);

		if (checkRef && currTarget.getStatus(Status.REFLECT) > -1) { //not sure if I want to check every time
			float refDam = (int)(damage*0.75f);
			attacker.modStat(Stat.HP, true, -refDam);
			Interface.writeOut(currTarget.getName() + " reflects " + refDam 
				+ " damage to " + attacker.getName());
		}
	}

	/**
	 * attacker deals damage to target and damage is also reflected
	 */
	protected void dealDamage (String damPrompt) {
		dealDamage(damPrompt, true);
	}

	/**
	 * applies status effect to {@code currTarget} for specified
	 * amount of turns
	 * @param status
	 * @param duration
	 */
	protected void applyStatus (Status status, int duration, String statusPrompt) {
		this.currTarget.setStatus(status, duration);
		this.applied.add(status);
		if (statusPrompt != null)
			Interface.writeOut(statusPrompt);
	}

	/**
	 * applies status effect to {@code currTarget} for one turn
	 */
	protected void applyStatus(Status status, boolean toggle, String statusPrompt) {
		applyStatus(status, Monster.toggleToDuration(toggle), statusPrompt);
	}


	/*
	 * targeting methods
	 */
	private boolean checkAddAll(List<Monster> possTargets) {
		int numTar = this.getNumTar();

		boolean check = numTar == -1 || numTar >= possTargets.size();
		if (check) {
			targets.clear();
			targets.addAll(possTargets);
		}

		return check;
	}

	private boolean checkSelfTar() {
		boolean check = numTar == 0;
		if (check) {
			targets.clear();
			targets.add(attacker);
		}
		
		return check;
	}
	
	protected void addTarget(Monster add) { //can add targets in subclasses, not sure
		targets.add(add);
	}

	public static boolean checkAutoTar(Ability check, List<Monster> possTargets) {
		return check.checkAddAll(possTargets) || check.checkSelfTar();
	}

	public void pickTargets(List<Monster> possTargets) {
		targets.clear();
		if (!checkAutoTar(this, possTargets)) {
			for (int i = 0; i < possTargets.size()
				&& this.targets.size() < this.getNumTar(); i++) { //gets targets if needed
				
				Monster possTarget = possTargets.get(i);
				if (possTarget.getAggro() != attacker.getAggro())
					this.targets.add(possTarget);
			}
		}
	}

	public void setTargets(List<Monster> newTargs) {
		targets.clear();
		if (numTar == 0 && newTargs.size() != 1 || numTar >= 1 && newTargs.size() > numTar)
			throw new RuntimeException("incorrect amount of targets being added");

		targets.addAll(newTargs);
	}


	public final void useAbility() {
		if (this.preExecute()) {
			for (Monster target: targets) {
				this.currTarget = target;
				this.damage = 0;
				this.execute();
				Interface.currentFight().getLogs().addLog(this.createLog()); //not sure
				applied.clear();
			}
			this.currTarget = null;
		}
	}
	
	@Override
	public String toString() {
		return this.name + " - " + manaCost + " mana\n\t" + description;
	}

}