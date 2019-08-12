package combat.moves.passive;

import assets.Stat;
import assets.Monster;
import combat.moves.Ability;
import main.Interface;

public class Rage extends Ability {
	
	public Rage(Monster user) {
		super(user);
		name = "Rage";
		description = "A passive that increases attack and decreases defense stat each turn";
		passive = true;
		numTar = 0;
	}

	protected void execute(Monster target) {
		attacker.modStat(Stat.ATT, false, 1);
		attacker.modStat(Stat.DEF, false, -1);
		Interface.writeOut(attacker.getName() + "'s gains attack boost and lowers defense");
	}

}
