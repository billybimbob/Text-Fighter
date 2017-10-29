package assets;

import combat.*;
import main.Index;

public class Hero extends Monsters {

	public String[] moveListNames;
	
	public Hero (String name, boolean classes){ //if classes true, warrior
		super(name, true, true, 25, 20, 5, 5, 5, 5, 5, 5);
		level = 1;
		try {
			if (classes) {
				Attacks[] moveStore = {(Attacks)Index.attackList[0].clone(), (Attacks)Index.attackList[1].clone(),
						(Attacks)Index.attackList[2].clone(), (Attacks)Index.attackList[3].clone()};
				moveList = moveStore;
				attType = true;
				hp = 40;
				maxHp = hp;
				att = 7;
				mag = 3;
			} else {
				Attacks[] moveStore = {(Attacks)Index.attackList[0].clone(), (Attacks)Index.attackList[4].clone(),
						(Attacks)Index.attackList[5].clone(), (Attacks)Index.attackList[6].clone(),
						(Attacks)Index.attackList[7].clone(), (Attacks)Index.attackList[8].clone(), (Attacks)Index.attackList[10].clone()};
				moveList = moveStore;
				attType = false;
				mag = 7;
				att = 3;
			}
			for (int i = 0; i <= moveList.length-1; i++) {
				moveList[i].setAttacker(this);
			}
		} catch (CloneNotSupportedException c) {}
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
