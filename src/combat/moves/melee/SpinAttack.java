package combat.moves.melee;

import assets.*;
import combat.*;
import main.Interface;

public class SpinAttack extends Ability {

	
	public SpinAttack (Monster user) { //might change to get rid of enemies parameter; max amount of targets is 5
		super(user);
		name = "Spin Attack";
		description = "A spinning melee attack that damages all enemies for less damage than a basic attack";
		attType = true; //melee attack; might make it based on the attacker
		numTar = -1;
		manaCost = 5;
	}
	
	public void execute() { //current bug with null pointer
		Monster[] targets = attacker.getTargets();

		if (enoughMana()) {
			attacker.modStat(Stat.MP, -manaCost);
			
			Interface.writeOut(attacker.getName() + " spins around");

			baseDamage();
			float startDam = damage;
			for (Monster target: targets) { //Checks if hits for each monster
				//Attack based on RNG and modified by stats
				
				damage = startDam;
				if (attackHit(target, 0.005)) { //Check if attack will be successful
					/*if (critCheck()) { //Might add later, with hashmap?
						baseDam *= 2;
						Interface.writeOut("Critical Hit!");
					}*/

					targetReduct(target);
					
					if (blocked())
						Interface.writeOut("but is blocked by " + target.getName());
					else {	
						dealDamage(attacker, target, damage);
						Interface.writeOut(target.getName() + " for " + damage + " damage");
					}
				}
			}
		
		} else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
	}
}
