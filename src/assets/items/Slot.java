package assets.items;

public enum Slot {
    HEAD("Head"), BODY("Body"), MARM("Main Arm"), AARM("Alt Arm"), POTION("Potion");

    private String name;

    Slot(String name) {
        this.name = name;
    }

    public String getName() { return this.name; }
}