package combat.moves.magic;

import assets.*;
import combat.*;
import main.Interface;

public class LifeDrain extends Ability {

	public LifeDrain(Monster user) {
		super(user);
		name = "Life Drain";
		description = "A magic attack that heals for for a portion of damage dealt";
		attType = false;
		manaCost = 3;
		damageMod = 1.4f;
	}

	public void execute() {
		Monster[] targets = attacker.getTargets();
		boolean manaUsed;

		if ((manaUsed = enoughMana()) && attackHit(targets[0], 0.01)) { //Check if attack will be successful
			targetReduct(targets[0]);
			float selfHeal = (int)(damage*0.5);
			
			if (blocked()) {//Check if the defense reduction value is greater than the attack, therefore blocking the attack
				Interface.writeOut(attacker.getName() + "'s drain was resisted by " + targets[0].getName());
			} else {
				Interface.writeOut(attacker.getName() + " drains " + targets[0].getName() + " for " + damage + " damage");
				dealDamage(attacker, targets[0], damage);

				if (selfHeal > 0) {
					float capOver = attacker.modStat(Stat.HP, true, selfHeal);
					Interface.writeOut(attacker.getName() + " absorbs " + (selfHeal-capOver) + " health");
				}
			}
		
		} else if (manaUsed)
			Interface.writeOut(attacker.getName() + "'s attack missed");
	
		else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
		
	}
}
