package combat;

import assets.Monsters;

public class Disrupt extends Attacks {

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
				
				double selfDam = (int)(baseDam*.5);
				attacker.hp -= selfDam;
				System.out.println(attacker.name + " charges, dealing " + selfDam + " damage to self from recoil");
				if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					System.out.println(attacker.name + "'s ram was resisted by " + targets[0].name);
				} else {
					targets[0].hp -= baseDam;
					System.out.println(attacker.name + " rams " + targets[0].name + " for " + baseDam + " damage");
					if (attackCheck(targets[0], 0.4)) {
						System.out.println(attacker.name + "'s blow also stuns " + targets[0].name);
						targets[0].status[3] = 1;
					}
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
