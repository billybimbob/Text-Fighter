package combat.magic.shapeshift;

import java.util.Scanner;
import assets.*;
import combat.Ability;
import main.Index;
import main.Interface;

public class ChangeForm extends ShapeShift {

	private Monsters[] formList;
	
	public ChangeForm () {
		name = "Change Form";
		description = "A spell to transform into an eagle, salamander, or pangolin";
		manaCost = 5; //might get rid
		selfTar = true;
	}
	
	public void execute() {
		//this.shifter = (Hero)attacker;
		if (formList == null) { //temporary, can't put in constructor because null pointer
			Monsters[] formListStore = {new Monsters(Index.shiftMonList[0]), new Monsters(Index.shiftMonList[1]), new Monsters(Index.shiftMonList[2])};
			formList = formListStore;
			for (int i = 0; i <= formList.length-1; i++) {
				try {
					formList[i].addAttack((Ability)Index.attackList[10].clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		if (attacker.getStat("mp") >= manaCost) {
			attacker.modStat("mp", -manaCost);
			String changePrompt = "Which form do you want to take?";
			
			int availChange = formList.length;
			Monsters[] tempList = new Monsters[availChange];
			for (int i = 0; i < formList.length; i++) { //determine available transformations, removes caster from list
				if (attacker.name != formList[i].name) {
					tempList[i-(formList.length-availChange)] = formList[i];
				} else {
					availChange--;
					Monsters[] tempStore = new Monsters[availChange];
					for(int j = 0; j < tempStore.length; j++) {
						tempStore[j] = tempList[j];
					}
					tempList = null;
					tempList = tempStore;
				}
			}
			String[] formNames = new String[tempList.length]; //sets list of names of available transformations
			for (int i = 0; i < tempList.length; i++)
				formNames[i] = tempList[i].name;
			
			int formChoice = Interface.choiceInput(true, formNames, changePrompt)-1;
			System.out.println(attacker.name + " has transformed into " + tempList[formChoice].name);
			transform(attacker, tempList[formChoice], 5);
		} else {
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
		}
	}
}
