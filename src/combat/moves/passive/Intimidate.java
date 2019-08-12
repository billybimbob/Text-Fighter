package combat.moves.passive;

import combat.moves.Ability;
import main.Interface;
import assets.Stat;
import assets.Monster;

public class Intimidate extends Ability {

	public Intimidate(Monster user) {
		super(user);
		name = "Intimidate";
		description = "A passive that decrease speed of all enemies";
		passive = true;
		numTar = -1;
	}

	@Override
	protected boolean preExecute() {
		Interface.writeOut(attacker.getName() + " intimidates all enemies, and decreases all of their speed");
		return true;
	}

	protected void execute(Monster target) {
		target.modStat(Stat.SPEED, true, -1);
	}

}
