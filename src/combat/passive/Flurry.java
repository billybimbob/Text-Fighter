package combat.passive;

import combat.Ability;

public class Flurry extends Ability {
	
	public Flurry() {
		name = "Flurry";
		description = "A passive that increases attack stat each turn";
		passive = true;
	}
	public void execute() {
		System.out.println(attacker.name + " gains attack boost");
		attacker.modStat("att", 1);
	}

}
