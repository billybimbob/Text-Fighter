package combat.passive;

import combat.Ability;
import assets.Monster;
import main.Interface;

public class Intimidate extends Ability {

	public Intimidate(Monster attacker) {
		super(attacker);
		name = "Intimidate";
		description = "A passive that decrease speed of all enemies";
		passive = true;
		aoe = true;
	}
	public void execute(Monster... targets) {
		Interface.writeOut(attacker.getName() + " intimidates all enemies, and decreases all of their speed");
		for (int i = 0; i <= targets.length-1; i++) {
			if (targets[i].getStat("spe") > 0)
				targets[i].modStat(("spe"), -1);
		}
	}

}
