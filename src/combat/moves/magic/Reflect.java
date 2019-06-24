package combat.moves.magic;

import assets.Monster;
import assets.Monster.Stat;
import combat.*;
import main.Interface;

public class Reflect extends Ability { //not sure if should be priority or not

	public Reflect (Monster user) {
		super(user);
		name = "Reflect";
		description = "A spell that reflects some damage back at the attacker for 5 turns";
		attType = false;
		manaCost = 6;
		numTar = 0;
	}

	public void execute() {
		if (enoughMana()) { //Checks if sufficient mana
			attacker.modStat(Stat.MP, -manaCost);
			attacker.setStatus("reflect", Interface.FIGHT.getTurnNum(), 5);
			Interface.writeOut(attacker.getName() + " casts a reflecting shield for " + manaCost + " mana");
			
		} else
			Interface.writeOut(attacker.getName() + " tries to use " + name + ", but has insufficient mana");

	}
}
