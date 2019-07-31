package combat.moves.melee;

import assets.Stat;
import assets.chars.Monster;
import combat.moves.Ability;
import combat.Status;
import main.Interface;

public class Disrupt extends Ability {

	public Disrupt(Monster user) {
		super(user);
		name = "Disrupt";
		description = "A quick bash disrupting a target while injuring yourself";
		attType = true;
		priority = true;
		attMod = 0.02f;
		manaCost = 7;
	}
	
	protected void execute(Monster target) {
		String missPrompt = attacker.getName() + "'s attack missed";
		if (attackHit(target, missPrompt)) { //Check if attack will be successful
			
			Interface.prompt(attacker.getName() + " slams into " + target.getName());
			float selfDam = 0;

			String blockedPrompt = "but was resisted";
			if (!targetReduct(target, blockedPrompt)) { //check if the defense reduction value is greater than the attack, therefore blocking the attack
				
				dealDamage(attacker, target, damage);
				Interface.writeOut(" for " + damage + " damage");
				selfDam = (int)(damage*0.5);

				float sto = setAttMod(0.4f);
				if (attackHit(target)) {
					Interface.writeOut(attacker.getName() + "'s blow also stuns " + target.getName());
					target.setStatus(Status.STUN, true);
				}
				setAttMod(sto);
			}
			
			if (selfDam > 0) {
				attacker.modStat(Stat.HP, true, -selfDam); //might add to damage received in turn?
				Interface.writeOut(attacker.getName() + " deals " + selfDam + " damage to self from recoil");
			}
			
		}
	}

}
