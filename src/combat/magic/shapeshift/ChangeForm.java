package combat.magic.shapeshift;

import java.util.Scanner;

import assets.*;
import combat.Ability;
import main.Index;
import main.Interface;

public class ChangeForm extends ShapeShift {

	private Monsters[] formList;
	private Scanner keyboard;
	
	public ChangeForm (Scanner keyboard) {
		name = "Change Form";
		description = "A spell to transform into an eagle, salamander, or pangolin";
		manaCost = 5; //might get rid
		selfTar = true;
		this.keyboard = keyboard;
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
		if (attacker.mp >= manaCost) {
			attacker.mp -= manaCost;
			String changePrompt = "Which form do you want to take?";
			
			int availChange = formList.length;
			String[] formNames = new String[availChange];
			for (int i = 0; i <= formList.length-1; i++) { //determine available transformations
				if (!(attacker.name == formList[i].name)) {
					formNames[i-(formList.length-availChange)] = formList[i].name;
				} else {
					availChange--;
					String[] formStore = new String[availChange];
					for(int j = 0; j <= formStore.length-1; j++) {
						formStore[j] = formNames[j];
					}
					formNames = null;
					formNames = formStore;
				}
			}
			
			int formChoice = Interface.choiceInput(keyboard, true, formNames, changePrompt)-1;
			System.out.println(attacker.name + " has transformed into " + formList[formChoice].name);
			
			transform(attacker, formList[formChoice], 5);
		} else {
			System.out.println(attacker.name + " tries to use " + name + ", but has insufficient mana");
		}
	}
}
