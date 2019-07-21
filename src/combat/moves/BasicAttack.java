package combat.moves;

import main.Interface;
import combat.Ability;
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

		String missPrompt = attacker.getName() + "'s attack missed";
		if (attackHit(targets[0], missPrompt)) { //check if attack will be successful

			boolean blocked = false;
			if (critCheck()) //Checks for critical hit
				Interface.prompt("Critical Hit! ");
			else {
				String blockedPrompt = attacker.getName() + "'s attack was blocked by " + targets[0].getName();
				blocked = targetReduct(targets[0], blockedPrompt);
			}
			
			if (!blocked) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				Interface.writeOut(attacker.getName() + " has hit " + targets[0].getName() + " for " + damage + " damage");
				dealDamage(attacker, targets[0], damage);
			}
			
		}
	}
}
