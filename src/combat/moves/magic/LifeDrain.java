package combat.moves.magic;

import assets.Monster;
import assets.Monster.Stat;
import combat.*;
import main.Interface;

public class LifeDrain extends Ability {

	public LifeDrain(Monster user) {
		super(user);
		name = "Life Drain";
		description = "A magic attack that heals for for a portion of damage dealt";
		attType = false;
		manaCost = 3;
		baseDamMod = 1.4f;
	}

	public void execute() {
		Monster[] targets = attacker.getTargets();

		if (enoughMana()) {
			attacker.modStat(Stat.MP, -manaCost);
			if (attackHit(targets[0], 0.01)) { //Check if attack will be successful
				baseDamage();
				targetReduct(targets[0]);
				float selfHeal = (int)(baseDam*0.5);
				
				if (damDealt()) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
					Interface.writeOut(attacker.getName() + "'s drain was resisted by " + targets[0].getName());
				} else {
					Interface.writeOut(attacker.getName() + " drains " + targets[0].getName() + " for " + baseDam + " damage");
					dealDam(attacker, targets[0], baseDam);
					if (selfHeal > 0 && attacker.getStat(Stat.HP) < attacker.getStat(Stat.MAXHP)) {
						attacker.modStat(Stat.HP, selfHeal);
						Interface.writeOut(attacker.getName() + " absorbs " + selfHeal + " health");
					}
				}
			} else {
				Interface.writeOut(attacker.getName() + "'s attack missed");
			}
		} else {
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
		}
	}
}
