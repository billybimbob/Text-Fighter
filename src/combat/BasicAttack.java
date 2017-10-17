package combat;

import assets.Monsters;

public class BasicAttack extends Attacks {

	public BasicAttack () {
		this.name = "Basic Attack";
		this.description = "A basic attack based off of the attack or magic skill of the attacker and modifiers with a chance to crit";
		this.targets = new Monsters[numTar];
	}
	
	public void execute() { //might need to change how the target is handled
		//Attack based on RNG and modified by stats, need to consider magic attack
		if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
			
			baseDamage();
			if (critCheck()) { //Checks for critical hit
				baseDam *= 2;
				System.out.print("Critical Hit! ");
			} else {
				targetReduct(targets[0]);
			}
			
			if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				System.out.println(attacker.name + "'s attack was blocked by " + targets[0].name);
			} else {
				targets[0].hp -= baseDam;
				System.out.println(attacker.name + " has hit " + targets[0].name + " for " + baseDam + " damage");
			}
		} else {
			System.out.println(attacker.name + "'s attack missed");
		}
	}
}
