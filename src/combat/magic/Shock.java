package combat.magic;

import assets.Monsters;
import combat.*;

public class Shock extends Ability {

	public Shock () {
		name = "Shock";
		description = "A a magic attack with the same damage as a basic, but has chance to stun and ignores some armor";
		attType = false;
		targets = new Monsters[numTar];
		priority = true;
		manaCost = 6;
		baseDamMod = 1.5;
	}
	
	public void execute() {
		if (attacker.getStat("mp") >= manaCost) {
			attacker.modStat("mp", -manaCost);
			if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
				
				baseDamage();
				targetReduct(targets[0]);
				
				if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					System.out.println(attacker.name + "'s magic blast was resisted by " + targets[0].name);
				} else {
					System.out.println(attacker.name + " blasts " + targets[0].name + " for " + baseDam + " damage");
					loseHp(targets[0], baseDam);
					
					if (attackCheck(targets[0], 0.4)) {
						System.out.println(attacker.name + "'s blast stuns " + targets[0].name);
						targets[0].setStatus("stun", true);
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
