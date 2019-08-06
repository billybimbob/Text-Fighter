package combat.moves.melee;

import combat.moves.Ability;
import assets.chars.Monster;
import main.Interface;

public class Revenge extends Ability {

    public Revenge(Monster user) {
        super(user);
        name = "Revenge";
        description = "Deal all damage received last turn to up to 3 targets; guranteed to hit";
        attType = true;
        manaCost = 6;
        numTar = 3;
    }

    @Override
    protected boolean preExecute() {
        int prevRound = Interface.FIGHT.getTurnNum()-1;
        damage = Interface.FIGHT.getTurnDamage(prevRound, attacker); //keep eye on

        Interface.writeOut(attacker.getName() + " enacts revenge");
        return enoughMana();
    }

    @Override
	protected void execute(Monster target) {
        dealDamage(attacker, target, damage);
        Interface.writeOut(target.getName() + " is dealt " + damage + " damage");
    }
}