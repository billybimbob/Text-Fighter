package combat.moves.magic;

import combat.moves.Ability;
import combat.Status;
import assets.Monster;

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
		Monster attacker = this.getAttacker();
		Monster target = this.currentTarget();
		String failPrompt = attacker.getName() + " failed to possess";
        if (attackHit(failPrompt)) {
            String controlPrompt = attacker.getName() + " takes control of " + target.getName();
            applyStatus(Status.CONTROL, 3, controlPrompt);
        }
    }
}