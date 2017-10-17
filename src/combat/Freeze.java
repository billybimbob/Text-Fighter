package combat;

import assets.Monsters;

public class Freeze extends Attacks {
	
	public Freeze () {
		this.name = "Freeze";
		this.description = "A a magic attack with the less damage, but lowers target's speed and evasion";
		this.attType = false;
		this.targets = new Monsters[numTar];
		this.manaCost = 2;
		baseDamMod = .75;
	}
	
	public void execute() {
		//Attack based on RNG and modified by stats, need to consider magic attack
		attacker.mp -= manaCost;
		if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
			
			baseDamage();
			int statDam = 1;
			targets[0].spe -= statDam;
			targetReduct(targets[0]);
			if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				System.out.println(attacker.name + "'s freeze was resisted by " + targets[0].name);
			} else {
				targets[0].hp -= baseDam;
				System.out.println(attacker.name + " freezes " + targets[0].name + " for " + baseDam + " damage\n" + targets[0].name + "'s speed was lowered by " + statDam);
			}
		} else {
			System.out.println(attacker.name + "'s attack missed");
		}
	}
}
