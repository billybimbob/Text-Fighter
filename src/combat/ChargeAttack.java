package combat;

import assets.Monsters;
import main.Interface;

public class ChargeAttack extends Attacks{
	
	public int turnCount = 0;
	
	public ChargeAttack () {
		this.name = "Charged Strike";
		this.description = "A powerful melee attack able to hit with twice the accuarcy and damage and ignores armor\nBut requires a turn to charge, and more vulnerable";
		this.attType = true;
		this.targets = new Monsters[numTar];
		this.manaCost = 6;
		this.baseDamMod = 3;
	}
	
	public void execute() { //Change, too messy, might put the print statements in the fight class
		if (turnCount == 1) { //Checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats
			if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				if (critCheck()) { //Checks for critical hit
					baseDam *= 2;
					System.out.print("Critical Hit! ");
				}
			
				targets[0].hp -= baseDam;
				System.out.println(attacker.name + " lands a powerful hit on " + targets[0].name + " for " + baseDam + " damage");
				
				if (attackCheck(targets[0], 0.1)) {
					System.out.println(attacker.name + "'s charged attack stuns " + targets[0].name);
					targets[0].stun = true;
				}
			} else {
				System.out.println(attacker.name + "'s attack missed");
			}
			attacker.def += 3; //might later set to a variable
			turnCount = 0;
			if (attacker.aggro)
				Interface.heroAction = false;
			
		} else if (attacker.mp >= manaCost && turnCount == 0){ //Checks if sufficient mana
			attacker.mp -= manaCost;
			System.out.println(attacker.name + " readies their swing and uses " + manaCost + " mana");
			attacker.def -= 3;
			turnCount++;
			if (attacker.aggro)
				Interface.heroAction = true;
			else
				attacker.skip = true;
		} else
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
	}
}
