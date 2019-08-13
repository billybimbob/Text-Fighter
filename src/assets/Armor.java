package assets;

import java.util.List;
import assets.Equipment.Slot;

public class Armor extends Item {

    public Armor(String name, Slot slot, List<Stat> modStats, int modVal) {
        super();
        this.name = name;
        this.slot = slot;
		modStats.forEach(stat -> mods.add(new ModInfo(stat, modVal))); //might make modInfo param
    }

    protected void statMod(Stat stat, int mod) {
        int modVal = remove ? -mod : mod;
		currentUser.modStatMax(stat, modVal); //want to mod max
    }
}