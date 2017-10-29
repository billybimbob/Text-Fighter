package combat.magic.shapeshift;

import java.util.Scanner;

import assets.*;
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
		if (formList == null) { //temporary
			Monsters[] formListStore = {Index.shiftMonList[0], Index.shiftMonList[1], Index.shiftMonList[2]};
			formList = formListStore;
		}
		if (attacker.mp >= manaCost) {
			attacker.mp -= manaCost;
			String changePrompt = "Which form do you want to take?";
			
			int availChange = formList.length;
			String[] formNames = new String[availChange];
			for (int i = 0; i <= formList.length-1; i++) { //determine available transformations
				if (!(attacker.name == formList[i].name)) {
					formNames[i] = formList[i].name;
				} else {
					availChange--;
					String[] formStore = new String[availChange];
					for(int j = 0; j <= formStore.length; j++) {
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
