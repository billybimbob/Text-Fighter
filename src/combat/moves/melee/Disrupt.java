package combat.moves.melee;

import assets.*;
import combat.*;
import main.Interface;

public class Disrupt extends Ability {

	public Disrupt(Monster user) {
		super(user);
		name = "Disrupt";
		description = "A quick bash disrupting a target while injuring yourself";
		attType = true;
		priority = true;
		attMod = 0.02f;
		manaCost = 5;
	}
	
	public void execute() {
		Monster[] targets = attacker.getTargets();

		String missPrompt = attacker.getName() + "'s attack missed";
		if (enoughMana() && attackHit(targets[0], missPrompt)) { //Check if attack will be successful
			
			Interface.prompt(attacker.getName() + " slams into " + targets[0].getName());
			String blockedPrompt = "but was resisted";
			if (!targetReduct(targets[0], blockedPrompt)) { //check if the defense reduction value is greater than the attack, therefore blocking the attack
				dealDamage(attacker, targets[0], damage);
				Interface.writeOut(" for " + damage + " damage");

				float sto = setAttMod(0.4f);
				if (attackHit(targets[0], null)) {
					Interface.writeOut(attacker.getName() + "'s blow also stuns " + targets[0].getName());
					targets[0].setStatus(Status.STUN, true);
				}
				setAttMod(sto);
			}
			
			float selfDam = (int)(damage*.5);
			if (selfDam > 0) {
				attacker.modStat(Stat.HP, true, -selfDam); //might add to damage received in turn?
				Interface.writeOut(attacker.getName() + " deals " + selfDam + " damage to self from recoil");
			}
			
		}
	}

}
