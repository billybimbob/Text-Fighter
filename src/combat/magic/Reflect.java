package combat.magic;

import assets.Monster;
import combat.*;
import main.Fight;
import main.Interface;

public class Reflect extends Ability { //not sure if should be priority or not

	public Reflect (Monster attacker) {
		super(attacker);
		name = "Reflect";
		description = "A spell that reflects some damage back at the attacker for 5 turns";
		attType = false;
		manaCost = 6;
		numTar = 0;
	}

	public void execute() {
		if (enoughMana()) { //Checks if sufficient mana
			attacker.modStat("mp", -manaCost);
			attacker.setStatus("reflect", Fight.turnCount, 5);
			Interface.writeOut(attacker.getName() + " casts a reflecting shield for " + manaCost + " mana");
			
		} else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");

	}
}
