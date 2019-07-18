package combat.moves.magic.shapeshift;

import assets.*;
import assets.Shifter;
import combat.*;

public abstract class ShapeShift extends Ability { //abstract so doesn't have to implement execute
	
	public ShapeShift(Monster user) {
		super(user);
	}


	public void transform (Monster target, Monster shiftedMon, int duration) { //might want to find a way to use Monster constructor to change values 
		float hpRatio = target.getStatRatio(Stat.HP);
		float mpRatio = target.getStatRatio(Stat.MP);

		target.copyVals(shiftedMon);

		target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
		target.setStat(Stat.MP, target.getStat(Stat.MP)*mpRatio);
		target.setStatus(Status.SHIFT, duration);
	}

	public static void revert (Monster target) { //can't ref by movelist; update status
		
		if (target.getOriginal() != null) {
			float hpRatio = target.getStatRatio(Stat.HP);
			float mpRatio = target.getStatRatio(Stat.MP);

			target.copyVals(target.getOriginal());

			target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
			target.setStat(Stat.MP, target.getStat(Stat.MP)*mpRatio);
			target.setOriginal(null);
		}
	}

}
