package combat.passive;

import combat.Ability;
import assets.Monster;
import main.Interface;

public class Flurry extends Ability {
	
	public Flurry(Monster attacker) {
		super(attacker);
		name = "Flurry";
		description = "A passive that increases attack stat each turn";
		passive = true;
		numTar = -1;
	}
	public void execute() {
		Interface.writeOut(attacker.getName() + " gains attack boost");
		attacker.modStat("att", 1);
	}

}
