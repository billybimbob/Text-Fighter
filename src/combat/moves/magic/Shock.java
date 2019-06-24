package combat.moves.magic;

import assets.Monster;
import assets.Monster.Stat;
import assets.Monster.Status;
import combat.*;
import main.Interface;

public class Shock extends Ability {

	public Shock (Monster user) {
		super(user);
		name = "Shock";
		description = "A a magic attack with the same damage as a basic, but has chance to stun and ignores some armor";
		attType = false;
		priority = true;
		manaCost = 6;
		baseDamMod = 1.5f;
	}
	
	public void execute() {
		Monster[] targets = attacker.getTargets();
		
		if (enoughMana()) {
			attacker.modStat(Stat.MP, -manaCost);
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				targetReduct(targets[0]);
				
				if (damDealt()) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					Interface.writeOut(attacker.getName() + "'s magic blast was resisted by " + targets[0].getName());
				} else {
					Interface.writeOut(attacker.getName() + " blasts " + targets[0].getName() + " for " + baseDam + " damage");
					dealDam(attacker, targets[0], baseDam);
					
					if (attackHit(targets[0], 0.4)) {
						Interface.writeOut(attacker.getName() + "'s blast stuns " + targets[0].getName());
						targets[0].setStatus(Status.STUN, true);
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
