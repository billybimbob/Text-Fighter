package combat.passive;

import combat.Ability;

public class Intimidate extends Ability {

	public Intimidate() {
		name = "Intimidate";
		description = "A passive that decrease speed of all enemies";
		passive = true;
		aoe = true;
	}
	public void execute() {
		System.out.println(attacker.name + " intimidates all enemies, and decreases all of their speed");
		for (int i = 0; i <= targets.length-1; i++) {
			if (targets[i].getStat("spe") > 0)
				targets[i].modStat(("spe"), -1);
		}
	}

}
