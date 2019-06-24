package combat.moves.passive;

import combat.Ability;
import assets.*;
import main.Interface;

public class Intimidate extends Ability {

	public Intimidate(Monster user) {
		super(user);
		name = "Intimidate";
		description = "A passive that decrease speed of all enemies";
		passive = true;
		numTar = -1;
	}

	public void execute() {
		Monster[] targets = attacker.getTargets();
		Interface.writeOut(attacker.getName() + " intimidates all enemies, and decreases all of their speed");
		for (Monster target: targets)
			if (target.getStat(Stat.SPEED) > 0)
				target.modStat((Stat.SPEED), -1);
	}

}
