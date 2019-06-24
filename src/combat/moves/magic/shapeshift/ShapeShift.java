package combat.moves.magic.shapeshift;

import assets.Monster;
import assets.Monster.Stat;
import combat.*;
import main.Interface;

public abstract class ShapeShift extends Ability { //abstract so doesn't have to implement execute
	
	public ShapeShift(Monster user) {
		super(user);
	}

	private static float hpRatio(Monster target) { return target.getStat(Stat.HP)/target.getStat(Stat.MAXHP); }

	public void transform (Monster target, Monster shiftedMon, int duration) { //might want to find a way to use Monster constructor to change values 
		float hpRatio = hpRatio(target);
		Monster store = new Monster(target); //stores original attacker
		
		target = new Monster(shiftedMon);
		target.setShifter(store);
		
		target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
		target.setStatus("shapeshift", Interface.FIGHT.getTurnNum(), duration);
	}

	public static void revert (Monster target) { //can't ref by movelist
		if (target.getShifter() != null) {
			float hpRatio = hpRatio(target);

			target = new Monster(target.getShifter());

			target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
			target.setShifter(null);
		}
	}

}
