package combat.moves.melee;

import combat.Ability;
import assets.Monster;
import main.Interface;

public class Revenge extends Ability {

    public Revenge(Monster user) {
        super(user);
        name = "Revenge";
        description = "Deal all damage received this turn to up to 3 targets; guranteed to hit";
        attType = true;
        manaCost = 6;
        numTar = 3;
    }

    public void execute() {
        Monster[] targets = attacker.getTargets();

        if (enoughMana()) {
            int prevRound = Interface.FIGHT.getTurnNum()-1;
            damage = Interface.FIGHT.getTurnDamage(prevRound, attacker); //keep eye on

            for (Monster target: targets) {
                Ability.dealDamage(attacker, target, damage);
                Interface.writeOut(attacker.getName() + " enacts revenge on " + target.getName() 
                    + " dealing " + damage + " damage");
            }
        }

    }
}