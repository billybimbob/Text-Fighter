package combat;

import assets.Monsters;

public class MagicBlast extends Attacks {

	public MagicBlast () {
		this.name = "Magic Blast";
		this.description = "A a magic attack with the same damage as a basic, but has chance to stun and ignores some armor";
		this.attType = false;
		this.targets = new Monsters[numTar];
		this.manaCost = 2;
		baseDamMod = 1.25;
	}
	
	public void execute() {
		//Attack based on RNG and modified by stats, need to consider magic attack
		attacker.mp -= manaCost;
		if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
			
			baseDamage();
			//double storeDam = baseDam;
			targetReduct(targets[0]);
			
			if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				System.out.println(attacker.name + "'s magic blast was resisted by " + targets[0].name);
			} else {
				targets[0].hp -= baseDam;
				System.out.println(attacker.name + " blasts " + targets[0].name + " for " + baseDam + " damage");
				
				if (attackCheck(targets[0], 0.3)) {
					System.out.println(attacker.name + "'s blast stuns " + targets[0].name);
					targets[0].stun = true;
				}
			}
			//baseDam = storeDam;
		} else {
			System.out.println(attacker.name + "'s attack missed");
		}
	}
}
