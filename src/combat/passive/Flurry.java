package combat.passive;

import assets.Monster;
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
		attacker.modStat("att", 1);
	}

}
