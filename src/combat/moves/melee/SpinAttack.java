package combat.moves.melee;

import assets.Monster;
import combat.moves.Ability;

public class SpinAttack extends Ability {

	public SpinAttack (Monster user) { //might change to get rid of enemies parameter; max amount of targets is 5
		super(user);
		name = "Spin Attack";
		description = "A spinning melee attack that damages all enemies for less damage than a basic attack";
		attType = true; //melee attack; might make it based on the attacker
		numTar = -1;
		manaCost = 6;
	}

	@Override
	protected boolean preExecute() {
		String attPrompt = this.getAttacker().getName() + " spins around";
		return enoughMana(attPrompt);
	}
	
	protected void execute() {
		Monster target = this.currentTarget();
		String missPrompt = target.getName() + " dodges the attack";
		if (attackHit(missPrompt)) { //Check if attack will be successful
			
			String blockedPrompt = target.getName() + " blocks all damage";
			if (!targetReduct(blockedPrompt)) {	
				String damPrompt = target.getName() + " gets hit for " + damage + " damage";
				dealDamage(damPrompt);
			}
		}
	}

}
