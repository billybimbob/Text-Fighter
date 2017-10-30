package combat.melee;

import combat.*;

public class SpinAttack extends Ability {
	
	public SpinAttack () { //might change to get rid of enemies parameter
		name = "Spin Attack";
		description = "A spinning melee attack that damages all enemies for less damage than a basic attack";
		attType = true; //melee attack; might make it based on the attacker
		aoe = true;
		manaCost = 5;
	}
	
	public void execute() { //current bug with null pointer
		if (attacker.mp >= manaCost) {
			attacker.mp -= manaCost;
			
			System.out.println(attacker.name + " spins around, hitting");
			baseDamage();
			for (int i = 0; i <= targets.length-1; i++) { //Checks if hits for each monster
				try {
					double damDealt = 0;
					//Attack based on RNG and modified by stats
					if (attackCheck(targets[i], 0.005)) { //Check if attack will be successful
						/*if (critCheck()) { //Might add later, with hashmap?
							baseDam *= 2;
							System.out.println("Critical Hit!");
						}*/
						double storeDam = baseDam;
						targetReduct(targets[i]);
						loseHp(targets[i], baseDam);
						damDealt = baseDam;
						baseDam = storeDam;
					}
					if (damDealt < 0)
						 System.out.println("but is blocked by " + targets[i].name);
					else
						System.out.println(targets[i].name + " for " + damDealt + " damage");
				} catch (Exception e) {System.out.print(e);}
			}
		
		} else
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
	}
}