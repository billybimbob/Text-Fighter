package assets;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import combat.Status;

public class Equipment {


    private static class EquipInfo extends Monster.StatusInfo { //keeps track of overTime with start and duration
        private Map<Slot, Item> slots;
        private static final Map<Integer, Slot> SLOTNUM;
        static {
            Map<Integer, Slot> tempSlots = new HashMap<>();
            for (Slot slot: Slot.values())
                tempSlots.put(slot.ordinal(), slot);

            SLOTNUM = Collections.unmodifiableMap(tempSlots);
        }
        
        EquipInfo() {
            slots = new LinkedHashMap<>();
            
            for (Slot slot: Slot.values()) //order follows or of enums
                slots.put(slot, null);
        }

        private Item getItem(Slot slot) { return slots.get(slot); }
        
        private void addItem(Item item) {
            slots.put(item.getSlot(), item);
        }
        private Item removeItem(Slot slot) {
            return slots.remove(slot);
        }
    }

    private static EquipInfo getInfo(Monster mon) {
        return ((EquipInfo)(mon.status.get(Status.POTION)));
    }

    static void initEquip(Monster mon) {
        mon.status.put(Status.POTION, new EquipInfo());
    }

    static boolean switchCheck(Monster.StatusInfo info, boolean turnOn) { //only turn on if potion slot used
        Item potion = ((EquipInfo)info).getItem(Slot.POTION);
	    return turnOn ? potion != null : potion == null;
    }

    static String[] showEquipped(Monster mon) { //will be out of order
        return getInfo(mon).slots.entrySet().stream()
            .map(entry -> entry.getKey().getName() + ": " 
                + (entry.getValue() == null ? "None" : entry.getValue().getName())) //could use for loop
            .toArray(String[]::new);
    }

    static Slot numToSlot(int num) {
        return EquipInfo.SLOTNUM.get(num);
    }


    //public methods
    public static Item checkSlot(Monster user, Slot slot) {
        return getInfo(user).getItem(slot);
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
        Item item = info.removeItem(slot);
        if (item != null)
            item.use(user);
    }

    public static void overTime(Monster user) { //for now just Potion slot
        EquipInfo info = getInfo(user);
        for (Slot slot: new Slot[]{ Slot.POTION })
            info.getItem(slot).use(user, false);
    }
}