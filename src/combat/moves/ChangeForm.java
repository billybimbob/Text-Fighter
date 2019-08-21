package combat.moves; //here to access Ability members

import java.util.*;
import java.util.stream.Collectors;

import assets.*;
import combat.Status;
import combat.moves.Ability;
import main.*;

public class ChangeForm extends Ability {

	private Monster[] formList;
	
	public ChangeForm (Monster user) {
		super(user);
		name = "Change Form";
		description = "A spell to transform into an eagle, salamander, or pangolin";
		manaCost = 1; //might get rid
		numTar = 0;
		priority = true;
		
		this.initFormList(attacker);
	}

	@Override
	public Object clone(Monster attacker) { //update attacker of formList
		ChangeForm copy = (ChangeForm)super.clone(attacker);
		copy.initFormList(attacker);
		return copy;
	}

	private void initFormList(Monster attacker) {
		this.formList = new Monster[] { //see if I can parameterize; clone doesn't work here
			Index.createMonster("Eagle"), 
			Index.createMonster("Pangolin"), 
			Index.createMonster("Salamander") //circular dependency; bad
		};

		for(Monster shift: this.formList) { //order not sorted
			Ability passive = shift.getPassive();
			
			if (passive != null)
				passive.attacker = attacker;
				
			for (Ability ability: shift.getMoves())
				ability.attacker = attacker;
			
			shift.addAttack(this);
		}
	}
	
	protected void execute() {
		boolean shifted = attacker.getStatus(Status.SHIFT) >= 0;

		List<Monster> tempList = Arrays.stream(formList) //determine available transformations, removes caster from list
			.filter(shift -> !attacker.getName().contains(shift.getName()))
			.collect(Collectors.toList()); //keep eye on from toList
		
		if (shifted)
			tempList.add(ShapeShift.getOriginal(attacker));

		int formChoice = -1;
		if (attacker.getClass() == Hero.class) {
			String[] formNames = tempList.stream().map(Monster::getName).toArray(String[]::new);
			String changePrompt = "Which form do you want to take?";
			formChoice = Interface.choiceInput(false, formNames, changePrompt)-1;

		} else
			formChoice = (int)(Math.random()*tempList.size());
		
			
		if (shifted && formChoice == tempList.size()-1) {
			ShapeShift.revert(attacker);
			Interface.writeOut(attacker.getName() + " reverted back");
		} else {	
			Monster newForm = tempList.get(formChoice);
			String beforeName = attacker.getName(), newName = newForm.getName();
			ShapeShift.transform(attacker, newForm, 5);			
			Interface.writeOut(beforeName + " has transformed into " + newName);
		}
	}
}
