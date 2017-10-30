package assets;

import combat.*;
import main.Index;

public class Monsters { //Temporary, probably make abstract later

	public String name;
	public int level = 1, storeTurn = 0, minDam; //Temporary
	public double hp, maxHp, mp, maxMp, att, def, mag, magR, spe, crit, damTurn = 0;
	public boolean aggro, multTurn, priority;
	public int[][] status; //passive, burn, poison, potion, shapeshift, stun; 1st row is turn when activated, 2nd row is duration
	public Ability[] moveList;
	public Ability passive;
	public boolean attType; //true means physical attack
	public Monsters storedShifter;
	public static int statusLen = 6;
	public final static double levMult = 2.5;
	
	//monster index constructor, basic attack and one special attack
	public Monsters (String name, boolean aggro, boolean attType, double hp, double mp, double att, double def, double mag, double magR, double spe, double crit, int special){
		this.name = name;
		this.aggro = aggro;
		this.hp = hp;
		this.maxHp = hp;
		this.mp = mp;
		this.maxMp = mp;
		this.att = att;
		this.def = def;
		this.mag = mag;
		this.magR = magR;
		this.spe = spe;
		this.crit = crit;
		status = new int[statusLen][2];
		try {
			Ability[] moveStore = {(Ability)Index.attackList[0].clone(), (Ability)Index.attackList[special].clone()};
			moveList = moveStore;
			for (int i = 0; i <= moveList.length-1; i++) {
				moveList[i].setAttacker(this);
			}
		} catch (CloneNotSupportedException c) {}
	}
	//constructor to have more than one ability
	public Monsters (String name, boolean aggro, boolean attType, double hp, double mp, double att, double def, double mag, double magR, double spe, double crit){
		this.name = name;
		this.aggro = aggro;
		this.hp = hp;
		this.maxHp = hp;
		this.mp = mp;
		this.maxMp = mp;
		this.att = att;
		this.def = def;
		this.mag = mag;
		this.magR = magR;
		this.spe = spe;
		this.crit = crit;
		status = new int[statusLen][2];
	}
	//copies to a new instance
	public Monsters (Monsters copy) { //not sure if deep or shallow
		this.name = copy.name;
		this.aggro = copy.aggro;
		this.hp = copy.hp;
		this.maxHp = copy.maxHp;
		this.mp = copy.mp;
		this.maxMp = copy.mp;
		this.att = copy.att;
		this.def = copy.def;
		this.mag = copy.mag;
		this.magR = copy.magR;
		this.spe = copy.spe;
		this.crit = copy.crit;
		status = new int[statusLen][2];
		this.moveList = copy.moveList; 
		for (int i = 0; i <= moveList.length-1; i++) {
			moveList[i].setAttacker(this);
		}
	}
	
	public static int getStatNum(String stat) {
		int statNum = -1;
		switch(stat) {
		case "passive":
			statNum = 0;
			break;
		case "burn":
			statNum = 1;
			break;
		case "poison":
			statNum = 2;
			break;
		case "potion":
			statNum = 3;
			break;
		case "shapeshift":
			statNum = 4;
			break;
		case "stun":
			statNum = 5;
			break;
		}
		return statNum;	
	}
	
	//setters
	public void addAttack(Ability adding) {
		Ability[] moveStore = new Ability[moveList.length+1];
		for (int i = 0; i <= moveList.length-1; i++) {
			moveStore[i] = moveList[i];
		}
		moveStore[moveList.length] = adding;
		moveList = null;
		moveList = moveStore;
	}
	public void setMinDam(boolean attType) { //max value for now is 10 for def and magR
		minDam = 5;
		double stat = 0;
		if (attType)
			stat = def;
		else
			stat = magR;
		
		for (int i = 1; i < stat; i+=2) {
			minDam--;
			if (minDam == 0)
				break;
		}
		//System.out.println(minDam);
	}
	public void setPassive(Ability passive) {
		this.passive = passive;
		setStatus("passive", true);
	}
	public void setStatus(String stat, int startTurn, int duration) {
		int index = getStatNum(stat);
		status[index][0] = startTurn;
		status[index][1] = duration;
	}
	public void setStatus(String stat, boolean toggle) {
		int index = getStatNum(stat);
		status[index][1] = 0;
		if (toggle)
			status[index][0] = 1;
		else
			status[index][0] = 0;
	}
	
	//modifies stats based and restores health and mana
	public void levelUp () {
		level += 1;
		maxHp += level*levMult;
		hp = maxHp;
		maxMp += level*levMult;
		mp = maxMp;
		att += level*levMult;
		def += level*levMult;
		mag += level*levMult;
		magR += level*levMult;
		crit += level*levMult;
		spe += level*levMult;
	}
	
}
