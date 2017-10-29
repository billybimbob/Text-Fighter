package combat;

import assets.Monsters;
import assets.ShapeShift;
import main.Index;

public class Polymorph extends ShapeShift {

	public Polymorph() {
		name = "Polymorph";
		description = "A spell that transforms an enemy into a sheep for 5 turns";
		targets = new Monsters[numTar];
	}
	
	public void execute() {
		if (attackCheck(targets[0], 0.05)) { //Check if attack will be successful
			transform(targets[0], Index.shiftMonList[3]);
			System.out.println(attacker.name + " has transformed " + targets[0].name + " into a sheep");
		} else {
			System.out.println(attacker.name + " 's spell failed");
		}
	}

}
