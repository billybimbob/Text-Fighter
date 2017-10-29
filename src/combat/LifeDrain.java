package combat;

import assets.Monsters;

public class LifeDrain extends Attacks {

	public LifeDrain() {
		name = "Life Drain";
		description = "A magic attack that heals for for a portion of damage dealt";
		attType = false;
		targets = new Monsters[numTar];
		manaCost = 3;
		baseDamMod = 2;
	}

	public void execute() {
		if (attacker.mp >= manaCost) {
			attacker.mp -= manaCost;
			if (attackCheck(targets[0], 0.01)) { //Check if attack will be successful
				attacker.mp -= manaCost;
				baseDamage();
				targetReduct(targets[0]);
				double selfHeal = (int)(baseDam*0.5);
				if (baseDam <= 0) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					System.out.println(attacker.name + "'s drain was resisted by " + targets[0].name);
				} else {
					loseHp(targets[0], baseDam);
					System.out.println(attacker.name + " drains " + targets[0].name + " for " + baseDam + " damage");
					if (selfHeal > 0 && attacker.hp < attacker.maxHp) {
						attacker.hp += selfHeal;
						System.out.println("and absorbs " + selfHeal + " health");
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
