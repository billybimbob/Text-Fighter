package combat;

import assets.Monsters;

public class SheepAttacks extends Ability{

	public SheepAttacks() {
		name = "Sheep Attacks";
		description = "the most OP attack in the game";
		targets = new Monsters[numTar];
	}
	
	public void execute() {
		int dialogue = (int)(Math.random()*4);
		switch(dialogue) {
			case 0:
				System.out.println(attacker.name + " readies its... hoof to strike");
				break;
			case 1:
				System.out.println(attacker.name + " wonders around, looking for some grass to eat");
				break;
			case 2:
				System.out.println("Baaa");
				break;
			case 3:
				if(Math.random()>0.99) {
					double secretDam = (int)(Math.random()*(targets[0].hp))*2;
					loseHp(targets[0], secretDam);
					System.out.println("Woah! " + attacker.name + " is angry, and bites on " + targets[0].name + " for " + secretDam + " damage!");
				} else {
					System.out.println("Just a little old goat");
				}
				break;
		}
	}

}
