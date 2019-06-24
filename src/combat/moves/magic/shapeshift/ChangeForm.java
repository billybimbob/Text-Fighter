package combat.moves.magic.shapeshift;

import assets.*;
import main.Index;
import main.Interface;

public class ChangeForm extends ShapeShift {

	private Monster[] formList;
	
	public ChangeForm (Monster user) {
		super(user);
		name = "Change Form";
		description = "A spell to transform into an eagle, salamander, or pangolin";
		manaCost = 5; //might get rid
		numTar = 0;
		
		formList = new Monster[]{ //see if I can parameterize
				new Monster(Index.shiftMonList[0]), 
				new Monster(Index.shiftMonList[1]), 
				new Monster(Index.shiftMonList[2])};
				
		
		for(Monster shift: formList)
			shift.addAttack(this);
		
	}
	
	public void execute() {
		//this.shifter = (Hero)attacker;
		
		if (enoughMana()) {
			attacker.modStat("mp", -manaCost);
			String changePrompt = "Which form do you want to take?";
			int availChange = formList.length;
			Monster[] tempList = new Monster[availChange];

			for (int i = 0; i < formList.length; i++) { //determine available transformations, removes caster from list
				if (attacker.getName() != formList[i].getName()) {
					tempList[i-(formList.length-availChange)] = formList[i];
				} else {
					Monster[] tempStore = new Monster[--availChange];
					for(int j = 0; j < tempStore.length; j++) {
						tempStore[j] = tempList[j];
					}
					tempList = null;
					tempList = tempStore;
				}
			}
			String[] formNames = new String[tempList.length]; //sets list of names of available transformations
			for (int i = 0; i < tempList.length; i++)
				formNames[i] = tempList[i].getName();
			
			int formChoice = Interface.choiceInput(false, formNames, changePrompt)-1;
			transform(attacker, tempList[formChoice], 5);
			Interface.writeOut(attacker.getName() + " has transformed into " + tempList[formChoice].getName());
			
		} else {
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");
		}
	}
}