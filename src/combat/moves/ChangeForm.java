package combat.moves; //here to access Ability members and setAttacker

import java.util.*;
import java.util.stream.Collectors;

import assets.*;
import combat.Status;
import combat.moves.Ability;
import main.*;

public class ChangeForm extends Ability {

	private Monster[] formList;
	private Monster newForm;
	private int duration;
	
	public ChangeForm (Monster user) {
		super(user);
		name = "Change Form";
		description = "A spell to transform into an eagle, salamander, or pangolin";
		manaCost = 1; //might get rid
		numTar = 0;
		priority = true;
		duration = 5;
		
		this.initFormList(this.getAttacker());
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
				passive.setAttacker(attacker);
				
			for (Ability ability: shift.getMoves())
				ability.setAttacker(attacker);
			
			shift.addAttack(this);
		}
	}


	@Override
	protected void applyStatus(Status status, int duration, String statusPrompt) {
		if (status == Status.SHIFT)
			ShapeShift.transform(this.getAttacker(), newForm, duration);
		
		super.applyStatus(status, duration, statusPrompt); //second setStatus should fail, not sure
	}
	
	protected void execute() {
		Monster attacker = this.getAttacker();
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
			newForm = tempList.get(formChoice);
			String beforeName = attacker.getName();
			String newName = newForm.getName();
			String transPrompt = beforeName + " has transformed into " + newName;
			applyStatus(Status.SHIFT, this.duration, transPrompt);
		}
	}
}
