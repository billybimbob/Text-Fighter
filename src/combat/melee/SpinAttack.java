package combat.melee;

import assets.Monster;
import combat.*;
import main.Interface;

public class SpinAttack extends Ability {

	
	public SpinAttack (Monster attacker) { //might change to get rid of enemies parameter; max amount of targets is 5
		super(attacker);
		name = "Spin Attack";
		description = "A spinning melee attack that damages all enemies for less damage than a basic attack";
		attType = true; //melee attack; might make it based on the attacker
		numTar = -1;
		aoe = true;
		manaCost = 5;
	}
	
	public void execute(Monster... targets) { //current bug with null pointer
		if (enoughMana()) {
			attacker.modStat("mp", -manaCost);
			
			Interface.writeOut(attacker.getName() + " spins around, hitting");
			baseDamage();
			for (Monster target: targets) { //Checks if hits for each monster
				try {
					double damDealt = 0;
					//Attack based on RNG and modified by stats
					if (attackHit(target, 0.005)) { //Check if attack will be successful
						/*if (critCheck()) { //Might add later, with hashmap?
							baseDam *= 2;
							Interface.writeOut("Critical Hit!");
						}*/
						//double storeDam = baseDam;
						targetReduct(target);
						loseHp(attacker, target, baseDam);
						damDealt = baseDam;
						//baseDam = storeDam;
					}
					if (damDealt < 0)
						 Interface.writeOut("but is blocked by " + target.getName());
					else
						Interface.writeOut(target.getName() + " for " + damDealt + " damage");
				} catch (Exception e) {System.out.print(e);}
			}
		
		} else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
	}
}
