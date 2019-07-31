package combat.moves;

import assets.*;
import main.Interface;

public class SheepAttacks extends Ability{

	public SheepAttacks(Monster user) {
		super(user);
		name = "Sheep Attacks";
		description = "the most OP attack in the game";
	}
	
	protected void execute(Monster target) {
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
				if (Math.random()>0.99) {
					float secretDam = (int)(Math.random()*(target.getStat(Stat.HP)))*2;
					Interface.writeOut("Woah! " + attacker.getName() + " is angry, and bites on " + target.getName() + " for " + secretDam + " damage!");
					dealDamage(attacker, target, secretDam);
				} else {
					Interface.writeOut("Just a little old goat");
				}
				break;
		}
	}

}
