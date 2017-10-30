package combat.melee;

import assets.Monsters;
import combat.Ability;

public class Disrupt extends Ability {

	public Disrupt() {
		name = "Disrupt";
		description = "A quick bash disrupting a target while injuring yourself";
		attType = true;
		targets = new Monsters[numTar];
		priority = true;
		manaCost = 5;
	}
	
	public void execute() {
		if (attacker.mp >= manaCost) {
			//Attack based on RNG and modified by stats, need to consider magic attack
			attacker.mp -= manaCost;
			if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
				baseDamage();
				targetReduct(targets[0]);
				
				System.out.print(attacker.name + " slams into " + targets[0].name);
				if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					System.out.println(" but was resisted");
				} else {
					loseHp(targets[0], baseDam);
					System.out.println(" for " + baseDam + " damage");
					if (attackCheck(targets[0], 0.4)) {
						System.out.println(attacker.name + "'s blow also stuns " + targets[0].name);
						targets[0].setStatus("stun", true);
					}
				}
				double selfDam = (int)(baseDam*.5);
				if (selfDam > 0) {
					attacker.hp -= selfDam; //might add to damage received in turn?
					System.out.println(attacker.name + " deals " + selfDam + " damage to self from recoil");
				}
			} else {
				System.out.println(attacker.name + "'s attack missed");
			}
		} else {
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
		}
		attacker.priority = false;
	}

}
