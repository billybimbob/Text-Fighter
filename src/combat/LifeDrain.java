package combat;

import assets.Monsters;

public class LifeDrain extends Attacks {

	public LifeDrain() {
		name = "Life Drain";
		description = "A magic attack that heals for for a portion of damage dealt";
		attType = false;
		targets = new Monsters[numTar];
		manaCost = 3;
		baseDamMod = 1.2;
	}

	public void execute() {
		if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
			attacker.mp -= manaCost;
			baseDamage();
			targetReduct(targets[0]);
			double selfHeal = (int)(baseDam*0.5);
			if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				System.out.println(attacker.name + "'s drain was resisted by " + targets[0].name);
			} else {
				targets[0].hp -= baseDam;
				attacker.hp += selfHeal;
				System.out.println(attacker.name + " drains " + targets[0].name + " for " + baseDam + " damage\nand heals self for " + selfHeal);
			}
		} else {
			System.out.println(attacker.name + "'s attack missed");
		}

	}
}
