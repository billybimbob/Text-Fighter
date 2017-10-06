package combat;

import assets.Monsters;

public class Freeze extends Attacks {
	public Freeze (Monsters attacker) {
		this.name = "Freeze";
		this.description = "A a magic attack with the less damage, but lowers target's speed and evasion";
		this.attacker = attacker;
		this.attType = false;
		this.targets = new Monsters[numTar];
		this.manaCost = 2;
		baseDamage();
		baseDam *= .85;
	}
	public void execute() {
		double attCheck = attackCheck(targets[0]); //Attack based on RNG and modified by stats, need to consider magic attack
		attacker.mp -= manaCost;
		if (attCheck > 0.1) { //Check if attack will be successful
			int statDam = (int)(baseDam*0.5);
			targets[0].spe -= statDam;
			targets[0].eva -= statDam;
			targetReduct(targets[0]);
			if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				System.out.println(attacker.name + "'s freeze was resisted by " + targets[0].name + "\n");
			} else {
				targets[0].hp -= baseDam;
				System.out.println(attacker.name + " freezes " + targets[0].name + " for " + baseDam + " damage\n" + targets[0].name + "'s speed and evasion were lowered by " + statDam + "\n");
			}
		} else {
			System.out.println(attacker.name + "'s attack missed\n");
		}
	}
}
