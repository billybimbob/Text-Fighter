package combat.moves.magic;

import combat.moves.Ability;
import combat.Status;
import assets.Monster;
import main.Interface;

public class Possess extends Ability {
    //look over either this ability or look at aggro
    public Possess (Monster user) {
        super(user);
        name = "Possess";
        description = "Magic attack that forces target to fight for you for 3 turns";
        attType = false;
        manaCost = 10;
    }

	protected void execute() {
		Monster target = this.getTarget();
		String failPrompt = attacker.getName() + "'s spell failed";
        if (attackHit(failPrompt)) {
            target.setStatus(Status.CONTROL, 3);
            Interface.writeOut(attacker.getName() + " takes control of " + target.getName());
        }
    }
}