package combat.moves.magic;

import assets.Stat;
import assets.Monster;
import combat.moves.Ability;
import main.Interface;

public class Freeze extends Ability {
	
	public Freeze (Monster user) {
		super(user);
		name = "Freeze";
		description = "A a magic attack with the less damage, but lowers target's speed and evasion";
		attType = false;
		manaCost = 2;
		damageMod = 0.75f;
	}
	
	protected void execute() {

		Monster target = this.getTarget();
		String failPrompt = attacker.getName() + "'s spell failed";
		if (attackHit(failPrompt)) {
			//Attack based on RNG and modified by stats, need to consider magic attack

			String blockedPrompt = attacker.getName() + "'s freeze was resisted by " + target.getName();
			if (!targetReduct(blockedPrompt)) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				
				String damPrompt = attacker.getName() + " freezes " + target.getName() + " for " +damage + " damage";
				dealDamage(damPrompt);
				
				if (target.getStat(Stat.SPEED) > 0) {
					int statDam = (int)(damage*0.5);		
					target.modStat(Stat.SPEED, true, -statDam);
					Interface.writeOut(target.getName() + "'s speed was lowered by " + statDam);
				}
			}

		}
	}
	
}
