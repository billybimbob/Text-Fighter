package assets.chars;

import assets.Stat;
import combat.Status;

public class ShapeShift {

	static class ShiftInfo extends Monster.StatusInfo {
		private Monster original;

		Monster getOriginal() { return original; }
		void setOriginal(Monster original) { this.original = original; }
	}

	/**
	 * used for transforming; so don't have to make many setters; status is kept
	 */
	private static void copyVals (Monster target, Monster copy) {
		target.name = copy.name;
		target.attType = copy.attType;
		target.passive = copy.passive;
		target.moveList = copy.moveList;
		target.stats = copy.stats;
	}

	static void offCheck(Monster checking) {
		ShiftInfo shift = (ShiftInfo)(checking.status.get(Status.SHIFT));
		Monster original = shift.getOriginal();
		if (original != null) {
			ShapeShift.copyVals(checking, original);
			shift.setOriginal(null);
		}
	}
	
	/**
	 * used for changing to another monster
	 * @param status for now expected to be SHIFT status, all else will act as basic setStatus
	 * @param duration any positive number to transform, 0 and negative will turn off status
	 * @param shifting the monster to transform into
	 */
	public static void transform (Monster target, Monster shiftedMon, int duration) { //might want to find a way to use Monster constructor to change values 
		if (duration > 0) {
			float hpRatio = target.getStatRatio(Stat.HP);
			float mpRatio = target.getStatRatio(Stat.MP);
		
			ShiftInfo info = (ShiftInfo)target.status.get(Status.SHIFT);
			if (info.getOriginal() == null) {
				try { info.setOriginal((Monster)target.clone()); } catch (CloneNotSupportedException e) {};
			}

			ShapeShift.copyVals(target, shiftedMon);
			String oldName = info.getOriginal().name;
			target.name = shiftedMon.name + "(" + oldName + ")";

			target.setStatus(Status.SHIFT, duration);
	
			target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
			target.setStat(Stat.MP, target.getStat(Stat.MP)*mpRatio);
		}
			
	}

	public static void revert (Monster target) { //can't ref by movelist; update status
		float hpRatio = target.getStatRatio(Stat.HP);
		float mpRatio = target.getStatRatio(Stat.MP);

		target.setStatus(Status.SHIFT, false); //can be called without revert, leading to inconsistent state

		target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
		target.setStat(Stat.MP, target.getStat(Stat.MP)*mpRatio);
	}


}
