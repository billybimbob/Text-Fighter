package assets;

import combat.*;
import main.Index;

public class Monsters { //Temporary, probably make abstract later

	public String name;
	public int level = 1, storeTurn = 0; //Temporary
	public double hp, maxHp, mp, maxMp, att, def, mag, magR, spe, crit, damTurn = 0;
	public boolean aggro, multTurn, priority;
	public int[][] status; //burn, poison, potion, shapeshift, stun; 1st row is turn when activated, 2nd row is duration
	public Attacks[] moveList;
	public boolean attType; //true means physical attack
	public Monsters storedShifter;
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
		status = new int[5][2];
		try {
			Attacks[] moveStore = {(Attacks)Index.attackList[0].clone(), (Attacks)Index.attackList[special].clone()};
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
		status = new int[5][2];
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
		status = new int[5][2];
		this.moveList = copy.moveList; 
		for (int i = 0; i <= moveList.length-1; i++) {
			moveList[i].setAttacker(this);
		}
	}
	
	public void setStatus(int index, int startTurn, int duration) {
		status[index][0] = startTurn;
		status[index][1] = duration;
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
