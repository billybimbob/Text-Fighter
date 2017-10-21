package combat;

import assets.*;

public abstract class Attacks implements Cloneable {

	public double baseDam, manaCost;
	public boolean attType, aoe = false;
	public Monsters attacker;
	public Monsters[] targets; //might make it an array
	public int numTar = 1, tarCount = 0; //number of targets default set to 1
	protected double baseDamMod = 1;
	public String name, description;
	
	public Object clone() throws CloneNotSupportedException { //can't use copy constructor because the subclasses are the constructors
		return super.clone();  
	}
	
	public void setTarget (Monsters target) { //probably useless
		if (tarCount >= numTar)
			tarCount = 0;
		targets[tarCount] = target;
		tarCount++;
	}
	
<<<<<<< HEAD
	//fighting calculations
=======
	//critical hit check
>>>>>>> branch 'master' of https://github.com/billybimbob/Text-Fighter.git
	public boolean critCheck () {
		double check = Math.random();
		boolean critHit = check < attacker.crit*0.02;		
		
		return critHit;
	}
	public boolean attackCheck (Monsters target, double checkMod) { //an attack damage check based on either the att or mag stat
		double checkNum;
		if (attType) {
			checkNum = Math.random()*attacker.att - Math.random()*targets[0].spe*.5;
			return checkNum > target.def*checkMod;
		} else {
			checkNum = Math.random()*attacker.mag - Math.random()*targets[0].spe*.5;
			return checkNum > target.magR*checkMod;
		}
	}
	public void baseDamage () { //determines the damage if either a melee or magic attack
		if (attType)
			this.baseDam = (int)(Math.random()*(this.attacker.att*baseDamMod)+1);
		else 
			this.baseDam = (int)(Math.random()*(this.attacker.mag*baseDamMod)+1);
	}
	public void targetReduct (Monsters target) {
		if (attType)
			baseDam -= (int)(Math.random()*(target.def*.5));
		else
			baseDam -= (int)(Math.random()*(target.magR*.5));
		
		if (baseDam < 0)
			baseDam = 1;
	}
	
	public abstract void execute ();
}