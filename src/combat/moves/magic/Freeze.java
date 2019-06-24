package combat.moves.magic;

import assets.Monster;
import combat.Ability;
import main.Interface;

public class Freeze extends Ability {
	
	public Freeze (Monster user) {
		super(user);
		name = "Freeze";
		description = "A a magic attack with the less damage, but lowers target's speed and evasion";
		attType = false;
		manaCost = 2;
		baseDamMod = 0.75f;
	}
	
	public void execute() {
		Monster[] targets = attacker.getTargets();

		if (enoughMana()) {
			//Attack based on RNG and modified by stats, need to consider magic attack
			attacker.modStat("mp", -manaCost);
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				baseDamage();
				int statDam = 1;
				
				targetReduct(targets[0]);
				if (damDealt()) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					Interface.writeOut(attacker.getName() + "'s freeze was resisted by " + targets[0].getName());
				} else {
					Interface.writeOut(attacker.getName() + " freezes " + targets[0].getName() + " for " + baseDam + " damage");
					dealDam(attacker, targets[0], baseDam);
					if (targets[0].getStat("spe") > 0) {
						targets[0].modStat("spe", -statDam);
						Interface.writeOut(targets[0].getName() + "'s speed was lowered by " + statDam);
					}
				}
				
			} else {
				Interface.writeOut(attacker.getName() + "'s attack missed");
			}
		} else {
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
		}
	}
	
}
