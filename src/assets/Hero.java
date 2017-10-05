package assets;

import java.util.ArrayList;

public class Hero extends Monsters {

	public boolean attType = true; //true means physical attack;
	public final static double levMult = 2.5;
	public ArrayList<String> moveListNames = new ArrayList<>();
	
	public Hero (String name){
		super(name, true, true, 25, 20, 5, 5, 5, 5, 5, 5, 5);
		level = 1;
	}
	
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
		eva += level*levMult;
		spe += level*levMult;
	}
	public void modStat (String stat, int val) {
		switch (stat) {
		case "hp":
			hp += val;
			break;
		case "mp":
			mp += val;
			break;
		case "att":
			att += val;
			break;
		case "def":
			def += val;
			break;
		case "mag":
			mag += val;
			break;
		case "magR":
			magR += val;
			break;
		case "crit":
			crit += val;
			break;
		case "eva":
			eva += val;
			break;
		case "spe":
			spe += val;
			break;
		}
	}
}
