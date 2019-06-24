package assets;

import assets.Monster;

public class Shifter extends Monster {

    private Monster original;

    public Shifter(Monster shifting, Monster original) {
        super(shifting);
        this.original = original;
    }
    public Shifter(Shifter shifting) {
        this(shifting, shifting.getOriginal()); //keep original
    }

    public Monster getOriginal() {
        return original;
    }

}

