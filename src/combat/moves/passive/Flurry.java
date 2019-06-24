package combat.moves.passive;

import assets.Monster;
import assets.Monster.Stat;
import combat.Ability;
import main.Interface;

public class Flurry extends Ability {
	
	public Flurry(Monster user) {
		super(user);
		name = "Flurry";
		description = "A passive that increases attack stat each turn";
		passive = true;
		numTar = -1;
	}
	public void execute() {
		Interface.writeOut(attacker.getName() + " gains attack boost");
		attacker.modStat(Stat.ATT, 1);
	}

}
