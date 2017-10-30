package combat.magic;

import combat.*;
import main.Interface;

public class Reflect extends Ability { //might change attack to be a buff instead of mult-turn attack

	private int turnCount = 0;
	
	public Reflect () {
		name = "Reflect";
		description = "A spell that reflects all damaged received for one turn to all enemies";
		attType = false;
		aoe = true;
		manaCost = 6;
	}

	public void execute() {
		if (attacker.mp >= manaCost && turnCount == 0) { //Checks if sufficient mana
			attacker.mp -= manaCost;
			System.out.println(attacker.name + " casts a reflecting shield for " + manaCost + " mana");
			attacker.def -= 5;
			turnCount++;
			if (attacker.aggro)
				Interface.heroAction = true;
			else
				attacker.multTurn = true;
		} else if (turnCount == 1) { //Checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats
			baseDam = attacker.damTurn*2;
			System.out.println(attacker.name + " channels damage received, and blasts ");
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
						 System.out.println("but is resisted by " + targets[i].name);
					else
						System.out.println(targets[i].name + " for " + damDealt + " damage");
				} catch (Exception e) {System.out.print(e);}
			}
			attacker.def += 5; //might later set to a variable
			turnCount = 0;
			if (attacker.aggro)
				Interface.heroAction = false;
			
		} else
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");

	}
}
