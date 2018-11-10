package assets;

import combat.*;
import main.Index;

public class Hero extends Monsters {

	public String[] moveListNames;
	
	public Hero (String name, int classes) { //if classes true, warrior
		super(name, true, true, new float[]{25, 20, 5, 5, 5, 5, 5, 5});
		level = 1;
		try { //redfine from starting vals
			switch(classes) {
			case 1: //warrior
				Ability[] moveStore1 = {
						(Ability)Index.attackList[0].clone(), (Ability)Index.attackList[1].clone(),
						(Ability)Index.attackList[2].clone(), (Ability)Index.attackList[3].clone()};
				moveList = moveStore1;
				attType = true;
				setStat("maxHp", 40.0f); //try iterating later
				setStat("hp", getStat("maxHp"));
				setStat("att", 7.0f);
				setStat("mag", 1.0f);
				setStat("def", 7.0f);
				break;
			case 2: //mage
				Ability[] moveStore2 = {
						(Ability)Index.attackList[0].clone(), (Ability)Index.attackList[4].clone(),
						(Ability)Index.attackList[5].clone(), (Ability)Index.attackList[6].clone(),
						(Ability)Index.attackList[7].clone(), (Ability)Index.attackList[8].clone()};
				moveList = moveStore2;
				attType = false;
				setStat("mag", 7.0f);
				setStat("att", 1.0f);
				setStat("magR", 7.0f);
				setStat("def", 4.0f);
				setStat("mag", 7.0f);
				break;
			case 3: //shifter
				Ability[] moveStore3 = {(Ability)Index.attackList[10].clone()};
				moveList = moveStore3;
				attType = false;
				setStat("hp", 10.0f);
				setStat("maxHp", getStat("hp"));
				setStat("att", 1.0f);
				setStat("mag", 1.0f);
				setStat("def", 1.0f);
				setStat("magR", 1.0f);
				setStat("spe", 7.0f);
				setStat("crit", 1.0f);
				break;
			}
			
			for (int i = 0; i <= moveList.length-1; i++) {
				moveList[i].setAttacker(this);
			}
		} catch (CloneNotSupportedException c) {}
	}
}
