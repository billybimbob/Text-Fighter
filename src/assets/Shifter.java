package assets;

import assets.Monster;

public class Shifter extends Monster {

    private Monster original;

    public Shifter(Monster shifting, Monster original) {
        super(shifting);
        this.original = original;
    }

    public Monster getOriginal() {
        return original;
    }

}

