package combat.magic.shapeshift;

import assets.Monster;
import combat.*;
import main.Interface;

public abstract class ShapeShift extends Ability { //abstract so doesn't have to implement execute
	
	public ShapeShift(Monster user) {
		super(user);
	}

	public void transform (Monster target, Monster shiftedMon, int duration) { //might want to find a way to use Monster constructor to change values 
		float hpRatio = target.getStat("hp")/target.getStat("maxHp");
		Monster store = new Monster(target); //stores original attacker
		
		target = new Monster(shiftedMon);
		target.setShifter(store);
		
		target.setStat("hp", target.getStat("hp")*hpRatio);
		target.setStatus("shapeshift", Interface.FIGHT.getTurnNum(), duration);
	}

	public static void revert (Monster target) { //can't ref by movelist
		if (target.getShifter() != null) {
			float hpRatio = target.getStat("hp")/target.getStat("maxHp");

			target = new Monster(target.getShifter());

			target.setStat("hp", target.getStat("hp")*hpRatio);
			target.setShifter(null);
		}
	}

}
