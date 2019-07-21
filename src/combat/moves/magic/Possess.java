package combat.moves.magic;

import combat.Ability;
import combat.Status;
import assets.Monster;
import main.Interface;

public class Possess extends Ability {

    public Possess (Monster user) {
        super(user);
        name = "Possess";
        description = "Magic attack that forces target to fight for you for 3 turns";
        attType = false;
        manaCost = 10;
    }

    public void execute() {
        Monster[] targets = attacker.getTargets();

		String failPrompt = attacker.getName() + "'s spell failed";
        if (enoughMana() && attackHit(targets[0], failPrompt)) {
            targets[0].setStatus(Status.CONTROL, 3);
            Interface.writeOut(attacker.getName() + " takes control of " + targets[0].getName());
        }
    }
}