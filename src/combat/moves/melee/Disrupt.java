package combat.moves.melee;

import assets.*;
import combat.Ability;
import main.Interface;

public class Disrupt extends Ability {

	public Disrupt(Monster user) {
		super(user);
		name = "Disrupt";
		description = "A quick bash disrupting a target while injuring yourself";
		attType = true;
		priority = true;
		manaCost = 5;
	}
	
	public void execute() {
		Monster[] targets = attacker.getTargets();
		if (enoughMana()) {
			//Attack based on RNG and modified by stats, need to consider magic attack
			attacker.modStat(Stat.MP, -manaCost);
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				baseDamage();
				targetReduct(targets[0]);
				
				Interface.prompt(attacker.getName() + " slams into " + targets[0].getName());
				if (blocked()) //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					Interface.writeOut(" but was resisted");
				else {
					dealDamage(attacker, targets[0], damage);
					Interface.writeOut(" for " + damage + " damage");
					if (attackHit(targets[0], 0.4)) {
						Interface.writeOut(attacker.getName() + "'s blow also stuns " + targets[0].getName());
						targets[0].setStatus(Status.STUN, true);
					}
				}
				
				float selfDam = (int)(damage*.5);
				if (selfDam > 0) {
					attacker.modStat(Stat.HP, -selfDam); //might add to damage received in turn?
					Interface.writeOut(attacker.getName() + " deals " + selfDam + " damage to self from recoil");
				}
			} else {
				Interface.writeOut(attacker.getName() + "'s attack missed");
			}
		} else {
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
		}
	}

}
