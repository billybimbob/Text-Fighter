package combat.moves.magic.shapeshift;

import assets.*;
import assets.Shifter;
import combat.*;
import main.Interface;

public abstract class ShapeShift extends Ability { //abstract so doesn't have to implement execute
	
	public ShapeShift(Monster user) {
		super(user);
	}

	private static float hpRatio(Monster target) {
		return target.getStat(Stat.HP)/target.getStat(Stat.MAXHP);
	}


	public void transform (Monster target, Monster shiftedMon, int duration) { //might want to find a way to use Monster constructor to change values 
		float hpRatio = hpRatio(target);
		
		Shifter newShift = target.getClass() == Shifter.class //keep original if already shifted
							? new Shifter( (Shifter)target )
							: new Shifter(shiftedMon, target); //keep eye on
		target = newShift;

		target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
		target.setStatus(Status.SHIFT, Interface.FIGHT.getTurnNum(), duration);
	}

	public static void revert (Monster target) { //can't ref by movelist; update status
		
		if (target.getClass() == Shifter.class) {
			Shifter shift = (Shifter)target;
			float hpRatio = hpRatio(target);

			target = shift.getOriginal();

			target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
			shift = null;
		}
	}

}
