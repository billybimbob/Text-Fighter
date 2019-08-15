package combat.moves;

import assets.Stat;
import assets.Monster;
import main.Interface;

public class SheepAttacks extends Ability{

	public SheepAttacks(Monster user) {
		super(user);
		name = "Sheep Attacks";
		description = "the most OP attack in the game";
	}
	
	protected void execute() {
		int dialogue = (int)(Math.random()*4);
		Monster target = this.getTarget();
		
		switch(dialogue) {
			case 0:
				Interface.writeOut(attacker.getName() + " readies its.. hoof to strike");
				break;
			case 1:
				Interface.writeOut(attacker.getName() + " wonders around, looking for some grass to eat");
				break;
			case 2:
				Interface.writeOut("Baaa");
				break;
			case 3:
				if (Math.random()>0.99) {
					this.damage = (int)(Math.random()*(target.getStat(Stat.HP)))*2;
					String damPrompt = "Woah! " + attacker.getName() + " is angry, and bites on " 
						+ target.getName() + " for " + damage + " damage!";
					dealDamage(damPrompt);
				} else {
					Interface.writeOut("Just a little old goat");
				}
				break;
		}
	}

}
