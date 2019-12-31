package combat.moves.melee;

import assets.Stat;
import assets.Monster;
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
	
	protected void execute() {
		Monster attacker = this.getAttacker();
		Monster target = this.currentTarget();

		String missPrompt = attacker.getName() + "'s attack missed";
		if (attackHit(missPrompt)) { //Check if attack will be successful
			
			Interface.prompt(attacker.getName() + " slams into " + target.getName());

			String blockedPrompt = "but was resisted";
			if (!targetReduct(blockedPrompt)) { //check if the defense reduction value is greater than the attack, therefore blocking the attack
				
				String damPrompt = " for " + damage + " damage";
				dealDamage(damPrompt);

				float sto = setAttMod(0.4f);
				if (attackHit()) { //mods damage
					String stunPrompt = attacker.getName() + "'s blow also stuns " + target.getName();
					applyStatus(Status.STUN, true, stunPrompt);
				}
				setAttMod(sto);
			}
			
			if (damage > 0) {
				attacker.modStat(Stat.HP, true, -damage); //might add to damage received in turn?
				Interface.writeOut(attacker.getName() + " deals " + damage + " damage to self from recoil");
			}
			
		}
	}

}
