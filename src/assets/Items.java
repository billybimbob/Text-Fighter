package assets;

import assets.chars.Monster;

public abstract class Items {

	protected Stat statMod;
	public String name, tier;
	public int numAmount, modVal, baseModVal;
	
	protected void qualityTier (String tier) {
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
