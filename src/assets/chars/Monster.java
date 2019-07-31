package assets.chars;

import java.util.*;

import assets.Stat;
import combat.Status;
import combat.moves.Ability;
import main.Index;
import main.Index.Move;
import main.Interface;

public class Monster implements Comparable<Monster>, Cloneable {

	/** nested classes */

	private static class StatInfo {
		float base, temp;

		StatInfo(float start) {
			this.base = start;
			this.temp = start;
		}

		float getBase() { return base; } //max val
		float getTemp() { return temp; } //current val


		void setBase(float base) { 
			this.base = base<0 ? 0 : base;
			this.temp = temp>base ? base : temp; //if new base is less than temp
		}
		void setTemp(float newVal) {
			this.temp = newVal<0 ? 0 : newVal;
		}
		float setTempCapped(float newVal) { //floor to 0; celings to base; returns amount over basecap
			float capOver = 0;
			
			if (newVal > this.base) {
				capOver = newVal-this.base;
				if (this.temp < this.base) //keep old temp or ceiling to base
					this.setTemp(this.base);

			} else
				this.setTemp(newVal);

			return capOver;
		}
	}

	static class StatusInfo {
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


	/**variables */

	public final static int levMult = 2;
	
	private static int idCount = 0;
	private int id;
	
	protected String name;
	protected boolean attType; //attType true means physical attack
	protected boolean aggro;
	protected Map<Stat, StatInfo> stats;
	protected Ability[] moveList;
	protected Ability passive;
	protected Map<Status, StatusInfo> status;
	protected Ability turnMove;
	protected List<Monster> targets; //look at how set and used
	protected int level = 1;


