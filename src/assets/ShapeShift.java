package assets;

import combat.Status;

public class ShapeShift {

	private static class ShiftInfo extends Monster.StatusInfo {
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
		target.stats = copy.stats;
		target.moveList = copy.moveList;
		target.setPassive(copy.getPassive());
	}
	
	private static ShiftInfo getShiftInfo(Monster mon) {
		return (ShiftInfo)(mon.status.get(Status.SHIFT));
	}

	static void initShift(Monster mon) {
		mon.status.put(Status.SHIFT, new ShiftInfo());
	}

	static boolean switchCheck(Monster.StatusInfo info, boolean turnOn) { //can only turn on/off Shift in trans/revert
		Monster original = ((ShiftInfo)info).getOriginal();
		return turnOn ? original != null : original == null;
	}


	public static Monster getOriginal(Monster mon) {
		return getShiftInfo(mon).getOriginal();
	}
	
	/**
	 * used for changing to another monster
	 * @param status for now expected to be SHIFT status, all else will act as basic setStatus
	 * @param duration any positive number to transform, 0 and negative will turn off status
	 * @param shifting the monster to transform into
	 */
	public static void transform (Monster target, Monster shiftedMon, int duration) { //might want to find a way to use Monster constructor to change values 
		if (duration > 0) {
			float hpRatio = (float)Math.ceil(target.getStatRatio(Stat.HP));
			float mpRatio = (float)Math.ceil(target.getStatRatio(Stat.MP));
		
			ShiftInfo info = getShiftInfo(target);
			if (info.getOriginal() == null)
				info.setOriginal((Monster)target.clone());

			ShapeShift.copyVals(target, shiftedMon);
			String oldName = info.getOriginal().getName();
			target.name = target.name + "(" + oldName + ")";

			target.setStatus(Status.SHIFT, duration);
	
			target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
			target.setStat(Stat.MP, target.getStat(Stat.MP)*mpRatio);
		}
			
	}

	public static void revert (Monster target) { //can't ref by movelist; update status
		ShiftInfo info = getShiftInfo(target);
		Monster original = info.getOriginal();

		if (original != null) {
			float hpRatio = (float)Math.ceil(target.getStatRatio(Stat.HP));
			float mpRatio = (float)Math.ceil(target.getStatRatio(Stat.MP));

			ShapeShift.copyVals(target, original);
			info.setOriginal(null);
			target.setStatus(Status.SHIFT, false);

			target.setStat(Stat.HP, target.getStat(Stat.HP)*hpRatio);
			target.setStat(Stat.MP, target.getStat(Stat.MP)*mpRatio);
		}
	}


}
