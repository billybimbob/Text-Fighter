package combat.moves.melee;

import assets.chars.Monster;
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
	
	protected void execute(Monster target) { //might put the print statements in the fight class
		if (turnCount == 1) { //checks if attack charged for 1 turn
			//Attack based on RNG and modified by stats

			String missPrompt = attacker.getName() + "'s attack missed";
			if (attackHit(target, missPrompt)) {
				
				boolean blocked = false;
				if (!critCheck()) { //reduce damage on failure
					String blockedPrompt = target.getName() + " resisted " + attacker.getName() + "'s attack";
					blocked = targetReduct(target, blockedPrompt);
				}
			
				if (!blocked) {
					dealDamage(attacker, target, damage);
					Interface.writeOut(attacker.getName() + " lands a powerful hit on " 
						+ target.getName() + " for " + damage + " damage");
				}
						
				float sto = setAttMod(0.1f);
				if (attackHit(target)) {
					Interface.writeOut(attacker.getName() + "'s charged attack stuns " + target.getName());
					target.setStatus(Status.STUN, true);
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
