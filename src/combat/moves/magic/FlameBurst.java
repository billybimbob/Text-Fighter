package combat.moves.magic;

import assets.Monster;
import combat.Status;
import combat.moves.Ability;

public class FlameBurst extends Ability {

    private int duration;

    public FlameBurst(Monster user) {
        super(user);
        name = "Flame Burst";
        description = "A blast of fire that causes all others to be set on fire for " + duration + " turns";
        attType = false;
        manaCost = 7;
        numTar = -1;
        damageMod = 0.75f;
        duration = 4;
    }

    @Override
    protected boolean preExecute() {
        String attPrompt = this.getAttacker().getName() + " creates a burst of flame surrounding self";
        return enoughMana(attPrompt);
    }

    protected void execute() {
		Monster target = this.currentTarget();
        String failPrompt = target.getName() + " resists the flames";
        if (attackHit(failPrompt)) {

            String damPrompt = target.getName() + " is hit with flames for " + damage + " damage"
                + "\nand burning for " + duration + " turns";
            dealDamage(damPrompt);
            applyStatus(Status.BURN, duration, null);
        }
    }

}