package combat.moves.melee;

import combat.moves.Ability;
import assets.Monster;
import main.Interface;

public class Revenge extends Ability {

	private float turnDamage;

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
		Monster attacker = this.getAttacker();
		int prevRound = Interface.currentFight().getTurnNum()-1;
		
		turnDamage = Interface.currentFight().getLogs()
			.getTurnDamage(prevRound, attacker); //keep eye on

		Interface.writeOut(attacker.getName() + " enacts revenge");
		return enoughMana();
	}

	@Override
	protected void execute() {
		this.damage = turnDamage;
		String damPrompt = this.currentTarget().getName() + " is dealt " + this.damage + " damage";
		dealDamage(damPrompt);
	}
}