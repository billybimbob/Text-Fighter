package combat.moves.magic;

import assets.*;
import combat.moves.Ability;
import combat.Status;
import main.Interface;

public class Shock extends Ability {

	public Shock (Monster user) {
		super(user);
		name = "Shock";
		description = "A quick magic attack that also buffs speed of user";
		attType = false;
		priority = true;
		manaCost = 6;
		damageMod = 1.5f;
	}
	
	public void execute() {
		Monster[] targets = attacker.getTargets();
		
		String failPrompt = attacker.getName() + "'s spell failed";
		if (enoughMana() && attackHit(targets[0], failPrompt)) {
			
			String blockedPrompt = attacker.getName() + "'s shock was resisted by " + targets[0].getName();
			if (!targetReduct(targets[0], blockedPrompt)) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				
				dealDamage(attacker, targets[0], damage);
				Interface.writeOut(attacker.getName() + " blasts " + targets[0].getName() + " for " + damage + " damage");
				
				attacker.setStatus(Status.DODGE, 3); //keep eye on
				Interface.writeOut(attacker.getName() + " gains increased evasiveness for 3 turns");
			}

		}
	}
}