	/**
	 * constructor to have no extra attacks but basic
	 * @param aggro {@code true} for hero aggresivnes, {@code false} for aggressive against hero
	 * @param attType {@code true} for melee, {@code false} for magic
	 * @param statsIn order should follow order of {@link Stat}
	 */
	public Monster (String name, boolean aggro, boolean attType, List<Integer> statsIn) {
		this.setId();
		this.name = name;
		this.aggro = aggro;
		this.attType = attType;

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
	 * @see {@link Monster#Monster(String, boolean, boolean, List)}
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
		this.setId();
		this.name = copy.name;
		this.aggro = copy.aggro;
		this.attType = copy.attType;

		this.stats = new HashMap<>(); //deep copy
		for (Stat statName: Stat.values())
			this.stats.put(statName, new StatInfo(copy.getStatMax(statName)));
		
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
	private void setId() { this.id = idCount++; }

	private void setPassive(Ability passive) {
		if (passive.isPassive()) 
			this.passive = passive;
	}
	private void initStatus() {
		status = new HashMap<>();
		for (Status statusName: Status.values())
			if (statusName.equals(Status.SHIFT))
				status.put(statusName, new ShapeShift.ShiftInfo());
			else
				status.put(statusName, new StatusInfo());
	}


	private void updateTurnVals(Ability move) {
		if (turnMove == null) {
			turnMove = move;
		}
	}

	/**wrapper for list clear method*/
	private Ability createAbility(Move name) {
		return Index.createAbility(name, this);
	}
	private Ability createPassive(Move name) {
		return Index.createPassive(name, this);
	}
	
	/** extra values to check/modify while turning on status;
	 *  status will only update if new values will extend duration */
	private void onChecks(Status status, int start, int duration) {
		StatusInfo info = this.status.get(status);
		
		int oldStart = info.getStart(), oldDur = info.getDuration();
		boolean flag = start+duration > oldStart+oldDur; //only update if will extend duration
		switch(status) {
			case CONTROL:
				this.setAggro();
				break;
			case DODGE:
				if (oldStart == -1 && oldDur == -1)
					this.modStat(Stat.SPEED, false, this.getStatMax(Stat.SPEED)); //doubles speed
				break;
			case SHIFT:
				flag = flag && ((ShapeShift.ShiftInfo)info).getOriginal() != null;
				break;
			default:
				break;
		}

		if (flag) {
			info.setStart(start);
			info.setDuration(duration);
		}
	}

	/** extra vales to check/modify while turning off a status */
	private void offChecks(Status status) {
		StatusInfo info = this.status.get(status);

		switch(status) {
			case CONTROL:
				this.setAggro();
				break;
			case DODGE:
				if (info.getStart() >= 0 || info.getDuration() >= 0)
					this.modStat(Stat.SPEED, false, -this.getStatMax(Stat.SPEED));
				break;
			case SHIFT:
				ShapeShift.offCheck(this);
				break;
			default:
				break;
		}
		
		info.setStart(-1);
		info.setDuration(-1);
	}

	private void setTargets(List<Monster> possTargets) {
		if (!checkAutoTar(possTargets))
			for (int i = 0; i < possTargets.size() 
				&& this.targets.size() < this.getNumTar(); i++) { //gets targets if needed
				
				Monster possTarget = possTargets.get(i);
				if (possTarget.getAggro() != this.getAggro())
					this.addTarget(possTarget);
			}

	}
	private boolean checkAddAll(List<Monster> possTargets) {
		int numTar = this.getNumTar();

		boolean check = numTar == -1 || numTar >= possTargets.size();
		if (check)
			this.addTargets(possTargets);

		return check;
	}
	private boolean checkSelfTar() {
		boolean check = this.getNumTar() == 0;
		if (check)
			this.addTarget(this);
		
		return check;
	}

	//protected helpers
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	protected boolean checkAutoTar(List<Monster> possTargets) {
		return checkAddAll(possTargets) || checkSelfTar();
	}
	protected void clearTargets() {
		this.targets.clear();
	}
	protected Ability getMove() {
		return moveList[(int)(Math.random()*moveList.length)];
	}
	protected Ability getMove(int idx) {
		return this.moveList[idx];
	}

	protected int currentTurn() {
		return Interface.FIGHT.getTurnNum();
	}
	
	protected void setMove() {
		updateTurnVals(getMove());
	}
	protected void setMove(int idx) {
		updateTurnVals(getMove(idx));
	}
	
	protected void addTarget(Monster add) {
		this.targets.add(add);
	}
	protected void addTargets(List<Monster> adds) {
		this.targets.addAll(adds);
	}


	/*
	 * public methods
	 */

	//accessors
	public String getName() {
		return name;
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
	public Ability[] getMoves() {
		return Arrays.copyOf(this.moveList, this.moveList.length);
	}
	public Ability getPassive() {
		return passive;
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
	 * @return -1 not active, 0 finished, >0 amount of time remaining
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
	public void setAggro() { //flips
		this.aggro = !this.aggro;
	}

	public void setRandomTargets(List<Monster> possTargets) {
		this.clearTargets();
		
		if (!checkAutoTar(possTargets)) {
			int amountTars = this.getNumTar();
			for (int i = 0; i < amountTars; i++) {
				int randIdx = (int)(Math.random()*(possTargets.size()));
				this.addTarget(possTargets.remove(randIdx));
			}
		}
	}
	
	public void setTurn(List<Monster> targets) {
		setMove();
		setTargets(targets);
	}

	public void setTurn(List<Monster> targets, int idx) {
		setMove(idx);
		setTargets(targets);
	}

	public void executeTurn() { //wrapper for turnMove
		if (targets.size() > 0)
			turnMove.useAbility();
		else
			System.err.println("no targets found, aggro: " + this.aggro);
	}

	public void clearTurn() {
		if (turnMove != null && turnMove.resolved()) {
			turnMove = null;		
			clearTargets();
		}
	}
	
	public void usePassive(List<Monster> possTargets) { //look at; assume all fighters passed in
		if (passive != null) {
			List<Monster> sto = this.targets; //store previous targets
			this.targets = possTargets;

			passive.useAbility();

			this.targets = sto;
		}
	}

	public void addAttack(Ability adding) { //not sure if better
		List<Ability> moveStore = new ArrayList<>();
		for (Ability move: moveList)
			moveStore.add(move);

		moveStore.add(adding);
		moveList = new Ability[moveStore.size()];
		moveList = moveStore.toArray(moveList);
	}

	/**
	 * modifies max stat value to newVal
	 */
	protected void setStatMax (Stat stat, float newVal) {
		stats.get(stat).setBase(newVal);
	}

	/**
	 * modifies current stat value to newVal, caps at base
	 */
	public void setStat (Stat stat, float newVal) {
		stats.get(stat).setTempCapped(newVal);
	}

	/**
	 * changes the stat by mod, caps new value to base based on flag
	 * @param capped {@code true} if bounded by max value, {@code false} if just temporary buff
	 * @return the amount that went over the max value cap
	 */
	public float modStat (Stat stat, boolean capped, float mod) { //changes stat by val
		StatInfo info = stats.get(stat);
		float capOver = 0;
		float newVal = info.getTemp()+mod;

		if (capped) //keep eye on
			capOver = info.setTempCapped(newVal);
		else
			info.setTemp(newVal);

		return capOver;
	}

	/**
	 * turns on/off the inputted status
	 * @param duration positive value turns on for that amount of duration, negative turns off status
	 */
	public void setStatus(Status status, int duration) { //could set special status
		if (duration > 0) {
			int start = currentTurn();
			onChecks(status, start, duration);
		} else
			offChecks(status);
	}

	/**
	 * turns on/off the inputted status; defaults to one turn
	 */
	public void setStatus(Status status, boolean toggle) {
		int duration = toggle ? 1 : 0;
		setStatus(status, duration);
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

	@Override
	public boolean equals(Object other) {
		return other != null
			&& this.getClass() == other.getClass()
			&& this.id == ((Monster)other).id;
	}

	@Override
	public int hashCode() { //hash by id
		Integer idVal = Integer.valueOf(this.id);
		return idVal.hashCode();
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