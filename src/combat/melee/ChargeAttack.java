package combat.melee;

import assets.Monsters;
import combat.Ability;
import main.Interface;

public class ChargeAttack extends Ability {
	
	private int turnCount = 0;
	
	public ChargeAttack () {
		name = "Charged Strike";
		description = "A melee attack able to hit with twice accuarcy and damage, ignores armor, but requires a turn to charge, and more vulnerable";
		attType = true;
		targets = new Monsters[numTar];
		manaCost = 6;
		baseDamMod = 3;
	}
	
	public void execute() { //Change, too messy, might put the print statements in the fight class
		if (attacker.getStat("mp") >= manaCost && turnCount == 0) { //checks if sufficient mana, and starts charged turn
			attacker.modStat("mp", -manaCost);
			System.out.println(attacker.name + " readies their swing");
			attacker.modStat("mp", -3);
			turnCount++;
			if (attacker.aggro)
				Interface.heroAction = true;
			else
				attacker.storeTurn = this;
		} else if (turnCount == 1) { //checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats
			if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				targetReduct(targets[0]);
				if (critCheck()) { //Checks for critical hit
					baseDam *= 2;
					System.out.print("Critical Hit! ");
				}
				
				System.out.println(attacker.name + " lands a powerful hit on " + targets[0].name + " for " + baseDam + " damage");
				loseHp(targets[0], baseDam);
				
				if (attackCheck(targets[0], 0.1)) {
					System.out.println(attacker.name + "'s charged attack stuns " + targets[0].name);
					targets[0].setStatus("stun", true);
				}
			} else {
				System.out.println(attacker.name + "'s attack missed");
			}
			attacker.modStat("mp", 3); //might later set to a variable
			turnCount = 0;
			if (attacker.aggro) //use store move and turnMove?
				Interface.heroAction = false;
			else
				attacker.storeTurn = null;
			
		} else
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
	}
}
