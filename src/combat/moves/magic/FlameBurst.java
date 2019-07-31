package combat.moves.magic;

import assets.chars.Monster;
import combat.Status;
import combat.moves.Ability;
import main.Interface;

public class FlameBurst extends Ability {

    public FlameBurst(Monster user) {
        super(user);
        name = "Flame Burst";
        description = "A blast of fire that causes all others to be set on fire";
        attType = false;
        manaCost = 6;
        numTar = -1;
        damageMod = 0.75f;
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
            Interface.writeOut(target.getName() + " is burned for " + damage + " damage");
            target.setStatus(Status.BURN, 3);
            Interface.writeOut(target.getName() + " is also burning for 3 turns");
        }
    }

}