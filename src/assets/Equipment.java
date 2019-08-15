package assets;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import combat.Status;

public class Equipment {


    private static class EquipInfo extends Monster.StatusInfo { //keeps track of overTime with start and duration
        private Map<Slot, Item> slots;
        private static Map<Integer, Slot> slotNum;
        
        EquipInfo() {
            slots = new LinkedHashMap<>();
            
            boolean notCreated = slotNum == null; //not sure
            if (notCreated)
                slotNum = new HashMap<>();
            
            for (int i = 0; i < Slot.values().length; i++) { //order will always be same
                Slot slot = Slot.values()[i];
                slots.put(slot, null);
                if (notCreated)
                    slotNum.put(i, slot);
            }

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

    static boolean switchCheck(Monster.StatusInfo info, boolean turnOn) { //only turn on if potion slot used
        Item potion = ((EquipInfo)info).getItem(Slot.POTION);
	    return turnOn ? potion != null : potion == null;
    }

    static void initEquip(Monster mon) {
        mon.status.put(Status.POTION, new EquipInfo());
    }

    static String[] showEquipped(Monster mon) { //will be out of order
        return getInfo(mon).slots.entrySet().stream()
            .map(entry -> entry.getKey().getName() + ": " 
                + (entry.getValue() == null ? "None" : entry.getValue().getName())) //could use for loop
            .toArray(String[]::new);
    }

    static Slot numToSlot(int num) {
        return EquipInfo.slotNum.get(num);
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