package assets;

import java.util.*;
import combat.*;
import main.Index;
import main.Interface;
import main.Index.Move;

public class Monster implements Comparable<Monster> { //Temporary, probably make abstract later

	private static class StatusInfo {
		private int start, duration;

		StatusInfo () {
			this.start = -1;
			this.duration = -1;
		}

		int getStart() { return start; }
		int getDuration() { return duration; }

		void setStart(int start) { this.start = start; }
		void setDuration(int duration) { this.duration = duration; }
	}

	public final static int levMult = 2;
	
	private float turnDam;
	private Map<Stat, Float> stats;
	private Ability[] moveList;
	private Ability passive, turnMove;
	private Map<Status, StatusInfo> status;
	private boolean aggro;
	private List<Monster> targets;
	
	protected String name;
	protected int level = 1;
	protected boolean attType; //attType true means physical attack


	//constructor to have no ability
	public Monster (String name, boolean aggro, boolean attType, List<Integer> stats) {
		this.name = name;
		this.aggro = aggro;
		this.attType = attType;
		this.turnDam = 0;

		this.stats = new HashMap<>();
		//double [] statVals = {hp,hp, mp, mp, att, def, mag, magR, spe, crit}; //order must be same as statsName
		int idx = 0;
		for (Stat statName: Stat.values()) {
			setStat(statName, stats.get(idx));
			if (statName != Stat.HP && statName != Stat.MP)
				idx++;
		}

		this.targets = new ArrayList<>();
		initStatus();
	}

	//monster index constructor, basic attack and one special attack
	public Monster (String name, boolean aggro, boolean attType, List<Integer> stats, List<Move> specials) {
		this(name, aggro, attType, stats);
		List<Ability> moveSto = new ArrayList<>();
		moveSto.add(getAbility(Move.BASIC));
		for (Move special: specials)
			moveSto.add(getAbility(special));

		moveList = moveSto.toArray(new Ability[moveSto.size()]);
		
	}

	//copies to a new instance
	public Monster (Monster copy) { //not sure if deep or shallow
		this.name = copy.name;
		this.aggro = copy.aggro;
		this.attType = copy.attType;
		this.turnDam = copy.turnDam;

		this.stats = new HashMap<>(); //deep copy
		for (Stat statName: Stat.values())
			this.setStat(statName, copy.getStat(statName));
		
		this.targets = new ArrayList<>(copy.targets);
		initStatus();

		try {		
			if (copy.passive != null)
				this.setPassive(copy.passive.clone(this));

			this.moveList = new Ability[copy.moveList.length];
			for (int i = 0; i < moveList.length; i++)
				this.moveList[i] = copy.moveList[i].clone(this);

		} catch (CloneNotSupportedException e) {}
	}


	//private helpers
	private void initStatus() {
		status = new HashMap<>();
		for (Status statusName: Status.values())
			status.put(statusName, new StatusInfo());
	}

	private Ability getMove() {
		return moveList[(int)(Math.random()*moveList.length)];
	}
	private Ability getMove(int idx) {
		return this.moveList[idx];
	}

	private void updateTurnVals(Ability move) {
		if (turnMove == null) {
			turnMove = move;
		}
	}
	private void clearTargets() {
		this.targets.clear();
	}
	private void resetDamage() {
		this.turnDam = 0;
	}
	private StatusInfo getStatus (Status status) {
		return this.status.get(status);
	}

	//protected helpers
	protected Ability getAbility(Move name) {
		return Index.createAbility(name, this);
	}
	protected Ability getPassive(Move name) {
		return Index.createPassive(name, this);
	}


	/*
	 * public methods
	 */

	//accessors
	public String getName() {
		return name;
	}
	public float getStat (Stat stat) { //most likely where nulls arise
		return this.stats.get(stat);
	}
	public double getTurnDam() {
		return this.turnDam;
	}
	public boolean getAggro() {
		return aggro;
	}
	public boolean getPriority() {
		return turnMove==null ? false : turnMove.getPriority();
	}
	public boolean getAttType() {
		return this.attType;
	}

	public String[] getMoveNames() {
		String[] ret = new String[moveList.length];
		for (int i = 0; i < moveList.length; i++)
			ret[i] = moveList[i].getName() + " - " + (int)moveList[i].getCost() + " mana";
		return ret;
	}
	public int getNumTar() {
		return turnMove.getNumTar();
	}
	public Monster[] getTargets() {
		return targets.toArray(new Monster[targets.size()]);
	}

