package combat.moves.magic.shapeshift;

import assets.*;
import main.Index;
import main.Interface;

public class Polymorph extends ShapeShift { //doesn't account for if the moveLists are different length

	public Polymorph(Monster user) {
		super(user);
		name = "Polymorph";
		description = "A spell that transforms an enemy into a sheep for 5 turns";
		manaCost = 15;
	}
	
	public void execute() {
		Monster[] targets = attacker.getTargets();
		boolean manaUsed;

		if ((manaUsed = enoughMana()) && attackHit(targets[0], 0.05)) { //Check if attack will be successful
			Interface.writeOut(attacker.getName() + " has transformed " + targets[0].getName() + " into a sheep");
			Monster sheep = Index.createMonster("Sheep");
			transform(targets[0], sheep, 3); //last 3 turns
		
		} else if (manaUsed) {
			Interface.writeOut(attacker.getName() + " 's spell failed");
		
		} else {
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
		}
	}

}
