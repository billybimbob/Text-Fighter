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
        boolean manaUsed;
        if ((manaUsed = enoughMana()) && attackHit(targets[0], 0.1)) {
            targets[0].setStatus(Status.CONTROL, 3);
            Interface.writeOut(attacker.getName() + " takes contorl of " + targets[0].getName());
        } else if (manaUsed) {
            Interface.writeOut(attacker.getName() + "'s spell failed");
        }
    }
}