package assets;

import java.util.List;
import main.Interface;

public class Armor extends Item {

    private boolean attType; //could make Item attr

    public Armor(String name, Slot slot, boolean attType, int space, List<Stat> modStats, int modVal) {
        super();
        this.name = name;
        this.slot = slot;
        this.attType = attType;
        this.space = space;
		modStats.forEach(stat -> mods.add(new ModInfo(stat, modVal))); //might make modInfo param
    }

    @Override
    protected void use(Monster user, boolean remove) {
        if (remove)
            Interface.writeOut(user.getName() + " has removed " + this.name);
        else
            Interface.writeOut(user.getName() + " equips " + this.name
                + " and gains a boost in " + this.getModNames());

        super.use(user, remove);
    }

    protected void statMod(Stat stat, int mod) {
        int modVal = this.remove ? -mod : mod;
        if (currentUser.attType != this.attType)
            modVal *= 0.5;
		currentUser.modStatMax(stat, modVal);
    }

    public boolean getAttType() { return attType; }
}