package assets;

//input for monster based on order here
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