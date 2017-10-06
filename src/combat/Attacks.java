package combat;

import assets.Monsters;

public abstract class Attacks {

	public double baseDam, manaCost;
	public boolean attType;
	public Monsters attacker;
	public Monsters[] targets; //might make it an array
	public int numTar = 1, tarCount = 0; //number of targets default set to 1
	public String name, description;
	
	public void baseDamage () { //determines the damage if either a melee or magic attack
		if (attType)
			this.baseDam = (int)(Math.random() * (this.attacker.att));
		else 
			this.baseDam = (int)(Math.random() * (this.attacker.mag));
	}
	
	public boolean critCheck () {
		double check = Math.random();
		boolean critHit = check < attacker.crit*0.01;		
		
		return critHit;
	}
	
	public double attackCheck (Monsters target) { //an attack check based on either the att or mag stat
		if (attType)
			return Math.random() + (attacker.att*0.5-targets[0].eva*0.3);
		else
			return Math.random() + (attacker.mag*0.5-targets[0].eva*0.3);
	}
	
	public void targetReduct (Monsters target) {
		if (attType)
			baseDam -= (int)(Math.random()*(target.def*.75));
		else
			baseDam -= (int)(Math.random()*(target.magR*.75));
		
		if (baseDam < 0)
			baseDam = 0;
	}
	
	public void setTarget (Monsters target) { //probably useless
		if (tarCount >= numTar)
			tarCount = 0;
		targets[tarCount] = target;
		tarCount++;
	}
	
	public abstract void execute ();
}