	public int minDam(Monster attacker, boolean attType) { //max value for now is 10 for def and magR
		Stat hitStat = getHitStat(attType), blockStat = getBlockStat(attType);
		int minDam = (int)attacker.getStat(hitStat);
		double stat = this.getStat(blockStat);
		minDam -= ((int)stat/2);
		return minDam > 0 ? minDam : 0;
	}

	//-1 not active, 0 finished, >0 amount of time active
	public int checkStatus(Status status, int turnNum) { //checks if status needs updating, keep eye on
		StatusInfo info = getStatus(status);
		int start = info.getStart(), duration = info.getDuration();

		int ret = -1;
		if (start > -1 && duration > -1) {
			int timeActive = turnNum-start;
			ret = timeActive >= duration ? 0 : timeActive;
		}
		return ret;
	}


	//mutators
	public void setTurn() {
		updateTurnVals(getMove());
	}
	public void setTurn(int idx) {
		updateTurnVals(getMove(idx));
	}
	public void clearTurn() {
		resetDamage();
		if (turnMove != null && turnMove.resolved()) {
			turnMove = null;		
			clearTargets();
		}
	}
	public void executeTurn() { //wrapper for turnMove
		turnMove.execute();
	}
	public void usePassive(Monster... targets) {
		if (passive != null) {
			List<Monster> sto = this.targets; //store previous targets
			this.targets = new ArrayList<>(Arrays.asList(targets));
			passive.execute();

			this.clearTargets();
			this.targets = sto;
		}
	}

	public void addTarget(Monster add) {
		this.targets.add(add);
	}
	public void addTarget(List<Monster> adds) {
		for(Monster add: adds)
			addTarget(add);
	}

	public void addAttack(Ability adding) { //not sure if better
		List<Ability> moveStore = new ArrayList<>();
		for (Ability move: moveList)
			moveStore.add(move);

		moveStore.add(adding);
		moveList = new Ability[moveStore.size()];
		moveList = moveStore.toArray(moveList);
	}

	public void setPassive(Ability passive) {
		if (passive.isPassive()) 
			this.passive = passive;
		else
			System.out.println("Not a valid ability");
	}


	public void setStat (Stat stat, float val) {
		stats.put(stat, val);
	}
	public void modStat (Stat stat, float val) { //changes stat by val; add max mod cases
		float newVal = stats.get(stat)+val;	
		setStat(stat, newVal);
	}

	@Deprecated
	public void setStatus(Status stat, int startTurn, int duration) {
		StatusInfo info = status.get(stat);
		if (startTurn < 0 || duration < 0) {
			info.setStart(-1);
			info.setDuration(-1);
		} else {
			info.setStart(startTurn);
			info.setDuration(duration);
		}
	}
	public void setStatus(Status stat, int duration) { //not sure
		StatusInfo info = status.get(stat);
		if (duration > 0) {
			int start = Interface.FIGHT.getTurnNum();
			info.setStart(start);
			info.setDuration(duration);
		} else {
			info.setStart(-1);
			info.setDuration(-1);
		}
	}
	public void setStatus(Status stat, boolean toggle) { //default to one turn
		StatusInfo info = status.get(stat);
		if (toggle) {
			info.setStart(0);
			info.setDuration(1);
		} else {
			info.setStart(-1);
			info.setDuration(-1);
		}
	}

	public void addDamTurn(double damage) {
		this.turnDam += damage;
	}

	//modifies stats based and restores health and mana
	public void levelUp () {
		level += 1;
		for (Stat stat: Stat.values()) {
			if (stat != Stat.HP && stat != Stat.MP)
				modStat(stat, level*levMult);
		}
		setStat(Stat.HP, getStat(Stat.MAXHP));
		setStat(Stat.MP, getStat(Stat.MAXMP));
	}

	@Override
	public String toString () {
		return name + " - " + getStat(Stat.HP) + " hp" + " - " + getStat(Stat.MP) + " mp" + " - " + getStat(Stat.SPEED) + " speed";
	}

	@Override
	public int compareTo (Monster other) { //based off of speed
		Float thisSpe = this.getStat(Stat.SPEED), otherSpe = other.getStat(Stat.SPEED);
		return thisSpe.compareTo(otherSpe);
	}


	//static methods
	public static Stat getHitStat(boolean attType) {
		return attType ? Stat.ATT : Stat.MAG;
	}
	public static Stat getBlockStat(boolean attType) {
		return attType ? Stat.DEF : Stat.MAGR;
	}

	
}
