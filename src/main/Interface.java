package main;

import java.util.*;
import assets.*;

public class Interface {
	
	public static final ArrayList<String> responseOptions = new ArrayList<>(Arrays.asList("Yes", "No"));
	public static boolean heroAction = false;
	public static Hero hero;
	
	public static void main(String[] args) throws InterruptedException { //All this is probably temporary
		
		Index stuff = new Index();
		Scanner kboard = new Scanner(System.in);
		ArrayList<Monsters> fighters = new ArrayList<>();
		System.out.println("Welcome hero!");
		System.out.print("Step forth and state your name: ");
		String name = kboard.nextLine();
		
		Inventory.addItems(Index.potionsList[0], 2);
		Inventory.addItems(Index.potionsList[1], 3);
		Inventory.addItems(Index.potionsList[2], 1);
		Inventory.addItems(Index.potionsList[3], 1);
		Inventory.addItems(Index.potionsList[4], 2);
		Inventory.addItems(Index.potionsList[5], 1);
		Inventory.addItems(Index.potionsList[6], 1);
		
		Hero player1 = new Hero(name, true);
		fighters.add(player1);
		
		hero = player1; //Temporary
		
		for (int i = 0; i <= player1.moveList.length-1; i++)
			player1.moveListNames.add(player1.moveList[i].name + " - " + (int)player1.moveList[i].manaCost + " mana");

		for (int i = 0; i <= Index.monsterList.length-1; i++) {
			fighters.add(new Monsters(Index.monsterList[i]));
		}
		fighters.add(new Monsters(Index.monsterList[2]));
		fighters.get(3).hp+= 5;
		Fight.fighting(kboard, fighters);
		kboard.close();
	}
	
	public static int choiceInput (Scanner keyboard, boolean back, ArrayList<String> list, String prompt) { //Returns user input from choices
		int choice = 0;
		System.out.println("-----------------------------------------------");
		if (back) //option for "back"
			System.out.println("0. Back");
		for (int i = 0; i <= list.size()-1; i++) { //Print out choices from an array
			System.out.print(i+1 + ". " + list.get(i) + "\n");
		}
		do {
			try {
				System.out.print("\n" + prompt + "\nSelect which number you want: ");
				choice = Integer.parseInt(keyboard.nextLine());
			} catch (Exception e) {} //might want to restructure somehow, right now, just preventing from crashing
			
			if (choice <= list.size() && choice > 0) //Checks if input is valid
				heroAction = true;
			else if (back && choice == 0)
				heroAction = true;
			else
				System.out.println("\nInvalid choice, please try again");
		} while (!heroAction);
		heroAction = false;
		System.out.println("");
		
		return choice;
	}
	
}