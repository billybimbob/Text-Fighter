package assets;

import combat.Attacks;

public class Monsters { //Temporary, probably make abstract later

	public String name;
	public int level = 0, store = 0; //Temporary
	public double hp, maxHp, mp, maxMp, att, def, mag, magR, crit, eva, spe;
	public boolean aggro, stun, skip;
	public Attacks[] moveList;
	public boolean attType = true; //true means physical attack
	
	public Monsters (String name, boolean aggro, boolean attType, double hp, double mp, double att, double def, double mag, double magR, double crit, double eva, double spe){
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
		this.crit = crit;
		this.eva = eva;
		this.spe = spe;
		
	}
	
}
