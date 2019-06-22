package assets;

public abstract class Items {
	public String name, statMod, tier;
	public int numAmount, modVal, baseModVal;
	
	public void qualityTier (String tier) {
		this.tier = tier;
		switch (tier) {
			case "basic":
				modVal += 5;
				break;
			case "greater":
				modVal += 10;
				break;
			case "ultimate":
				modVal += 15;
				break;
		}
	}
	public abstract void useItem (Monster user);

}
