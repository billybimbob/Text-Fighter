package combat.moves.magic;

import combat.moves.Ability;
import combat.Status;
import assets.Monster;

public class Frenzy extends Ability {

    private int duration;

    public Frenzy(Monster user) {
        super(user);
        name = "Frenzy";
        description = "Magic attack that causes all others to have random targets";
        attType = false;
        manaCost = 8;
        numTar = -1;
        duration = 2;
    }

    @Override
    protected boolean preExecute() {
        String attPrompt = this.getAttacker().getName() + " causes mass panic";
        return enoughMana(attPrompt);
    }

    protected void execute() {
        Monster target = this.currentTarget();
        String failPrompt = target.getName() + " resists being frenzied";
        if (attackHit(failPrompt)) {
            String frenzPrompt = target.getName() + " becomes frenzied";
            applyStatus(Status.FRENZY, duration, frenzPrompt);
        }
    
    }
}