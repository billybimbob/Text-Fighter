package combat.moves.magic;
import combat.Ability;
import combat.Status;
import main.Interface;
import assets.Monster;

public class Frenzy extends Ability {

    public Frenzy(Monster user) {
        super(user);
        name = "Frenzy";
        description = "Magic attack that causes all others to have random targets";
        attType = false;
        manaCost = 8;
        numTar = -1;
    }

    public void execute() {
        Monster[] targets = attacker.getTargets();
        if(enoughMana()) {
            Interface.writeOut(attacker.getName() + " causes mass panic");
            for (Monster target: targets) {

                if (attackHit(target, 0.01)) {
                    target.setStatus(Status.FRENZY, 2);
                    Interface.writeOut(target.getName() + " becomes frenzied");
                } else
                    Interface.writeOut(target.getName() + " resists being frenzied");
            }
        }
    }
}