package assets;

import java.util.ArrayList;

import combat.*;
import main.Index;

public class Hero extends Monsters {

	public boolean attType; //true means physical attack;
	public final static double levMult = 2.5;
	public ArrayList<String> moveListNames = new ArrayList<>();
	
	public Hero (String name, boolean classes){ //if classes true, warrior
		super(name, true, true, 25, 20, 5, 5, 5, 5, 5, 5);
		level = 1;
		try {
			if (classes) {
				Attacks[] moveStore = {(Attacks)Index.attackList[0].clone(), (Attacks)Index.attackList[1].clone(), (Attacks)Index.attackList[2].clone()};
				moveList = moveStore;
				attType = true;
				mag = 7;
				att = 3;
			} else {
				Attacks[] moveStore = {(Attacks)Index.attackList[0].clone(), (Attacks)Index.attackList[3].clone(), (Attacks)Index.attackList[4].clone(), (Attacks)Index.attackList[5].clone()};
				moveList = moveStore;
				attType = false;
				hp = 40;
				att = 7;
				mag = 3;
			}
			//priority = true;
			for (int i = 0; i <= moveList.length-1; i++) {
				moveList[i].attacker = this;
			}
		} catch (CloneNotSupportedException c) {}
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
		case "spe":
			spe += val;
			break;
		}
	}
}
