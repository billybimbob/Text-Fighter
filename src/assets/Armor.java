package assets;

import java.util.List;
import assets.Equipment.Slot;

public class Armor extends Item {

    private boolean attType; //could make Item attr

    public Armor(String name, Slot slot, boolean attType, List<Stat> modStats, int modVal) {
        super();
        this.name = name;
        this.slot = slot;
        this.attType = attType;
		modStats.forEach(stat -> mods.add(new ModInfo(stat, modVal))); //might make modInfo param
    }

    protected void statMod(Stat stat, int mod) {
        int modVal = this.remove ? -mod : mod;
        if (currentUser.attType != this.attType)
            modVal *= 0.5;
		currentUser.modStatMax(stat, modVal);
    }

    public boolean getAttType() { return attType; }
}