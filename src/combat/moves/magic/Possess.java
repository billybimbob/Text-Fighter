package combat.moves.magic;

import combat.moves.Ability;
import combat.Status;
import assets.chars.Monster;
import main.Interface;

public class Possess extends Ability {

    public Possess (Monster user) {
        super(user);
        name = "Possess";
        description = "Magic attack that forces target to fight for you for 3 turns";
        attType = false;
        manaCost = 10;
    }

	protected void execute(Monster target) {
		String failPrompt = attacker.getName() + "'s spell failed";
        if (attackHit(target, failPrompt)) {
            target.setStatus(Status.CONTROL, 3);
            Interface.writeOut(attacker.getName() + " takes control of " + target.getName());
        }
    }
}