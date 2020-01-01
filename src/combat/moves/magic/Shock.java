package combat.moves.magic;

import assets.Monster;
import combat.moves.Ability;
import combat.Status;

public class Shock extends Ability {

	private int duration;

	public Shock (Monster user) {
		super(user);
		name = "Shock";
		description = "A quick magic attack that also buffs evasiness of user";
		attType = false;
		priority = true;
		manaCost = 5;
		damageMod = 1.5f;
		duration = 3;
	}

	@Override
	protected boolean preExecute() {
		addTarget(this.getAttacker()); //for self buff
		return enoughMana();
	}

	protected void execute() {
		Monster attacker = this.getAttacker();
		Monster target = this.currentTarget();
		String failPrompt = attacker.getName() + "'s shock failed";
		
		if (attacker.equals(target)) {
			String evadePrompt = attacker.getName() + " gains increased evasiveness for " + duration + " turns";
			applyStatus(Status.DODGE, duration, evadePrompt);

		} else if (attackHit(failPrompt)) {
			String blockedPrompt = attacker.getName() + "'s shock was resisted by " + target.getName();
			if (!targetReduct(blockedPrompt)) { //Check if the defense reduction value is greater than the attack, therefore blocking the attack
				
				String damPrompt = attacker.getName() + " blasts " + target.getName() + " for " + damage + " damage";
				dealDamage(damPrompt);
			}
		}
	}

}
