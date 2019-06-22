package combat;

import assets.Monster;
import main.Interface;

public class SheepAttacks extends Ability{

	public SheepAttacks(Monster attacker) {
		super(attacker);
		name = "Sheep Attacks";
		description = "the most OP attack in the game";
	}
	
	public void execute(Monster... targets) {
		int dialogue = (int)(Math.random()*4);
		switch(dialogue) {
			case 0:
				Interface.writeOut(attacker.getName() + " readies its... hoof to strike");
				break;
			case 1:
				Interface.writeOut(attacker.getName() + " wonders around, looking for some grass to eat");
				break;
			case 2:
				Interface.writeOut("Baaa");
				break;
			case 3:
				if(Math.random()>0.99) {
					float secretDam = (int)(Math.random()*(targets[0].getStat("hp")))*2;
					Interface.writeOut("Woah! " + attacker.getName() + " is angry, and bites on " + targets[0].getName() + " for " + secretDam + " damage!");
					loseHp(attacker, targets[0], secretDam);
				} else {
					Interface.writeOut("Just a little old goat");
				}
				break;
		}
	}

}
