package combat.moves.melee;

import assets.Monster;
import combat.moves.Ability;
import main.Interface;

public class SpinAttack extends Ability {

	
	public SpinAttack (Monster user) { //might change to get rid of enemies parameter; max amount of targets is 5
		super(user);
		name = "Spin Attack";
		description = "A spinning melee attack that damages all enemies for less damage than a basic attack";
		attType = true; //melee attack; might make it based on the attacker
		numTar = -1;
		manaCost = 5;
		damageMod = 1.25f;
	}

	@Override
	protected boolean preExecute() {
		String attPrompt = attacker.getName() + " spins around";
		return enoughMana(attPrompt);
	}
	
	protected void execute(Monster target) {
		String missPrompt = target.getName() + " dodges the attack";
		if (attackHit(target, missPrompt)) { //Check if attack will be successful
			
			String blockedPrompt = target.getName() + " blocks all damage";
			if (!targetReduct(target, blockedPrompt)) {	
				dealDamage(attacker, target, damage);
				Interface.writeOut(target.getName() + " gets hit for " + damage + " damage");
			}
		}
	}

}
