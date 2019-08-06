package combat.moves.magic;

import assets.chars.Monster;
import combat.Status;
import combat.moves.Ability;
import main.Interface;

public class FlameBurst extends Ability {

    private int duration;

    public FlameBurst(Monster user) {
        super(user);
        name = "Flame Burst";
        description = "A blast of fire that causes all others to be set on fire for " + duration + " turns";
        attType = false;
        manaCost = 6;
        numTar = -1;
        damageMod = 0.5f;
        duration = 3;
    }

    @Override
    protected boolean preExecute() {
        String attPrompt = attacker.getName() + " creates a burst of flame surrounding self";
        return enoughMana(attPrompt);
    }

    protected void execute(Monster target) {
        String failPrompt = target.getName() + " resists the flames";
        if (attackHit(target, failPrompt)) {
            dealDamage(attacker, target, damage);
            target.setStatus(Status.BURN, duration);
            Interface.writeOut(target.getName() + " is hit with flames for " + damage + " damage"
                + "\n\tand is also burning for " + duration + " turns");
        }
    }

}