package combat.moves.magic;

import assets.chars.Monster;
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

	protected void execute(Monster target) {
		String failPrompt = attacker.getName() + "'s spell failed";
		if (attackHit(target, failPrompt)) {
			
			String blockedPrompt = attacker.getName() + "'s shock was resisted by " + target.getName();
			if (!targetReduct(target, blockedPrompt)) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				
				dealDamage(attacker, target, damage);
				Interface.writeOut(attacker.getName() + " blasts " + target.getName() + " for " + damage + " damage");
				
				attacker.setStatus(Status.DODGE, 3); //keep eye on
				Interface.writeOut(attacker.getName() + " gains increased evasiveness for 3 turns");
			}

		}
	}
}
