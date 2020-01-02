package assets;

/**
 * input for monster stats based on order here
 * <ul>
 * <li> hp: health of a monster </li>
 * <li> mp: mana of a monster, used for attacks </li>
 * <li> att: attack value, used to determine strength of physical attacks </li>
 * <li> def: defense value, used to determine damage resistance of physical attacks </li>
 * <li> mag: magic value, used to determine strength of magical attacks </li>
 * <li> magr: magic resistance, used to determine resistance of magical attacks </li>
 * <li> speed: used to determine turn order and chance to dogde any attack </li>
 * <li> crit: critical value, used to determine chance to land a critical hit </li>
 * </ul>
 */
public enum Stat {
    HP, MP, ATT, DEF, MAG, MAGR, SPEED, CRIT;

    
    //static methods
    public static Stat getHitStat(boolean attType) {
        return attType ? Stat.ATT : Stat.MAG;
    }
    public static Stat getBlockStat(boolean attType) {
        return attType ? Stat.DEF : Stat.MAGR;
    }
}