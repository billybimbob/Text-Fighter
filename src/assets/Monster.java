package assets;

import java.util.*;
import combat.*;
import main.Index;
import main.Interface;
import main.Index.Move;

public class Monster implements Comparable<Monster> { //Temporary, probably make abstract later

	private static class StatInfo {
		private float base, temp;

		StatInfo(float start) {
			this.base = start;
			this.temp = start;
		}

		float getBase() { return base; } //max val
		float getTemp() { return temp; } //current val

		float setTemp(float newVal) { //floor to 0; celings to base; returns amount over basecap
			float capOver = 0;
			
			if (this.base < newVal) {
				capOver = newVal-this.base;
				this.temp = this.base;
			} else
				this.temp = newVal;

			return capOver;
		}
		void setBase(float base) { 
			this.base = base<0 ? 0 : base;
			this.temp = temp>base ? base : temp; //if new base is less than temp
		}
	}
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
	private Map<Stat, StatInfo> stats;
	private Ability[] moveList;
	private Ability passive, turnMove;
	private Map<Status, StatusInfo> status;
	private boolean aggro;
	private List<Monster> targets; //look at how set and used
	
	protected String name;
	protected int level = 1;
	protected boolean attType; //attType true means physical attack


	/**
	 * constructor to have no extra attacks but basic
	 * @param attType true for melee, false for magic
	 * @param stats order should follow Stat.java and not include MAXHP or MAXMP
	 * @see Stat
	 */
	public Monster (String name, boolean aggro, boolean attType, List<Integer> statsIn) {
		this.name = name;
		this.aggro = aggro;
		this.attType = attType;
		this.turnDam = 0;

		this.stats = new HashMap<>();
		//hp, mp, att, def, mag, magR, spe, crit}; //order must be same as enum
		for (int i = 0; i < statsIn.size(); i++)
			this.stats.put(Stat.values()[i], new StatInfo(statsIn.get(i)));

		this.targets = new ArrayList<>();
		initStatus();
		moveList = new Ability[] {createAbility(Move.BASIC)};
	}

	/**
	 * monster constructor, basic attack and list of specials
	 * @see {@link} {@link Monster#Monster(String, boolean, boolean, List)}
	 */
	public Monster (String name, boolean aggro, boolean attType, List<Integer> stats, List<Move> specials) {
		this(name, aggro, attType, stats);
		
		List<Ability> moveSto = new ArrayList<>();
		for (Ability prevMove: moveList) //abilities from default constructor
			moveSto.add(prevMove);
		for (Move special: specials)
			moveSto.add(createAbility(special));

		moveList = moveSto.toArray(new Ability[moveSto.size()]);
	}

	/**
	 * contructor with specials and a passive
	 * @param passive must be a passive ability
	 */
	public Monster (String name, boolean aggro, boolean attType, List<Integer> stats, List<Move> specials, Move passive) {
		this(name, aggro, attType, stats, specials);
		this.setPassive(createPassive(passive));
	}

