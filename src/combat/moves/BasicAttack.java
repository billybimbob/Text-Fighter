package combat.moves;

import main.Interface;
import assets.Monster;

public class BasicAttack extends Ability {

	public BasicAttack (Monster user) {
		super(user);
		name = "Basic Attack";
		description = "A basic attack based off of the attack or magic skill of the attacker with a chance to crit";
	}
	
	protected void execute(Monster target) { //might need to change how the target is handled
		//Attack based on RNG and modified by stats, need to consider magic attack
		attType = attacker.getAttType();

		String missPrompt = attacker.getName() + "'s attack missed";
		if (attackHit(target, missPrompt)) { //check if attack will be successful

			boolean blocked = false;
			if (!critCheck()) { //reduce damage on failure
				String blockedPrompt = attacker.getName() + "'s attack was blocked by " + target.getName();
				blocked = targetReduct(target, blockedPrompt);
			}
			
			if (!blocked) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				Interface.writeOut(attacker.getName() + " has hit " + target.getName() + " for " + damage + " damage");
				dealDamage(attacker, target, damage);
			}
			
		}
	}
}
