package assets;

import java.util.LinkedHashMap;
import java.util.Map;

import combat.Status;

public class Equipment {

    private static class EquipInfo extends Monster.StatusInfo { //keeps track of overTime with start and duration
        private Map<Slot, Item> slots;
        
        private EquipInfo() {
            slots = new LinkedHashMap<>();
            
            for (Slot slot: Slot.values()) //order follows or of enums
                slots.put(slot, null);
        }

        private Item getItem(Slot slot) { return slots.get(slot); }
        
        private void addItem(Item item) {
            slots.put(item.getSlot(), item);
        }
        private Item removeItem(Slot slot) {
            Item item = slots.get(slot);
            slots.put(slot, null);
            return item;
        }
    }

    private static EquipInfo stoInfo;


    private static EquipInfo getInfo(Monster mon, boolean store) {
        EquipInfo info;

        if(!store && stoInfo != null) { //could add counter param and info
            info = stoInfo;
            stoInfo = null;
            return info;

        } else {
            info = ((EquipInfo)(mon.status.get(Status.POTION)));
            if (store) //will null on second call with store as false; keep eye on
                stoInfo = info;
            
            return info;
        }
    }
    private static EquipInfo getInfo(Monster mon) {
        return getInfo(mon, false);
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
        return Slot.values()[num];
    }


    //public methods
    public static Item checkSlot(Monster user, Slot slot) {
        return getInfo(user).getItem(slot);
    }

    /**
     * @return item previously in the slot, or {@code null}
     */
    public static Item equip(Monster user, Item item) { //check if item already there
        EquipInfo info = getInfo(user, true);
        Slot slot = item.getSlot();
        
        Item oldItem = unequip(user, slot); //clear store info here

        info.addItem(item);
        item.use(user);
        return oldItem;
    }

    /**
     * @return the item at {@code slot}, or {@code null} if
     * no item was in the slot
     */
    public static Item unequip(Monster user, Slot slot) {
        EquipInfo info = getInfo(user); //called twice
        Item removed = info.removeItem(slot);
        
        if (removed != null)
            removed.use(user);
        return removed;
    }

    public static void overTime(Monster user) { //for now just Potion slot
        EquipInfo info = getInfo(user);
        for (Slot slot: new Slot[]{ Slot.POTION })
            info.getItem(slot).use(user, false);
    }
}