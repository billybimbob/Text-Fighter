package combat.magic;

import combat.*;
import main.Fight;

public class Reflect extends Ability { //not sure if should be priority or not

	public Reflect () {
		name = "Reflect";
		description = "A spell that reflects some damage back at the attacker for 5 turns";
		attType = false;
		aoe = true;
		manaCost = 6;
	}

	public void execute() {
		if (attacker.mp >= manaCost) { //Checks if sufficient mana
			attacker.mp -= manaCost;
			attacker.setStatus("reflect", Fight.turnCount, 5);
			System.out.println(attacker.name + " casts a reflecting shield for " + manaCost + " mana");
			
		} else
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");

	}
}
