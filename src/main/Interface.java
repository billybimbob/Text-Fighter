package main;

import java.util.*;
import assets.*;
import combat.*;

public class Interface {
	
	public static final ArrayList<String> responseOptions = new ArrayList<>(Arrays.asList("Yes", "No"));
	public static boolean heroAction = false;
	public static Hero hero;
	
	public static void main(String[] args) throws InterruptedException { //All this is probably temporary
		
		Scanner kboard = new Scanner(System.in);
		ArrayList<Monsters> fighters = new ArrayList<>();
		System.out.println("Welcome hero!");
		System.out.print("Step forth and state your name: ");
		String name = kboard.nextLine();
		
		Potions hpPotion = new Potions("hp"); //Temporary
		Potions mpPotion = new Potions("mp");
		Potions atPotion = new Potions("att");
		Potions dfPotion = new Potions("def");
		Potions crPotion = new Potions("crit");
		Potions evPotion = new Potions("eva");
		Potions spPotion = new Potions("spe");
		Inventory.addItems(mpPotion, 2);
		Inventory.addItems(hpPotion, 3);
		Inventory.addItems(atPotion, 1);
		Inventory.addItems(dfPotion, 1);
		Inventory.addItems(crPotion, 2);
		Inventory.addItems(evPotion, 1);
		Inventory.addItems(spPotion, 1);
		
		Hero player1 = new Hero(name);
		fighters.add(player1);
		Monsters mon1 = new Monsters("Thief", false, true, 15, 15, 3, 3, 3, 3, 6, 6, 6); //Temporary 
		fighters.add(mon1);
		Monsters mon2 = new Monsters("Spider", false, true, 15, 15, 6, 2, 3, 3, 4, 4, 4);  // lvl ,hp, mp,  atk, def, magic, mr, crit, eva, speed
		fighters.add(mon2);
		Monsters mon3 = new Monsters("Slime", false, true, 20, 10, 3, 5, 3, 3, 1, 1, 1);
		fighters.add(mon3);
		
		hero = player1; //Temporary
		for (int i = 0; i <= fighters.size()-1; i++) { //Temporary
			BasicAttack baseAtt = new BasicAttack(fighters.get(i));
			ChargeAttack charge = new ChargeAttack(fighters.get(i));
			MagicBlast blast = new MagicBlast(fighters.get(i));
			if (fighters.get(i).aggro) {
				Attacks[] moveSet = {baseAtt, charge, blast, null};
				hero.moveList = moveSet;
			} else {
				Attacks[] moveSet = {baseAtt, charge, blast};
				fighters.get(i).moveList = moveSet;
			}
		}
		fighters.remove(hero);
		SpinAttack spin = new SpinAttack(hero, fighters);
		fighters.add(hero);
		hero.moveList[3] = spin;
		
		for (int i = 0; i <= player1.moveList.length-1; i++)
			player1.moveListNames.add(player1.moveList[i].name + " - " + (int)player1.moveList[i].manaCost + " mana");

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