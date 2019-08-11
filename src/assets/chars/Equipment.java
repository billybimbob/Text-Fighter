package assets.chars;

import java.util.HashMap;
import java.util.Map;

import assets.Item;
import combat.Status;

public class Equipment {

    public enum Slot { HEAD, BODY, LARM, RARM, POTION }

    private static class EquipInfo extends Monster.StatusInfo { //keeps track of overTime with start and duration
        private Map<Slot, Item> slots;
        
        EquipInfo() {
            super();
            slots = new HashMap<>();
            /*
            for (Slot slot: Slot.values())
                slots.put(slot, null);*/
        }

        private Item getItem(Slot slot) { return slots.get(slot); }
        
        private void addItem(Item item) {
            slots.put(item.getSlot(), item);
        }
        private void removeItem(Slot slot) {
            slots.remove(slot);
        }
    }

    private static EquipInfo getInfo(Monster mon) {
        return ((EquipInfo)(mon.status.get(Status.POTION)));
    }

    static boolean switchCheck(Monster.StatusInfo info, boolean turnOn) { //only turn on if potion slot used
        Item potion = ((EquipInfo)info).getItem(Slot.POTION);
	    return turnOn ? potion != null : potion == null;
    }

    static void initEquip(Monster mon) {
        mon.status.put(Status.POTION, new EquipInfo());
    }

    public static void equip(Monster user, Item item) { //check if item already there
        EquipInfo info = getInfo(user);
        Slot slot = item.getSlot();
        
        if (info.getItem(slot) != null) //same potions treated as new ones
            unequip(user, slot); //not sure

        info.addItem(item);
        item.use(user);
    }

    public static void unequip(Monster user, Slot slot) {
        EquipInfo info = getInfo(user);
        Item item = info.getItem(slot);
        info.removeItem(slot);
        item.use(user);
    }

    public static void overTime(Monster user) { //for now just Potion slot
        EquipInfo info = getInfo(user);
        for (Slot slot: new Slot[]{ Slot.POTION })
            info.getItem(slot).use(user, false);
    }
}