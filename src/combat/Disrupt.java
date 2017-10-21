package combat;

import assets.Monsters;

public class Disrupt extends Attacks {

	public Disrupt() {
		name = "Disrupt";
		description = "A quick strike disrupting a target while injuring yourself";
		attType = true;
		targets = new Monsters[numTar];
		priority = true;
		manaCost = 5;
		baseDamMod = 0.75;
	}
	
	public void execute() {
		if (attacker.mp >= manaCost) {
			
		} else {
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
		}
	}

}
