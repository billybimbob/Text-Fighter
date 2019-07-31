package combat.moves.magic;

import assets.Monster;
import combat.moves.Ability;
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
            Interface.writeOut(attacker.getName() + " creates a burst of flame surrounding self");

            for (Monster target: targets) {
                String failPrompt = target.getName() + " resists the flames";
                if (attackHit(target, failPrompt)) {
                    dealDamage(attacker, target, damage);
                    Interface.writeOut(target.getName() + " is burned for " + damage + " damage");
                    target.setStatus(Status.BURN, 3);
                    Interface.writeOut(target.getName() + " is on fire for 3 turns");
                }
            }
        }
    }

}