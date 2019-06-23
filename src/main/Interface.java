package main;

import java.util.*;
import assets.*;

public class Interface {
	
	public static Hero HERO;
	
	public static final String[] RESPONSEOPTIONS = {"Yes", "No"};
	public static final String LINESPACE = "-----------------------------------------------";
	private static final Scanner KEYBOARD = new Scanner(System.in);
	
	
	public static void main(String[] args) throws InterruptedException { //All this is probably temporary
		Index.createVals();
		
		ArrayList<Monster> fighters = new ArrayList<>();
		Interface.writeOut("Welcome hero!");
		Interface.prompt("Step forth and state your name: ");
		String name = KEYBOARD.nextLine();
		
		String[] availClass = {"Warrior", "Mage", "Shifter"};
		String classPrompt = "Which class you would like to be?\nThis will affect your stats, potions, and abilities";
		int classChoice = choiceInput(false, availClass, classPrompt);
		//int classChoice = 1;
		
		HERO = new Hero(name, classChoice);
		
		switch (classChoice) {
		case 1: //warrior
			HERO.addItems(Index.potionsList[2], 1);
			HERO.addItems(Index.potionsList[3], 1);
			Interface.writeOut("You are a warrior");
			break;
		case 2: //mage
			HERO.addItems(Index.potionsList[4], 1);
			HERO.addItems(Index.potionsList[5], 1);
			Interface.writeOut("You are a mage");
			break;
		case 3: //shifter
			HERO.addItems(Index.potionsList[4], 1);
			HERO.addItems(Index.potionsList[5], 1);
			Interface.writeOut("You are a shifter");
			break;
		}
		
		HERO.addItems(Index.potionsList[0], 3);
		HERO.addItems(Index.potionsList[1], 3);
		HERO.addItems(Index.potionsList[6], 1);
		HERO.addItems(Index.potionsList[7], 1);
		
		fighters.add(HERO);
		
		for (int i = 0; i <= 2; i++) {
			fighters.add(new Monster(Index.monsterList[i]));
		}

		Fight.fighting(fighters);
		
		KEYBOARD.close();
	}


	public static void writeOut(String... printings) { //wrapper for system.out.print; can be other output
		for (String printing: printings)
			System.out.println(printing);
	}
	public static void prompt(String prompt) { //print to same line
		System.out.print(prompt);
	}
	
	public static int choiceInput (boolean back, String[] options, String prompt) { //Returns user input from choices
		int choice = -1;
		StringBuilder lstOptions = new StringBuilder();

		lstOptions.append(LINESPACE);
		if (back) //option for "back"
			lstOptions.append("0. Back");
		for (int i = 0; i < options.length; i++)
			lstOptions.append(i+1 + ". " + options[i]);
		lstOptions.append(LINESPACE);
		Interface.writeOut(lstOptions.toString());

		boolean heroAction = false;
		do {
			try {
				Interface.prompt(prompt + "\n\nSelect which number you want: ");
				choice = Integer.parseInt(KEYBOARD.nextLine());
			} catch (NumberFormatException e) {} //might want to restructure somehow, right now, just preventing from crashing
			
			if ((choice < options.length && choice > 0) || (back && choice == 0)) //Checks if input is valid
				heroAction = true;
			else
				Interface.writeOut("\nInvalid choice, please try again\n");
		} while (!heroAction);
		
		heroAction = false;
		Interface.writeOut();
		
		return choice;
	}

	public static void confirm() {
		KEYBOARD.nextLine();
	}
}