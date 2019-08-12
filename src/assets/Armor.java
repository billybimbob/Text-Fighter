package assets;


public class Armor extends Item {

    protected void statMod(Stat stat, int mod) {
        int modVal = remove ? -mod : mod;
		currentUser.modStatMax(stat, modVal); //want to mod max
    }
}