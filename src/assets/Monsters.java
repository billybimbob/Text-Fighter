package assets;

import java.util.*;
import combat.*;
import main.Index;

public class Monsters { //Temporary, probably make abstract later

	public String name;
	public int level = 1, minDam;
	public double damTurn = 0;
	private HashMap<String, Float> stats;
	private HashMap<String, Integer[]> status; //1st row is turn activated, 2nd is duration
	public boolean attType, aggro, priority; //attType true means physical attack
	public Ability[] moveList;
	public Ability passive, turnMove, storeTurn; //temporary?
	public Monsters storedShifter;
	
	public static final String[] statName = {"hp", "maxHp", "mp", "maxMp", "att", "def", "mag", "magR", "spe", "crit"}; //need to add edge cases for max health
	public static final String[] statusName = {"burn", "poison", "potion", "reflect", "shapeshift", "stun"};
	public final static int levMult = 2;
	
	//monster index constructor, basic attack and one special attack
	public Monsters (String name, boolean aggro, boolean attType, float[] stats, int special) {
	//public Monsters (String name, boolean aggro, boolean attType, double hp, double mp, double att, double def, double mag, double magR, double spe, double crit, int special){
		this.name = name;
		this.aggro = aggro;
		this.attType = attType;

		this.stats = new HashMap<String, Float>(); //set stats
		//double [] statVals = {hp,hp, mp, mp, att, def, mag, magR, spe, crit}; //order must be same as statsName
		int j = 0;
		for (int i=0; i<statName.length; i++) {
			setStat(statName[i], stats[j]);
			if (statName[i] != "hp" && statName[i] != "mp")
				j++;
		}
		status = new HashMap<String, Integer[]>(); //set status
		Integer[] startStatus = {0, 0};
		for (int i=0; i<statusName.length; i++) {
			status.put(statusName[i], startStatus.clone());
		}

		try {
			Ability[] moveStore = {(Ability)Index.attackList[0].clone(), (Ability)Index.attackList[special].clone()};
			moveList = moveStore;
			for (int i = 0; i <= moveList.length-1; i++) {
				moveList[i].setAttacker(this);
			}
		} catch (CloneNotSupportedException c) {}
	}
	//constructor to have no ability
	public Monsters (String name, boolean aggro, boolean attType, float[] stats) {
	//public Monsters (String name, boolean aggro, boolean attType, double hp, double mp, double att, double def, double mag, double magR, double spe, double crit) {
		this.name = name;
		this.aggro = aggro;
		this.attType = attType;

		this.stats = new HashMap<String, Float>();
		//double [] statVals = {hp,hp, mp, mp, att, def, mag, magR, spe, crit}; //order must be same as statsName
		int j = 0;
		for (int i=0; i<statName.length; i++) {
			setStat(statName[i], stats[j]);
			if (statName[i] != "hp" && statName[i] != "mp")
				j++;
		}

		status = new HashMap<String, Integer[]>();
		Integer[] startStatus = {0, 0};
		for (int i=0; i<statusName.length; i++) {
			status.put(statusName[i], startStatus.clone());
		}
	}
	//copies to a new instance
	public Monsters (Monsters copy) { //not sure if deep or shallow
		this.name = copy.name;
		this.aggro = copy.aggro;
		this.attType = copy.attType;
		
		stats = new HashMap<String, Float>();
		for (int i=0; i<statName.length; i++) {
			String stat = statName[i];
			this.setStat(stat, copy.getStat(stat));
		}
		
		status = new HashMap<String, Integer[]>();
		Integer[] startStatus = {0, 0};
		for (int i=0; i<statusName.length; i++) {
			status.put(statusName[i], startStatus.clone());
		}

		if (copy.passive != null) {
			this.setPassive(copy.passive);
			this.passive.setAttacker(this);
		}
		this.moveList = copy.moveList; 
		for (int i = 0; i < moveList.length; i++) {
			moveList[i].setAttacker(this);
		}
	}

	//accessors
	public float getStat (String stat) { //most likely where nulls arise
		float ret = 0;
		//try {
			//System.out.println(this.name);
			ret = this.stats.get(stat);
		//} catch (Exception e) {
		//	System.out.println(e);
		//}

		return ret;
	}
	public int[] getStatus (String status) { //convert to int[]
		int[] ret = {this.status.get(status)[0], this.status.get(status)[1]};
		return ret;
	}


	//mutators
	public void addAttack(Ability adding) {
		Ability[] moveStore = new Ability[moveList.length+1];
		for (int i = 0; i <= moveList.length-1; i++) {
			moveStore[i] = moveList[i];
		}
		moveStore[moveList.length] = adding;
		moveList = null;
		moveList = moveStore;
	}
	public void setMinDam(Monsters attacker, boolean attType) { //max value for now is 10 for def and magR
		String hitStat = attType?"att":"mag", blockStat = attType?"def":"magR";
		minDam = (int)attacker.getStat(hitStat);
		double stat = this.getStat(blockStat);
		minDam -= ((int)stat/2);
		if (minDam < 0)
			minDam = 0;
		/*for (int i = 1; i < stat; i+=2) {
			minDam--;
			if (minDam == 0)
				break;
		}*/
		//System.out.println(minDam);
	}
	public void setPassive(Ability passive) {
		if (passive.getPassive()) 
			this.passive = passive;
		else
			System.out.println("Not a valid ability");
	}

	public void setStat (String stat, float val) {
		//System.out.println("setting " + this.name + "\'s " + stat);
		stats.put(stat, val);
	}
	public void modStat (String stat, float val) { //changes stat by val add max mod cases
		float newVal = stats.get(stat)+val;	
		setStat(stat, newVal);
	}
	public void setStatus(String stat, int startTurn, int duration) {
		status.get(stat)[0] = startTurn;
		status.get(stat)[1] = duration;
	}
	public void setStatus(String stat, boolean toggle) {
		status.get(stat)[1] = 0; //does not matter
		status.get(stat)[0] = toggle?1:0;
	}
	
	//modifies stats based and restores health and mana
	public void levelUp () {
		level += 1;
		for (int i=0; i<statName.length; i++) {
			String stat = statName[i];
			if (i != 0 && i != 2 ) { //for hp and mp
				modStat(stat, level*levMult);
			}
		}
		setStat("hp", getStat("maxHp"));
		setStat("mp", getStat("maxMp"));
	}

	public String toString () {
		return name + " - " + getStat("hp") + " hp" + " - " + getStat("mp") + " mp" + " - " + getStat("spe") + " speed";
	}
	
}
