package combat.moves.magic;

import assets.Monster;
import combat.Ability;
import combat.Status;
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

    public void execute() {
        Monster[] targets = attacker.getTargets();
        if (enoughMana()) {
            for (Monster target: targets) {
                if (attackHit(target, 0.01)) {
                    dealDamage(attacker, target, damage);
                    Interface.writeOut(attacker.getName() + "'s fire deals " + damage + " damage");
                    target.setStatus(Status.BURN, 3);
                    Interface.writeOut(target.getName() + " gets burned");
                } else
                    Interface.writeOut(target.getName() + " resists the flames");
            }
        }
    }

}