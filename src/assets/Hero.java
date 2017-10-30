package assets;

import combat.*;
import main.Index;

public class Hero extends Monsters {

	public String[] moveListNames;
	
	public Hero (String name, int classes){ //if classes true, warrior
		super(name, true, true, 25, 20, 5, 5, 5, 5, 5, 5);
		level = 1;
		try {
			switch(classes) {
			case 1: //warrior
				Ability[] moveStore1 = {(Ability)Index.attackList[0].clone(), (Ability)Index.attackList[1].clone(),
						(Ability)Index.attackList[2].clone(), (Ability)Index.attackList[3].clone()};
				moveList = moveStore1;
				attType = true;
				hp = 40;
				maxHp = hp;
				att = 7;
				mag = 1;
				def = 7;
				break;
			case 2: //mage
				Ability[] moveStore2 = {(Ability)Index.attackList[0].clone(), (Ability)Index.attackList[4].clone(),
						(Ability)Index.attackList[5].clone(), (Ability)Index.attackList[6].clone(),
						(Ability)Index.attackList[7].clone(), (Ability)Index.attackList[8].clone()};
				moveList = moveStore2;
				attType = false;
				mag = 7;
				att = 1;
				magR = 7;
				def = 4;
				break;
			case 3: //shifter
				Ability[] moveStore3 = {(Ability)Index.attackList[10].clone()};
				moveList = moveStore3;
				attType = false;
				hp = 10;
				maxHp = hp;
				att = 1;
				mag = 1;
				def = 1;
				magR = 1;
				spe = 7;
				crit = 1;
				break;
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
