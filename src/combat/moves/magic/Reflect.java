package combat.moves.magic;

import assets.*;
import combat.*;
import main.Interface;

public class Reflect extends Ability { //not sure if should be priority or not

	public Reflect (Monster user) {
		super(user);
		name = "Reflect";
		description = "A spell that reflects some damage back at the attacker for 5 turns";
		attType = false;
		manaCost = 6;
		numTar = 0;
	}

	public void execute() {
		if (enoughMana()) { //Checks if sufficient mana
			attacker.modStat(Stat.MP, true, -manaCost);
			attacker.setStatus(Status.REFLECT, 5);
			Interface.writeOut(attacker.getName() + " casts a reflecting shield for " + manaCost + " mana");
			
		}
	}
}
