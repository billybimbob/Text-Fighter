package assets;

import combat.Attacks;
import main.Fight;

public abstract class ShapeShift extends Attacks {

	private int turnLength;
	
	public int getTurnLength() {
		return turnLength;
	}
	
	public void transform (Monsters target, Monsters shiftedMon) {
		target.storedShifter = new Monsters(target); //stores original attacker
		target = new Monsters(shiftedMon);
		System.out.println(target.storedShifter.hp + " " + target.maxHp);
		//target.hp*=(target.storedShifter.maxHp/target.storedShifter.hp);
		//target.mp = target.storedShifter.mp;
		//targets[0].status[3] = Fight.turnCount;
	}
	public static void revert (Monsters target) {
		double hpRatio = target.hp/target.maxHp;
		target = new Monsters(target.storedShifter);
		target.hp*=hpRatio; 
		target.status[3] = 0;
	}

}
