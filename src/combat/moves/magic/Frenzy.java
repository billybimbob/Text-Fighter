package combat.moves.magic;

import combat.moves.Ability;
import combat.Status;
import assets.Monster;
import main.Interface;

public class Frenzy extends Ability {

    public Frenzy(Monster user) {
        super(user);
        name = "Frenzy";
        description = "Magic attack that causes all others to have random targets";
        attType = false;
        manaCost = 8;
        numTar = -1;
    }

    @Override
    protected boolean preExecute() {
        String attPrompt = attacker.getName() + " causes mass panic";
        return enoughMana(attPrompt);
    }

    protected void execute() {
        Monster target = this.getTarget();
        String failPrompt = target.getName() + " resists being frenzied";
        if (attackHit(failPrompt)) {
            target.setStatus(Status.FRENZY, 2);
            this.afflicted.add(Status.FRENZY);
            Interface.writeOut(target.getName() + " becomes frenzied");
        }
    
    }
}