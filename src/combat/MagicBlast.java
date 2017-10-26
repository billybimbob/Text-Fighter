package combat;

import assets.Monsters;

public class MagicBlast extends Attacks {

	public MagicBlast () {
		name = "Magic Blast";
		description = "A a magic attack with the same damage as a basic, but has chance to stun and ignores some armor";
		attType = false;
		targets = new Monsters[numTar];
		manaCost = 2;
		baseDamMod = 1.5;
	}
	
	public void execute() {
		if (attacker.mp >= manaCost) {
			//Attack based on RNG and modified by stats, need to consider magic attack
			attacker.mp -= manaCost;
			if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				targetReduct(targets[0]);
				
				if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					System.out.println(attacker.name + "'s magic blast was resisted by " + targets[0].name);
				} else {
					loseHp(targets[0], baseDam);
					System.out.println(attacker.name + " blasts " + targets[0].name + " for " + baseDam + " damage");
					
					if (attackCheck(targets[0], 0.4)) {
						System.out.println(attacker.name + "'s blast stuns " + targets[0].name);
						targets[0].status[3] = 1;
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
