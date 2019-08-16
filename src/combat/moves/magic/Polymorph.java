package combat.moves.magic;

import assets.*;
import combat.Status;
import combat.moves.Ability;
import main.Index;
import main.Interface;

public class Polymorph extends Ability { //doesn't account for if the moveLists are different length

	public Polymorph(Monster user) {
		super(user);
		name = "Polymorph";
		description = "A spell that transforms an enemy into a sheep for 5 turns";
		manaCost = 15;
		attMod = 0.05f;
	}
	
	protected void execute() { //checks twice
		Monster target = this.getTarget();
		String failPrompt = attacker.getName() + "'s morph failed";
		String secondFail = attacker.getName() + "'s morph was resisted";
		if (attackHit(failPrompt) && attackHit(secondFail)) { //Check if attack will be successful
			
			Interface.writeOut(attacker.getName() + " has transformed " + target.getName() + " into a sheep");
			Monster sheep = Index.createMonster("Sheep");
			this.afflicted.add(Status.SHIFT);
			ShapeShift.transform(target, sheep, 3); //last 3 turns
		}
	}

}