	/**
	 * copy constructor
	 * @param copy creates new Monster instance based off of values from copy
	 */
	public Monster (Monster copy) { //not sure if deep or shallow
		this.name = copy.name;
		this.aggro = copy.aggro;
		this.attType = copy.attType;
		this.turnDam = copy.turnDam;

		this.stats = new HashMap<>(); //deep copy
		for (Stat statName: Stat.values())
			this.setStat(statName, copy.getStat(statName)); //same ref; need to change
		
		this.targets = new ArrayList<>(copy.targets);
		initStatus();

		try {		
			if (copy.passive != null)
				this.setPassive((Ability)copy.passive.clone(this));

			this.moveList = new Ability[copy.moveList.length];
			for (int i = 0; i < moveList.length; i++)
				this.moveList[i] = (Ability)copy.moveList[i].clone(this);

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

	/**
	 * wrapper for list clear method
	 */
	private void clearTargets() {
		this.targets.clear();
	}
	private void resetDamage() {
		this.turnDam = 0;
	}
	private Ability createAbility(Move name) {
		return Index.createAbility(name, this);
	}
	private Ability createPassive(Move name) {
		return Index.createPassive(name, this);
	}

	//protected helpers
	protected int currentTurn() {
		return Interface.FIGHT.getTurnNum();
	}


	/*
	 * public methods
	 */

	//accessors
	public String getName() {
		return name;
	}
	public double getTurnDam() {
		return this.turnDam;
	}
	public boolean getAggro() {
		return aggro;
	}
	public boolean getPriority() {
		return turnMove == null ? false : turnMove.getPriority();
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

	/**
	 * @return -1 is no limit, 0 is self, >0 max number of targets
	 */
	public int getNumTar() {
		return turnMove.getNumTar();
	}

	public Monster[] getTargets() {
		return targets.toArray(new Monster[targets.size()]);
	}

	public float getStatRatio(Stat stat) {
		return this.getStat(stat)/this.getStatMax(stat);
	}
	
	public float getStat (Stat stat) {
		return this.stats.get(stat).getTemp();
	}
	public float getStatMax (Stat stat) {
		return this.stats.get(stat).getBase();
	}

	/**
	 * @return -1 not active, 0 finished, >0 amount of time remaining; toggle always returns 0
	 */
	public int getStatus(Status status) { //checks if status needs updating, keep eye on
		StatusInfo info = this.status.get(status);
		int start = info.getStart(), duration = info.getDuration();
		int turnNum = currentTurn();

		int ret = -1;
		if (start > -1 && duration > -1) {
			int timeActive = turnNum-start;
			ret = timeActive >= duration ? 0 : duration-timeActive;
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
	public void usePassive(List<Monster> possTargets) { //look at; assume all fighters passed in
		if (passive != null) {
			List<Monster> sto = this.targets; //store previous targets

			this.targets = new ArrayList<>(possTargets);
			passive.execute(); //execute will handle targeting

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

	@Deprecated
	public void setPassive(Ability passive) {
		if (passive.isPassive()) 
			this.passive = passive;
		else
			System.out.println("Not a valid ability");
	}

	/**
	 * modifies max stat value to newVal
	 */
	public void setStatMax (Stat stat, float newVal) {
		stats.get(stat).setBase(newVal);
	}

	/**
	 * modifies current stat value to newVal, caps at base
	 */
	public void setStat (Stat stat, float newVal) {
		stats.get(stat).setTemp(newVal);
	}

	/**
	 * changes the stat by mod, caps new value to base
	 * @return the amount that went over the base cap
	 */
	public float modStat (Stat stat, float mod) { //changes stat by val
		StatInfo info = stats.get(stat);
		return info.setTemp(info.getTemp()+mod);
	}

	/**
	 * turns on/off the inputted status
	 * @param duration positive value turns on for that duration, negative turns off
	 */
	public void setStatus(Status status, int duration) { //not sure
		StatusInfo info = this.status.get(status);
		if (duration > 0) {
			int start = currentTurn();
			info.setStart(start);
			info.setDuration(duration);
		} else {
			info.setStart(-1);
			info.setDuration(-1);
		}
	}

	/**
	 * turns on/off the inputted status; defaults to one turn
	 */
	public void setStatus(Status status, boolean toggle) {
		StatusInfo info = this.status.get(status);
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

	/**
	 * modifies stats based and restores health and mana
	 */
	public void levelUp () { //clears current temp right now
		level += 1;
		StatInfo info;
		for (Stat stat: Stat.values()) {
			info = stats.get(stat);
			info.setBase(info.getBase() + levMult);
			info.setTemp(info.getBase());
		}
	}

	@Override
	public String toString () {
		return name + " - " + getStat(Stat.HP) + " hp" + " - " + getStat(Stat.MP) + " mp" + " - " + getStat(Stat.SPEED) + " speed";
	}

	/**
	 * based off of name, alphbetically
	 */
	@Override
	public int compareTo (Monster other) {
		String thisName = this.name, otherName = other.name;
		return thisName.compareTo(otherName);
	}


	//static methods
	public static Stat getHitStat(boolean attType) {
		return attType ? Stat.ATT : Stat.MAG;
	}
	public static Stat getBlockStat(boolean attType) {
		return attType ? Stat.DEF : Stat.MAGR;
	}

	
}
