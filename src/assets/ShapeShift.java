package assets;

import combat.Attacks;
import main.Fight;

public abstract class ShapeShift extends Attacks {

	private int turnLength;
	
	public int getTurnLength() {
		return turnLength;
	}
	
	public void transform (Monsters target, Monsters shiftedMon) { //might want to find a way to use Monster constructor to change values 
		Monsters store = new Monsters(target); //stores original attacker
		//target = new Monsters(shiftedMon);
		target.name = shiftedMon.name;
		target.aggro = shiftedMon.aggro;
		target.hp = shiftedMon.hp;
		target.maxHp = shiftedMon.maxHp;
		target.att = shiftedMon.att;
		target.def = shiftedMon.def;
		target.mag = shiftedMon.mag;
		target.magR = shiftedMon.magR;
		target.spe = shiftedMon.spe;
		target.crit = shiftedMon.crit;
		target.status = new int[5];
		target.moveList = shiftedMon.moveList; 
		for (int i = 0; i <= target.moveList.length-1; i++) {
			target.moveList[i].setAttacker(target);
		}
		target.storedShifter = store;
		target.hp*=(target.storedShifter.maxHp/target.storedShifter.hp);
		target.status[3] = Fight.turnCount;
	}
	public static void revert (Monsters target) { //look at comment on transform method
		double hpRatio = target.hp/target.maxHp;
		//target = new Monsters(target.storedShifter);
		target.name = target.storedShifter.name;
		target.aggro = target.storedShifter.aggro;
		target.hp = target.storedShifter.hp;
		target.maxHp = target.storedShifter.maxHp;
		target.att = target.storedShifter.att;
		target.def = target.storedShifter.def;
		target.mag = target.storedShifter.mag;
		target.magR = target.storedShifter.magR;
		target.spe = target.storedShifter.spe;
		target.crit = target.storedShifter.crit;
		target.status = new int[5];
		target.hp*=hpRatio;
		target.storedShifter = null;
	}

}
