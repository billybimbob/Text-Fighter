package combat;

import main.Interface;
import assets.Monster;

public class BasicAttack extends Ability {

	public BasicAttack (Monster user) {
		super(user);
		name = "Basic Attack";
		description = "A basic attack based off of the attack or magic skill of the attacker with a chance to crit";
	}
	
	public void execute() { //might need to change how the target is handled
		//Attack based on RNG and modified by stats, need to consider magic attack
		attType = attacker.getAttType();
		Monster[] targets = attacker.getTargets();
		if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
			
			baseDamage();
			if (critCheck()) { //Checks for critical hit
				baseDam *= 2;
				System.out.print("Critical Hit! ");
			} else {
				targetReduct(targets[0]);
			}
			
			if (damDealt()) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				Interface.writeOut(attacker.getName() + "'s attack was blocked by " + targets[0].getName());
			} else {
				Interface.writeOut(attacker.getName() + " has hit " + targets[0].getName() + " for " + baseDam + " damage");
				loseHp(attacker, targets[0], baseDam);
			}
		} else {
			Interface.writeOut(attacker.getName() + "'s attack missed");
		}
	}
}
