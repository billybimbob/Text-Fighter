package combat.magic.shapeshift;

import assets.Monsters;
import combat.*;
import main.Fight;

public abstract class ShapeShift extends Ability { //abstract so doesn't have to implement execute
	
	public void transform (Monsters target, Monsters shiftedMon, int duration) { //might want to find a way to use Monster constructor to change values 
		float hpRatio = target.getStat("hp")/target.getStat("maxHp");
		Monsters store = new Monsters(target); //stores original attacker
		target.storedShifter = store;

		target.name = shiftedMon.name;
		target.aggro = shiftedMon.aggro;
		for (int i=0; i<Monsters.statName.length; i++) { //copy stats
			String stat = Monsters.statName[i];
			target.setStat(stat, shiftedMon.getStat(stat));
		}

		for (int i=0; i<Monsters.statusName.length; i++) { //clears all status effects
			target.setStatus(Monsters.statusName[i], false);
		}

		if (shiftedMon.passive != null) {
			target.setPassive(shiftedMon.passive);
			target.passive.setAttacker(target);
			//System.out.println(target.passive.getName());
		}
		target.moveList = shiftedMon.moveList; 
		for (int i = 0; i <= target.moveList.length-1; i++) {
			target.moveList[i].setAttacker(target);
		}
		
		target.setStat("hp", target.getStat("hp")*hpRatio);
		target.setStatus("shapeshift", Fight.turnCount, duration);
	}
	public static void revert (Monsters target) { //look at comment on transform method
		float hpRatio = target.getStat("hp")/target.getStat("maxHp");
		
		target.name = target.storedShifter.name;
		target.aggro = target.storedShifter.aggro;
		for (int i=0; i<Monsters.statName.length; i++) { //copy stats
			String stat = Monsters.statName[i];
			target.setStat(stat, target.storedShifter.getStat(stat));
		}

		for (int i=0; i<Monsters.statusName.length; i++) { //clears all status effects
			target.setStatus(Monsters.statusName[i], false);
		}
		
		if (target.storedShifter.passive != null) {
			target.setPassive(target.storedShifter.passive);
			target.passive.setAttacker(target);
			//System.out.println(target.passive.getName());
		} else
			target.setPassive(null);
		target.moveList = target.storedShifter.moveList;
		target.setStat("hp", target.getStat("hp")*hpRatio);
		target.storedShifter = null;
	}

}
