package main;

import java.util.*;
import assets.*;

public class Interface {
	
	public static final String[] responseOptions = {"Yes", "No"};
	public static boolean heroAction = false;
	public static Hero hero;
	
	public static void main(String[] args) throws InterruptedException { //All this is probably temporary
		Scanner kboard = new Scanner(System.in);
		Index stuff = new Index(kboard);
		stuff.use(); //dummy method to get rid of yellow line
		
		ArrayList<Monsters> fighters = new ArrayList<>();
		System.out.println("Welcome hero!");
		System.out.print("Step forth and state your name: ");
		String name = kboard.nextLine();
		
		String[] availClass = {"Warrior", "Mage", "Shifter"};
		String classPrompt = "Which class you would like to be?\nThis will affect your stats, potions, and abilities";
		int classChoice = choiceInput(kboard, false,availClass, classPrompt);
		//int classChoice = 1;
		Hero player1 = new Hero(name, classChoice);
		
		switch (classChoice) {
		case 1: //warrior
			Inventory.addItems(Index.potionsList[2], 1);
			Inventory.addItems(Index.potionsList[3], 1);
			System.out.println("You are a warrior");
			break;
		case 2: //mage
			Inventory.addItems(Index.potionsList[4], 1);
			Inventory.addItems(Index.potionsList[5], 1);
			System.out.println("You are a mage");
			break;
		case 3: //shifter
			Inventory.addItems(Index.potionsList[4], 1);
			Inventory.addItems(Index.potionsList[5], 1);
			System.out.println("You are a shifter");
			break;
		}
		
		Inventory.addItems(Index.potionsList[0], 3);
		Inventory.addItems(Index.potionsList[1], 3);
		Inventory.addItems(Index.potionsList[6], 1);
		Inventory.addItems(Index.potionsList[7], 1);
		
		fighters.add(player1);
		hero = player1; //Temporary
		
		player1.moveListNames = new String[player1.moveList.length];
		for (int i = 0; i <= player1.moveList.length-1; i++)
			player1.moveListNames[i] = player1.moveList[i].getName() + " - " + (int)player1.moveList[i].getCost() + " mana";

		for (int i = 0; i <= 2; i++) {
			fighters.add(new Monsters(Index.monsterList[i]));
		}
		Fight.fighting(kboard, fighters);
		
		kboard.close();
	}
	
	public static int choiceInput (Scanner keyboard, boolean back, String[] options, String prompt) { //Returns user input from choices
		int choice = 0;
		System.out.println("-----------------------------------------------");
		if (back) //option for "back"
			System.out.println("0. Back");
		for (int i = 0; i <= options.length-1; i++) { //Print out choices from an array
			System.out.println(i+1 + ". " + options[i]);
		}
		System.out.println("-----------------------------------------------");
		do {
			try {
				System.out.print(prompt + "\n\nSelect which number you want: ");
				choice = Integer.parseInt(keyboard.nextLine());
			} catch (Exception e) {} //might want to restructure somehow, right now, just preventing from crashing
			
			if ((choice <= options.length && choice > 0) || (back && choice == 0)) //Checks if input is valid
				heroAction = true;
			else
				System.out.println("\nInvalid choice, please try again\n");
		} while (!heroAction);
		
		heroAction = false;
		System.out.println("");
		
		return choice;
	}
	
}