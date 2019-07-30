package combat.moves.melee;

import assets.*;
import combat.*;
import main.Interface;

public class SpinAttack extends Ability {

	
	public SpinAttack (Monster user) { //might change to get rid of enemies parameter; max amount of targets is 5
		super(user);
		name = "Spin Attack";
		description = "A spinning melee attack that damages all enemies for less damage than a basic attack";
		attType = true; //melee attack; might make it based on the attacker
		numTar = -1;
		manaCost = 5;
		damageMod = 1.25f;
	}
	
	public void execute() { //current bug with null pointer
		Monster[] targets = attacker.getTargets();

		if (enoughMana()) {
			Interface.writeOut(attacker.getName() + " spins around");
			
			for (Monster target: targets) { //Checks if hits for each monster; Attack based on RNG and modified by stats
				
				String missPrompt = target.getName() + " dodges the attack";
				if (attackHit(target, missPrompt)) { //Check if attack will be successful
					/*if (critCheck()) { //Might add later
						baseDam *= 2;
						Interface.writeOut("Critical Hit!");
					}*/
					String blockedPrompt = "but is blocked by " + target.getName();
					if (!targetReduct(target, blockedPrompt)) {	
						dealDamage(attacker, target, damage);
						Interface.writeOut(target.getName() + " gets hit for " + damage + " damage");
					}
				}
			}
		
		}
	}

}
