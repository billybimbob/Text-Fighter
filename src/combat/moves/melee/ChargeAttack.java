package combat.moves.melee;

import assets.*;
import combat.*;
import main.Interface;

public class ChargeAttack extends Ability {
	
	private int turnCount;
	
	public ChargeAttack (Monster user) {
		super(user);
		name = "Charged Strike";
		description = "A melee attack able to hit with twice accuarcy and damage, ignores armor, but requires a turn to charge, and more vulnerable";
		attType = true;
		manaCost = 6;
		damageMod = 3;
		turnCount = 0;
	}

	@Override
	public boolean resolved() {
		return turnCount == 0; //means reset count
		//return Interface.FIGHT.getTurnNum()-start > duration;
	}
	
	public void execute() { //Change, too messy, might put the print statements in the fight class
		Monster[] targets = attacker.getTargets();
		
		if (turnCount == 1) { //checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				
				if (critCheck()) //Checks for critical hit
					Interface.prompt("Critical Hit! ");
				else
					targetReduct(targets[0]);
				
				Interface.writeOut(attacker.getName() + " lands a powerful hit on " + targets[0].getName() + " for " + damage + " damage");
				dealDamage(attacker, targets[0], damage);
				
				if (attackHit(targets[0], 0.1)) {
					Interface.writeOut(attacker.getName() + "'s charged attack stuns " + targets[0].getName());
					targets[0].setStatus(Status.STUN, true);
				}

			} else
				Interface.writeOut(attacker.getName() + "'s attack missed");
			
			turnCount = 0;
			
		} else if (enoughMana() && turnCount == 0) { //checks if sufficient mana, and starts charged turn
			Interface.writeOut(attacker.getName() + " readies their swing");
			turnCount++;
		
		}
	}
}
