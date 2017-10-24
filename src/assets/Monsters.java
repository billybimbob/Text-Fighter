package assets;

import combat.*;
import main.Index;

public class Monsters { //Temporary, probably make abstract later

	public String name;
	public int level = 0, store = 0; //Temporary
	public double hp, maxHp, mp, maxMp, att, def, mag, magR, spe, crit, damTurn = 0;
	public boolean aggro, stun, skip, priority;
	public Attacks[] moveList;
	public boolean attType = true; //true means physical attack
	
	//monster index constructor
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
		try {
			Attacks[] moveStore = {(Attacks)Index.attackList[0].clone(), (Attacks)Index.attackList[special].clone()};
			moveList = moveStore;
			for (int i = 0; i <= moveList.length-1; i++) {
				moveList[i].attacker = this;
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
	}
	//copies to a new instance
	public Monsters (Monsters copy) {
		this.name = copy.name;
		this.aggro = copy.aggro;
		this.hp = copy.hp;
		this.maxHp = copy.hp;
		this.mp = copy.mp;
		this.maxMp = copy.mp;
		this.att = copy.att;
		this.def = copy.def;
		this.mag = copy.mag;
		this.magR = copy.magR;
		this.spe = copy.spe;
		this.crit = copy.crit;
		this.moveList = copy.moveList;
		for (int i = 0; i <= moveList.length-1; i++) {
			moveList[i].attacker = this;
		}
	}
	
}
