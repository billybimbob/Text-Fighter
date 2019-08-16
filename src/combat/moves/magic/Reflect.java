package combat.moves.magic;

import assets.Monster;
import combat.moves.Ability;
import combat.Status;
import main.Interface;

public class Reflect extends Ability { //not sure if should be priority or not

	private int duration;

	public Reflect (Monster user) {
		super(user);
		name = "Reflect";
		description = "A spell that reflects some damage back at the attacker for " + duration + " turns";
		attType = false;
		manaCost = 6;
		numTar = 0;
		duration = 5;
		attMod = 0.005f;
	}

	protected void execute() {

		String failChannel = attacker.getName() + " failed to cast shield";
		if (attackHit(failChannel)) {
			attacker.setStatus(Status.REFLECT, 5);
			Interface.writeOut(attacker.getName() + " casts a reflecting shield for " + duration + " turns");
		}
	}

}
