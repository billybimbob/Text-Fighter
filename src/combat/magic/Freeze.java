package combat.magic;

import assets.Monsters;
import combat.Ability;

public class Freeze extends Ability {
	
	public Freeze () {
		name = "Freeze";
		description = "A a magic attack with the less damage, but lowers target's speed and evasion";
		attType = false;
		targets = new Monsters[numTar];
		manaCost = 2;
		baseDamMod = .75;
	}
	
	public void execute() {
		if (attacker.mp >= manaCost) {
			//Attack based on RNG and modified by stats, need to consider magic attack
			attacker.mp -= manaCost;
			if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
				baseDamage();
				int statDam = 1;
				
				targetReduct(targets[0]);
				if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					System.out.println(attacker.name + "'s freeze was resisted by " + targets[0].name);
				} else {
					loseHp(targets[0], baseDam);
					System.out.println(attacker.name + " freezes " + targets[0].name + " for " + baseDam + " damage");
					if (targets[0].spe > 0) {
						targets[0].spe -= statDam;
						System.out.println(targets[0].name + "'s speed was lowered by " + statDam);
					}
				}
				
			} else {
				System.out.println(attacker.name + "'s attack missed");
			}
		} else {
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
		}
	}
	
}
