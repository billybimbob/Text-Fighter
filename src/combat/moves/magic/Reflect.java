package combat.moves.magic;

import assets.*;
import combat.moves.Ability;
import combat.Status;
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

	protected void execute(Monster target) {
		attacker.setStatus(Status.REFLECT, 5);
		Interface.writeOut(attacker.getName() + " casts a reflecting shield for " + manaCost + " mana");
	}

}
