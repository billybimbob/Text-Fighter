package combat.magic.shapeshift;

import assets.*;
import main.Index;

public class Polymorph extends ShapeShift { //doesn't account for if the moveLists are different length

	public Polymorph() {
		name = "Polymorph";
		description = "A spell that transforms an enemy into a sheep for 5 turns";
		targets = new Monsters[numTar];
		manaCost = 15;
	}
	
	public void execute() {
		if (attacker.mp >= manaCost) {
			attacker.mp -= manaCost;
			if (attackCheck(targets[0], 0.05)) { //Check if attack will be successful
				System.out.println(attacker.name + " has transformed " + targets[0].name + " into a sheep");
				transform(targets[0], Index.shiftMonList[3], 3); //last 3 turns
			} else {
				System.out.println(attacker.name + " 's spell failed");
			}
		} else {
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
		}
	}

}
