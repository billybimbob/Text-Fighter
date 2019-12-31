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

	@Override
	protected void applyStatus(Status status, int duration, String statusPrompt) {
		if (status == Status.SHIFT) {
			Monster sheep = Index.createMonster("Sheep");
			ShapeShift.transform(this.currentTarget(), sheep, duration); //last 3 turns
			if (statusPrompt != null)
				Interface.writeOut(statusPrompt);
		} else {
			super.applyStatus(status, duration, statusPrompt);
		}
		this.applied.add(status);
	}
	
	protected void execute() { //checks twice
		Monster attacker = this.getAttacker();
		Monster target = this.currentTarget();
		String failPrompt = attacker.getName() + "'s morph failed";
		String secondFail = attacker.getName() + "'s morph was resisted";

		if (attackHit(failPrompt) && attackHit(secondFail)) { //Check if attack will be successful
			
			String transPrompt = attacker.getName() + " has transformed " + target.getName() + " into a sheep";
			applyStatus(Status.SHIFT, 3, transPrompt);
		}
	}

}
