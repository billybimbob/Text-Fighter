package combat.magic;

import assets.Monster;
import combat.*;
import main.Interface;

public class Shock extends Ability {

	public Shock (Monster attacker) {
		super(attacker);
		name = "Shock";
		description = "A a magic attack with the same damage as a basic, but has chance to stun and ignores some armor";
		attType = false;
		priority = true;
		manaCost = 6;
		baseDamMod = 1.5f;
	}
	
	public void execute(Monster... targets) {
		if (enoughMana()) {
			attacker.modStat("mp", -manaCost);
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				targetReduct(targets[0]);
				
				if (damDealt()) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					Interface.writeOut(attacker.getName() + "'s magic blast was resisted by " + targets[0].getName());
				} else {
					Interface.writeOut(attacker.getName() + " blasts " + targets[0].getName() + " for " + baseDam + " damage");
					loseHp(attacker, targets[0], baseDam);
					
					if (attackHit(targets[0], 0.4)) {
						Interface.writeOut(attacker.getName() + "'s blast stuns " + targets[0].getName());
						targets[0].setStatus("stun", true);
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
