package combat.moves.magic;

import assets.Stat;
import assets.Monster;
import combat.moves.Ability;
import main.Interface;

public class LifeDrain extends Ability {

	private float heal;

	public LifeDrain(Monster user) {
		super(user);
		name = "Life Drain";
		description = "A magic attack that heals for for a portion of damage dealt";
		attType = false;
		manaCost = 3;
		damageMod = 1.4f;
	}

	@Override
	protected boolean preExecute() {
		addTarget(this.getAttacker()); //for heal
		return enoughMana();
	}

	protected void execute() {
		Monster attacker = this.getAttacker();
		Monster target = this.currentTarget();
		String failPrompt = attacker.getName() + "'s drain failed";

		if (target.equals(attacker)) {
			float capOver = attacker.modStat(Stat.HP, true, heal); //for cap
			float selfHeal = heal-capOver;
			if (selfHeal > 0) {
				Interface.writeOut(attacker.getName() + " absorbs " + selfHeal + " health");
				damage = selfHeal;
			}
			
		} else if (attackHit(failPrompt)) { //Check if attack will be successful
			String blockedPrompt = attacker.getName() + "'s drain was resisted by " + target.getName();
			if (!targetReduct(blockedPrompt)) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				String damPrompt = attacker.getName() + " drains " + target.getName() + " for " + damage + " damage";
				dealDamage(damPrompt);
			}
			heal = (int)(damage*0.5);
		}
		
	}
}
