package combat;

import assets.*;
import main.Fight;

public abstract class Ability implements Cloneable {

	protected String name, description;
	protected float manaCost, baseDam, baseDamMod;
	protected Monster attacker;
	protected int numTar; //no limit is -1
	protected boolean attType, aoe, priority, selfTar, passive; //aoe attacks can't work with Monster
	
	public Ability(Monster attacker) {
		this.aoe = false;
		this.priority = false;
		this.passive = true;
		this.numTar = 1;
		this.attacker = attacker;
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

	//getters
	public String getName() {
		return name;
	}
	public double getCost() {
		return manaCost;
	}
	public boolean getAoe() {
		return aoe;
	}
	public boolean getPriority() {
		return priority;
	}
	public boolean getSelfTar() {
		return selfTar;
	}
	public boolean getPassive() {
		return passive;
	}
	

	//static methods
	public static void loseHp (Monster attacker, Monster target, float damage) {
		target.modStat("hp", -damage);
		target.addDamTurn(damage);
		Fight.statusCheck(attacker, target, "reflect", damage);
	}


	//combat calculations
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



	@Override
	public String toString() {
		return name + " - " + manaCost + " mana\n\t" + description;
	}

	public abstract void execute(Monster... targets);

}