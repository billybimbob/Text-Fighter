package combat.moves.magic;

import assets.*;
import combat.moves.Ability;
import main.Interface;

public class LifeDrain extends Ability {

	public LifeDrain(Monster user) {
		super(user);
		name = "Life Drain";
		description = "A magic attack that heals for for a portion of damage dealt";
		attType = false;
		manaCost = 3;
		damageMod = 1.4f;
	}

	public void execute() {
		Monster[] targets = attacker.getTargets();

		String failPrompt = attacker.getName() + "'s spell failed";
		if (enoughMana() && attackHit(targets[0], failPrompt)) { //Check if attack will be successful
			
			String blockedPrompt = attacker.getName() + "'s drain was resisted by " + targets[0].getName();
			if (!targetReduct(targets[0], blockedPrompt)) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				
				dealDamage(attacker, targets[0], damage);
				Interface.writeOut(attacker.getName() + " drains " + targets[0].getName() + " for " + damage + " damage");

				float baseHeal = (int)(damage*0.5);
				float capOver = attacker.modStat(Stat.HP, true, baseHeal);
				float selfHeal = baseHeal-capOver;
				if (selfHeal > 0)
					Interface.writeOut(attacker.getName() + " absorbs " + selfHeal + " health");
				
			}
		
		}
		
	}
}
