package combat.moves.melee;

import assets.Monster;
import combat.Ability;
import main.Interface;

public class ChargeAttack extends Ability {
	
	private int turnCount;
	
	public ChargeAttack (Monster user) {
		super(user);
		name = "Charged Strike";
		description = "A melee attack able to hit with twice accuarcy and damage, ignores armor, but requires a turn to charge, and more vulnerable";
		attType = true;
		manaCost = 6;
		baseDamMod = 3;
		duration = 2;
		turnCount = 0;
	}

	@Override
	public boolean resolved() {
		return turnCount == 0; //means reset count
	}
	
	public void execute() { //Change, too messy, might put the print statements in the fight class
		
		Monster[] targets = attacker.getTargets();
		if (enoughMana() && turnCount == 0) { //checks if sufficient mana, and starts charged turn
			attacker.modStat("mp", -manaCost);
			Interface.writeOut(attacker.getName() + " readies their swing");
			attacker.modStat("mp", -3);
			turnCount++;
		
		} else if (turnCount == 1) { //checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				targetReduct(targets[0]);
				if (critCheck()) { //Checks for critical hit
					baseDam *= 2;
					Interface.prompt("Critical Hit! ");
				}
				
				Interface.writeOut(attacker.getName() + " lands a powerful hit on " + targets[0].getName() + " for " + baseDam + " damage");
				dealDam(attacker, targets[0], baseDam);
				
				if (attackHit(targets[0], 0.1)) {
					Interface.writeOut(attacker.getName() + "'s charged attack stuns " + targets[0].getName());
					targets[0].setStatus("stun", true);
				}
			} else {
				Interface.writeOut(attacker.getName() + "'s attack missed");
			}
			attacker.modStat("mp", 3); //might later set to a variable
			turnCount = 0;
			
		} else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
	}
}
