package combat.moves.melee;

import assets.Monster;
import combat.moves.Ability;
import combat.Status;
import main.Interface;

public class ChargeAttack extends Ability {
	
	private int turnCount;
	
	public ChargeAttack (Monster user) {
		super(user);
		name = "Charged Strike";
		description = "A melee attack able to hit with twice accuarcy and damage, ignores armor, but requires a turn to charge, and more vulnerable";
		attType = true;
		manaCost = 6;
		attMod = 0.005f;
		damageMod = 3;
		turnCount = 0;
	}

	@Override
	public boolean resolved() {
		return turnCount == 0; //means reset count
	}

	@Override
	protected boolean preExecute() { //checks happen later
		return true;
	}
	
	protected void execute() { //might put the print statements in the fight class
		Monster attacker = this.getAttacker();
		
		if (turnCount == 1) { //checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats
			Monster target = this.currentTarget();
			String missPrompt = attacker.getName() + "'s attack missed";
			if (attackHit(missPrompt)) {
				
				boolean blocked = false;
				if (!critCheck()) { //reduce damage on failure
					String blockedPrompt = target.getName() + " resisted " + attacker.getName() + "'s attack";
					blocked = targetReduct(blockedPrompt);
				}
			
				if (!blocked) {
					String damPrompt = attacker.getName() + " lands a powerful hit on " 
						+ target.getName() + " for " + damage + " damage";
					dealDamage(damPrompt);
				}
						
				float sto = setAttMod(0.1f);
				if (attackHit()) {
					String stunPrompt = attacker.getName() + "'s charged attack stuns " + target.getName();
					applyStatus(Status.STUN, true, stunPrompt);
				}
				setAttMod(sto);
			}
			
			turnCount = 0;
			
		} else if (enoughMana() && turnCount == 0) { //checks if sufficient mana, and starts charged turn
			Interface.writeOut(attacker.getName() + " readies their swing");
			turnCount++;
		}
	}
}
