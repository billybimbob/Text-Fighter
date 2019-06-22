package combat.melee;

import assets.Monster;
import combat.Ability;
import main.Interface;

public class ChargeAttack extends Ability {
	
	private int turnCount = 0;
	
	public ChargeAttack (Monster attacker) {
		super(attacker);
		name = "Charged Strike";
		description = "A melee attack able to hit with twice accuarcy and damage, ignores armor, but requires a turn to charge, and more vulnerable";
		attType = true;
		manaCost = 6;
		baseDamMod = 3;
	}
	
	public void execute(Monster... targets) { //Change, too messy, might put the print statements in the fight class
		if (enoughMana() && turnCount == 0) { //checks if sufficient mana, and starts charged turn
			attacker.modStat("mp", -manaCost);
			Interface.writeOut(attacker.getName() + " readies their swing");
			attacker.modStat("mp", -3);
			turnCount++;
			if (attacker.aggro)
				Interface.heroAction = true;
			else
				attacker.storeTurn = this;
		} else if (turnCount == 1) { //checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				targetReduct(targets[0]);
				if (critCheck()) { //Checks for critical hit
					baseDam *= 2;
					System.out.print("Critical Hit! ");
				}
				
				Interface.writeOut(attacker.getName() + " lands a powerful hit on " + targets[0].getName() + " for " + baseDam + " damage");
				loseHp(attacker, targets[0], baseDam);
				
				if (attackHit(targets[0], 0.1)) {
					Interface.writeOut(attacker.getName() + "'s charged attack stuns " + targets[0].getName());
					targets[0].setStatus("stun", true);
				}
			} else {
				Interface.writeOut(attacker.getName() + "'s attack missed");
			}
			attacker.modStat("mp", 3); //might later set to a variable
			turnCount = 0;
			if (attacker.aggro) //use store move and turnMove?
				Interface.heroAction = false;
			else
				attacker.storeTurn = null;
			
		} else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
	}
}
