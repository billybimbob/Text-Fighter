package combat;

import java.util.ArrayList;
import assets.Monsters;

public class SpinAttack extends Attacks {
	
	public SpinAttack (Monsters attacker, ArrayList<Monsters> enemies) { //might change to get rid of enemies parameter
		this.name = "Spin Attack";
		this.description = "A spinning melee attack that damages all enemies for less damage than a basic attack";
		this.attacker = attacker;
		this.attType = true; //melee attack; might make it based on the attacker
		this.numTar = enemies.size();
		this.targets = new Monsters[numTar]; //need to consider if one of the monsters die
		this.manaCost = 5;
		this.aoe = true;
		baseDamage();
		this.baseDam *= 0.85;
	}
	public void execute() {
		if (attacker.mp >= manaCost) {
			attacker.mp -= manaCost;
			
			System.out.println(attacker.name + " spins around, hitting");
			for (int i = 0; i <= targets.length-1; i++) { //Checks if hits for each monster
				try { 
					double damDealt = 0;
					double attCheck = attackCheck(targets[i]); //Attack based on RNG and modified by stats
					if (attCheck > 0.1) { //Check if attack will be successful
						/*if (critCheck()) { //Might add later, with hashmap?
							baseDam *= 2;
							System.out.println("Critical Hit!");
						}*/
						double storeDam = baseDam;
						targetReduct(targets[i]);
						targets[i].hp -= baseDam;
						damDealt = baseDam;
						baseDam = storeDam;
					}
					if (damDealt < 0)
						 System.out.println("but is blocked by " + targets[i].name);
					else
						System.out.println(targets[i].name + " for " + damDealt + " damage");
				} catch (Exception e) {}
			}
		
		} else
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
	}
}
