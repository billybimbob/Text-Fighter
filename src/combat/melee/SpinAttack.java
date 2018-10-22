package combat.melee;

import assets.Monsters;
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
		if (attacker.getStat("mp") >= manaCost) {
			attacker.modStat("mp", -manaCost);
			
			System.out.println(attacker.name + " spins around, hitting");
			baseDamage();
			for (Monsters target: targets) { //Checks if hits for each monster
				try {
					double damDealt = 0;
					//Attack based on RNG and modified by stats
					if (attackCheck(target, 0.005)) { //Check if attack will be successful
						/*if (critCheck()) { //Might add later, with hashmap?
							baseDam *= 2;
							System.out.println("Critical Hit!");
						}*/
						double storeDam = baseDam;
						targetReduct(target);
						loseHp(target, baseDam);
						damDealt = baseDam;
						baseDam = storeDam;
					}
					if (damDealt < 0)
						 System.out.println("but is blocked by " + target.name);
					else
						System.out.println(target.name + " for " + damDealt + " damage");
				} catch (Exception e) {System.out.print(e);}
			}
		
		} else
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
	}
}
