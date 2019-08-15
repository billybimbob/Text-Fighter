package main;

import java.util.*;

import assets.*;
import combat.Fight;

public class Interface {
	
	public static final String[] RESPONSEOPTIONS = {"Yes", "No"};
	public static final String LINESPACE = "-----------------------------------------------";
	
	private static final Scanner KEYBOARD = new Scanner(System.in);
	private static Hero hero;
	private static Fight fight;
	
	public static void main(String[] args) { //All this is probably temporary
		
		List<Monster> fighters = new ArrayList<>();
		Interface.writeOut("Welcome hero!");
		Interface.prompt("Step forth and state your name: ");
		String name = KEYBOARD.nextLine();
		
		String[] availClass = {"Warrior", "Mage", "Cleric", "Shifter"};
		String classPrompt = "Which class you would like to be?\nThis will affect your stats, potions, and abilities";
		int classChoice = choiceInput(false, availClass, classPrompt);
		
		
		hero = new Hero(name, classChoice);
		
		switch (classChoice) {
			case 1: //warrior
				hero.addItem(Index.getArmor("Broadsword"));
				hero.addItem(Index.getPotion("Ironskin Potion"));
				hero.addItem(Index.getPotion("Potion of Offense"));
				Interface.writeOut("You are a warrior");
				break;
			case 2: //mage
				hero.addItem(Index.getArmor("Staff"));
				hero.addItem(Index.getPotion("Potion of Elements"));
				hero.addItem(Index.getPotion("Swiftness Potion"));
				Interface.writeOut("You are a mage");
				break;
			case 3:
				hero.addItem(Index.getArmor("Tome"));
				hero.addItem(Index.getPotion("Element Barrier Potion"));
				hero.addItem(Index.getPotion("Ironskin Potion"));
				Interface.writeOut("You are a cleric");
				break;
			case 4: //shifter
				hero.addItem(Index.getArmor("Dagger"));
				hero.addItem(Index.getPotion("Ironskin Potion"));
				hero.addItem(Index.getPotion("Element Barrier Potion"));
				Interface.writeOut("You are a shifter");
				break;
		}
		
		hero.addItems(Index.getPotion("Health Potion"), 3);
		hero.addItems(Index.getPotion("Mana Potion"), 3);
		hero.addItems(Index.getPotion("Swiftness Potion"), 1);
		hero.addItems(Index.getPotion("Lucky Potion"), 1);
		
		fighters.add(hero);
		
		for (int i = 0; i < 3; i++)
			fighters.add(Index.randomMonster());

		fight = new Fight(fighters);
		Interface.writeOut("Press enter when you are ready to fight");
		Interface.confirm();
		fight.run();
		fight = null;
		
		KEYBOARD.close();
	}

	public static Fight currentFight() { return fight; }
	public static Hero getHero() { return hero; }

	
	public static void writeOut(String... printings) { //wrapper for system.out.print; can be other output
		StringBuilder combined = new StringBuilder();
		for (String printing: printings)
			combined.append(printing);
		System.out.println(combined.toString());
	}
	public static void prompt(String prompt) { //print to same line
		System.out.print(prompt);
	}
	public static void confirm() {
		KEYBOARD.nextLine();
	}
	
	/**
	 * Takes in user input from multiple options, with input in the form of {@code int}
	 * @param back if the value of {@code 0} should be an option
	 * @param options all the possible options to be selected
	 * @param prompt question shown before the options
	 * @return the option that was selected, {@code 0} represents back option
	 */
	public static int choiceInput (boolean back, Object[] options, String prompt) { //Returns user input from choices
		StringBuilder lstOptions = new StringBuilder(LINESPACE + "\n");

		if (back) //option for "back"
			lstOptions.append("0. Back\n");
		for (int i = 0; i < options.length; i++)
			lstOptions.append(i+1 + ". " + options[i].toString() + "\n");

		lstOptions.append(LINESPACE + "\n");
		Interface.writeOut(lstOptions.toString());

		int choice = -1;
		boolean heroAction = false;
		do {
			try {
				Interface.prompt(prompt + "\n\nSelect which number you want: ");
				choice = Integer.parseInt(KEYBOARD.nextLine());
			} catch (NumberFormatException e) {} //might want to restructure somehow, right now, just preventing from crashing
			
			if (choice <= options.length && choice > 0 || back && choice == 0) //Checks if input is valid
				heroAction = true;
			else
				Interface.writeOut("\nInvalid choice, please try again\n");
		} while (!heroAction);
		
		Interface.writeOut();
		return choice;
	}

}