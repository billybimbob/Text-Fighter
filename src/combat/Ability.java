package combat;

import java.util.ArrayList;
import assets.*;
import main.Fight;

public abstract class Ability implements Cloneable {

	protected String name, description;
	protected Monsters attacker;
	protected double manaCost, baseDam;
	protected boolean attType, aoe = false, priority = false, selfTar = false, passive = false; //aoe attacks can't work with monsters
	protected Monsters[] targets;
	protected int numTar = 1, tarCount = 0; //number of targets default set to 1
	protected double baseDamMod = 1;
	
	//getters
	public String getName() {
		return name;
	}
	public Monsters getAttacker() {
		return attacker;
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
	public Monsters[] getTargets() {
		return targets;
	}
	
	//setters
	public void setAttacker(Monsters attacker) {
		this.attacker = attacker;
	}
	public void setNumTar (int numTar) {
		this.numTar = numTar;
		targets = new Monsters[numTar];
	}
	public void setTarget (Monsters target) { //probably useless
		if (tarCount >= numTar)
			tarCount = 0;
		targets[tarCount++] = target;
	}
	public void setAllTar (ArrayList<Monsters> enemies) {
		this.setNumTar(enemies.size());
		//turnMove.targets = new Monsters[turnMove.numTar];
		for (Monsters enemy: enemies) {
			setTarget(enemy);
		}
	}
	
	public Object clone() throws CloneNotSupportedException { //can't use copy constructor because the subclasses are the constructors
		return super.clone();  
	}
	
	//combat calculations
	public boolean critCheck () {
		double check = Math.random();
		boolean critHit = check < attacker.getStat("crit")*0.02;		
		return critHit;
	}
	public boolean attackCheck (Monsters target, double checkMod) { //an attack damage check based on either the att or mag stat
		String hitStat = attType?"att":"mag", blockStat = attType?"def":"magR";
		double checkNum = Math.random()*attacker.getStat(hitStat) - Math.random()*target.getStat("spe")*.5;
		return checkNum > target.getStat(blockStat)*checkMod;
	}

	public void baseDamage () { //determines the damage if either a melee or magic attack
		String hitStat = attType?"att":"mag";
		baseDam = (int)(Math.random()*(attacker.getStat(hitStat)*baseDamMod)+1);
	}

	public void targetReduct (Monsters target) {
		String blockStat = attType?"def":"magR";
		baseDam -= (int)(Math.random()*(target.getStat(blockStat)*.65));
		
		target.setMinDam(attacker, attType);
		if (baseDam < target.minDam) {
			//System.out.println("minDam");
			baseDam = target.minDam;
		}
	}

	public void loseHp (Monsters target, double damage) {
		target.modStat("hp", -damage);
		target.damTurn += damage;
		Fight.statusCheck(attacker, target, "reflect", damage);
	}

	public String toString() {
		return name + " - " + manaCost + " mana\n\t" + description;
	}

	public abstract void execute ();

}