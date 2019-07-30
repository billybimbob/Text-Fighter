package combat.moves; //here to access ability members

import assets.*;
import combat.moves.magic.shapeshift.ShapeShift;
import main.*;

public class ChangeForm extends ShapeShift {

	private Monster[] formList;
	
	public ChangeForm (Monster user) {
		super(user);
		name = "Change Form";
		description = "A spell to transform into an eagle, salamander, or pangolin";
		manaCost = 1; //might get rid
		numTar = 0;
		priority = true;
		
		initFormList(this, attacker);
	}

	@Override
	public Object clone(Monster attacker) throws CloneNotSupportedException { //update attacker of formList
		ChangeForm copy = (ChangeForm)super.clone(attacker);
		initFormList(copy, attacker);

		return copy;
	}

	private static void initFormList(ChangeForm move, Monster attacker) {
		move.formList = new Monster[] { //see if I can parameterize; clone doesn't work here
			Index.createMonster("Eagle"), 
			Index.createMonster("Pangolin"), 
			Index.createMonster("Salamander") //circular dependency; bad
		};

		for(Monster shift: move.formList) {
			for (Ability ability: shift.getMoves())
				ability.attacker = attacker;
			
			shift.addAttack(move);
		}
	}
	
	public void execute() {
		//this.shifter = (Hero)attacker;
		
		if (enoughMana()) {
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

			int formChoice = -1;
			if (attacker.getClass() == Hero.class) {
				String[] formNames = new String[tempList.length]; //sets list of names of available transformations
				for (int i = 0; i < tempList.length; i++)
					formNames[i] = tempList[i].getName();
				String changePrompt = "Which form do you want to take?";
				
				formChoice = Interface.choiceInput(false, formNames, changePrompt)-1;

			} else
				formChoice = (int)(Math.random()*tempList.length);

			Monster newForm = tempList[formChoice];
			String beforeName = attacker.getName(), newName = newForm.getName();
			transform(attacker, newForm, 5);			
			Interface.writeOut(beforeName + " has transformed into " + newName);
			
		}
	}
}
