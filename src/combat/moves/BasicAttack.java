package combat.moves;

import assets.Monster;

public class BasicAttack extends Ability {

	public BasicAttack (Monster user) {
		super(user);
		name = "Basic Attack";
		description = "A basic attack based off of the attack or magic skill of the attacker with a chance to crit";
		attType = user.getAttType();
	}
	
	protected void execute() { //might need to change how the target is handled
		//Attack based on RNG and modified by stats, need to consider magic attack
		Monster attacker = this.getAttacker();
		Monster target = this.currentTarget();
		String missPrompt = attacker.getName() + "'s attack missed";
		if (attackHit(missPrompt)) { //check if attack will be successful

			boolean blocked = false;
			if (!critCheck()) { //reduce damage on failure
				String blockedPrompt = attacker.getName() + "'s attack was blocked by " + target.getName();
				blocked = targetReduct(blockedPrompt);
			}
			
			if (!blocked) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				String damPrompt = attacker.getName() + " has hit " + target.getName() + " for " + damage + " damage";
				dealDamage(damPrompt);
			}
			
		}
	}
}
