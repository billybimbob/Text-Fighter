package combat.moves.passive;

import assets.*;
import combat.Ability;
import main.Interface;

public class Rage extends Ability {
	
	public Rage(Monster user) {
		super(user);
		name = "Rage";
		description = "A passive that increases attack and decreases defense stat each turn";
		passive = true;
		numTar = -1;
	}
	public void execute() {
		attacker.modStat(Stat.ATT, false, 1);
		Interface.writeOut(attacker.getName() + " gains attack boost");
		attacker.modStat(Stat.DEF, false, -1);
		Interface.writeOut(attacker.getName() + "'s rage lowers defense");
	}

}