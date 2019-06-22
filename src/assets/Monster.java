package assets;

import java.util.*;
import combat.*;
import main.Index;

public class Monster { //Temporary, probably make abstract later

	private String name;
	private float damTurn = 0;
	private Map<String, Float> stats;
	private Map<String, Integer[]> status; //1st row is turn activated, 2nd is duration
	private Monster storedShifter;
	
	protected int level = 1;
	protected Ability[] moveList;

	public boolean attType, aggro, priority; //attType true means physical attack
	public Ability passive, turnMove, storeTurn; //temporary?
	
	public static final String[] STATNAMES = {"hp", "maxHp", "mp", "maxMp", "att", "def", "mag", "magR", "spe", "crit"}; //need to add edge cases for max health
	public static final String[] STATUSNAMES = {"burn", "poison", "potion", "reflect", "shapeshift", "stun"};
	public final static int levMult = 2;
	
	//constructor to have no ability
	public Monster (String name, boolean aggro, boolean attType, float[] stats) {
	//public Monsters (String name, boolean aggro, boolean attType, double hp, double mp, double att, double def, double mag, double magR, double spe, double crit) {
		this.name = name;
		this.aggro = aggro;
		this.attType = attType;

		this.stats = new HashMap<>();
		//double [] statVals = {hp,hp, mp, mp, att, def, mag, magR, spe, crit}; //order must be same as statsName
		int j = 0;
		for (int i=0; i<STATNAMES.length; i++) {
			setStat(STATNAMES[i], stats[j]);
			if (STATNAMES[i] != "hp" && STATNAMES[i] != "mp")
				j++;
		}

		initStatus();
	}

	//monster index constructor, basic attack and one special attack
	public Monster (String name, boolean aggro, boolean attType, float[] stats, int special) {
	//public Monsters (String name, boolean aggro, boolean attType, double hp, double mp, double att, double def, double mag, double magR, double spe, double crit, int special){
		this(name, aggro, attType, stats);

		try {
			Ability[] moveStore = {(Ability)Index.attackList[0].clone(this), 
									(Ability)Index.attackList[special].clone(this)};
			moveList = moveStore;
			
		} catch (CloneNotSupportedException c) {}
	}

	//copies to a new instance
	public Monster (Monster copy) { //not sure if deep or shallow
		this.name = copy.name;
		this.aggro = copy.aggro;
		this.attType = copy.attType;
		this.damTurn = copy.damTurn;

		this.stats = new HashMap<>(); //deep copy
		for (String statName: STATNAMES)
			this.setStat(statName, copy.getStat(statName));
		
		initStatus();

		try {		
			if (copy.passive != null)
				this.setPassive(copy.passive.clone(this));

			this.moveList = new Ability[copy.moveList.length];
			for (int i = 0; i < moveList.length; i++)
				this.moveList[i] = copy.moveList[i].clone(this);

		} catch (CloneNotSupportedException e) {}
	}

	private void initStatus() {
		status = new HashMap<>();
		Integer[] startStatus = {0, 0};
		for (String statusName: STATUSNAMES)
			status.put(statusName, startStatus.clone());
	}


	//accessors
	public String getName() {
		return name;
	}
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
		Integer[] val = this.status.get(status);
		int[] ret = {val[0], val[1]};
		return ret;
	}
	public Monster getShifter() {
		return storedShifter;
	}
	public double getDamTurn() {
		return this.damTurn;
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
	public void resetDamage() {
		this.damTurn = 0;
	}
	public void addDamTurn(double damage) {
		this.damTurn += damage;
	}
	public void setShifter(Monster storing) {
		this.storedShifter = storing;
	}

	//modifies stats based and restores health and mana
	public void levelUp () {
		level += 1;
		for (int i=0; i<STATNAMES.length; i++) {
			String stat = STATNAMES[i];
			if (i != 0 && i != 2 ) { //for hp and mp
				modStat(stat, level*levMult);
			}
		}
		setStat("hp", getStat("maxHp"));
		setStat("mp", getStat("maxMp"));
	}



	public int minDam(Monster attacker, boolean attType) { //max value for now is 10 for def and magR
		String hitStat = attType?"att":"mag", blockStat = attType?"def":"magR";
		int minDam = (int)attacker.getStat(hitStat);
		double stat = this.getStat(blockStat);
		minDam -= ((int)stat/2);
		return minDam>0 ? minDam : 0;
	}

	public String toString () {
		return name + " - " + getStat("hp") + " hp" + " - " + getStat("mp") + " mp" + " - " + getStat("spe") + " speed";
	}
	
}
