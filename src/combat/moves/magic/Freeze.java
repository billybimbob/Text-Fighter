package combat.moves.magic;

import assets.*;
import combat.Ability;
import main.Interface;

public class Freeze extends Ability {
	
	public Freeze (Monster user) {
		super(user);
		name = "Freeze";
		description = "A a magic attack with the less damage, but lowers target's speed and evasion";
		attType = false;
		manaCost = 2;
		damageMod = 0.75f;
	}
	
	public void execute() {
		Monster[] targets = attacker.getTargets();
		boolean manaUsed;

		if ((manaUsed = enoughMana()) && attackHit(targets[0], 0.01)) {
			//Attack based on RNG and modified by stats, need to consider magic attack

			targetReduct(targets[0]);
			if (blocked()) //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				Interface.writeOut(attacker.getName() + "'s freeze was resisted by " + targets[0].getName());
			else {
				Interface.writeOut(attacker.getName() + " freezes " + targets[0].getName() + " for " +damage + " damage");
				dealDamage(attacker, targets[0], damage);
				if (targets[0].getStat(Stat.SPEED) > 0) {
					int statDam = 1;
					targets[0].modStat(Stat.SPEED, true, -statDam);
					Interface.writeOut(targets[0].getName() + "'s speed was lowered by " + statDam);
				}
			}
		} else if (manaUsed) { //attackHit failed
			Interface.writeOut(attacker.getName() + "'s attack missed");
		} else {
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
		}
	}
	
}
