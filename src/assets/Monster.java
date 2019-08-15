package assets;

import java.util.*;

import combat.Status;
import combat.moves.Ability;
import main.Index;
import main.Index.Move;
import main.Interface;

public class Monster extends Entity implements Cloneable {

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
		private int end;
		private boolean checked;

		StatusInfo () {
			this.end = -1;
			checked = true;
		}

		int getEnd() { return end; }
		boolean getChecked() { return checked; }
		void setEnd(int end) { this.end = end; }
		void setCheck(boolean checked) { this.checked = checked; }

	}


	/**variables */

	public final static int levMult = 2;
	
	private Ability passive;
	private Ability turnMove;
	
	protected boolean attType; //attType true means physical attack
	protected boolean aggro;
	protected Map<Stat, StatInfo> stats;
	protected Map<Status, StatusInfo> status; //temporary values
	protected Ability[] moveList;
	protected List<Monster> targets; //keep eye on how set and used
	protected int level = 1;


	/**
	 * constructor to have no extra attacks but basic
	 * @param aggro {@code true} for hero aggresivnes, {@code false} for aggressive against hero
	 * @param attType {@code true} for melee, {@code false} for magic
	 * @param statsIn order should follow order of {@link Stat}
	 */
	public Monster (String name, boolean aggro, boolean attType, List<Integer> statsIn) {
		this.name = name;
		this.aggro = aggro;
		this.attType = attType;

		this.stats = new HashMap<>();
		//hp, mp, att, def, mag, magR, spe, crit //order must be same as enum
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
		
		List<Ability> moveSto = new ArrayList<>(Arrays.asList(moveList)); //abilities from default constructor
		specials.forEach(special -> 
			moveSto.add(createAbility(special)));

		moveSto.sort(new Comparator<Ability>() {
			@Override
			public int compare(Ability a, Ability b) {
				Float aMana = a.getManaCost(), bMana = b.getManaCost();
				return aMana.compareTo(bMana);
			}
		});

		moveList = moveSto.toArray(Ability[]::new);
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

		this.stats = new HashMap<>(); //deep copy
		for (Stat statName: Stat.values())
			this.stats.put(statName, new StatInfo(copy.getStatMax(statName)));
		
		this.targets = new ArrayList<>(copy.targets);
		initStatus();

		if (copy.passive != null)
			this.setPassive((Ability)copy.passive.clone(this));
		
		this.moveList = Arrays.stream(copy.moveList) //should already be sorted
			.map(move -> (Ability)move.clone(this)) //could use name and valueOf
			.toArray(Ability[]::new);

	}

	//private helpers
	private Ability createAbility(Move name) {
		return Index.createAbility(name, this);
	}
	private Ability createPassive(Move name) {
		return Index.createPassive(name, this);
	}

	private void initStatus() {
		status = new HashMap<>();
		for (Status statusName: Status.values())
			if (statusName.equals(Status.SHIFT))
				ShapeShift.initShift(this);
			else if (statusName.equals(Status.POTION))
				Equipment.initEquip(this);
			else
				status.put(statusName, new StatusInfo());
	}

	
	/** extra values to check/modify while turning on status;
	 *  status will only update if new values will extend duration */
	private boolean onChecks(Status status, int endTurn) {
		StatusInfo info = this.status.get(status);
		int oldEnd = info.getEnd();

		boolean turnOn = endTurn > oldEnd; //only update if will extend duration
		switch(status) {
			case CONTROL:
				if (turnOn)
					this.setAggro();
				break;
			case DODGE:
				if (turnOn && oldEnd == -1)
					this.modStat(Stat.SPEED, false, this.getStatMax(Stat.SPEED)); //doubles speed
				break;
			case SHIFT:
				turnOn = turnOn && ShapeShift.switchCheck(info, true);
				break;
			case POTION:
				turnOn = turnOn && Equipment.switchCheck(info, true);
			default:
				break;
		}

		if (turnOn)
			info.setEnd(endTurn);

		return turnOn;
	}

	/** extra values to check/modify while turning off a status */
	private boolean offChecks(Status status) {
		StatusInfo info = this.status.get(status);
		
		boolean turnOff = true;
		switch(status) {
			case CONTROL:
				if (turnOff)
					this.setAggro();
				break;
			case DODGE:
				if (turnOff && info.getEnd() >= 0)
					this.modStat(Stat.SPEED, false, -this.getStatMax(Stat.SPEED));
				break;
			case SHIFT:
				turnOff = turnOff && ShapeShift.switchCheck(info, false);
				break;
			case POTION:
				turnOff = turnOff && Equipment.switchCheck(info, false);
			default:
				break;
		}
		
		if (turnOff)
			info.setEnd(-1);

		return turnOff;
	}


	private void setTargets(List<Monster> possTargets) {
		if (!checkAutoTar(possTargets)) {
			for (int i = 0; i < possTargets.size() 
				&& this.targets.size() < this.getNumTar(); i++) { //gets targets if needed
				
				Monster possTarget = possTargets.get(i);
				if (possTarget.getAggro() != this.getAggro())
					this.targets.add(possTarget);
			}
		}
	}

	private boolean checkAddAll(List<Monster> possTargets) {
		int numTar = this.getNumTar();

		boolean check = numTar == -1 || numTar >= possTargets.size();
		if (check)
			this.targets.addAll(possTargets);

		return check;
	}
	private boolean checkSelfTar() {
		boolean check = this.getNumTar() == 0;
		if (check)
			this.targets.add(this);
		
		return check;
	}

	//protected helpers
	protected boolean checkAutoTar(List<Monster> possTargets) {
		return checkAddAll(possTargets) || checkSelfTar();
	}

	/**
	 * returns number of targets; defaults to 0
	 * @return -1 is no limit, 0 is self, >0 max number of targets
	 */
	protected int getNumTar() { //could be null
		return turnMove == null? 0 : turnMove.getNumTar();
	}
	
	protected Ability getTurnMove() { return turnMove; }
	
	protected void setTurnMove(int idx) {
		if (idx == -1)
			turnMove = null;
		if (turnMove == null)
			turnMove = moveList[idx];
	}
	protected void setTurnMove() {
		int randInt = (int)(Math.random()*moveList.length);
		setTurnMove(randInt);
	}

	protected void setPassive(Ability passive) {
		if (passive != null && passive.isPassive() || passive == null) 
			this.passive = passive;
	}

	protected int currentTurn() {
		return Interface.currentFight().getTurnNum();
	}
	
	@Override
	protected Object clone() { //intentional shallow copy
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			System.err.println("issue cloning monster");
			return null;
		}
	}


	/*
	 * public methods
	 */

	//accessors
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

	public Monster[] getTargets() {
		return targets.toArray(Monster[]::new);
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
	 * shows the amount of time for the status;
	 * @return {@code -1} not active, {@code 0} finished, 
	 * {@code >0} amount of time remaining
	 */
	public int getStatus(Status status) { //checks if status needs updating, keep eye on
		int endTurn = this.status.get(status).getEnd();
		return endTurn == -1
			? -1
			: endTurn - currentTurn();
	}
	
	public boolean statusUpdated() {
		return this.status.values().stream()
			.map(StatusInfo::getChecked)
			.reduce(true, (accum, info) -> accum && info);
	}

	public Status[] getNotChecked() {
		return this.status.entrySet().stream()
			.filter(entry -> !entry.getValue().getChecked())
			.map(entry -> entry.getKey())
			.toArray(Status[]::new);
	}


	//mutators
	public void setAggro() { //flips
		this.aggro = !this.aggro;
	}

	public void setRandomTargets(List<Monster> possTargets) {
		this.targets.clear();
		
		if (turnMove == null || checkAutoTar(possTargets))
			return;

		int amountTars = this.getNumTar();
		for (int i = 0; i < amountTars; i++) {
			int randIdx = (int)(Math.random()*(possTargets.size()));
			this.targets.add(possTargets.remove(randIdx));
		}
	}
	
	public void setTurn(List<Monster> targets) {
		setTurnMove();
		setTargets(targets);
	}

	public void setTurn(List<Monster> targets, int idx) {
		setTurnMove(idx);
		setTargets(targets);
	}

	public void executeTurn() { //wrapper for turnMove
		if (targets.size() > 0)
			turnMove.useAbility();
		else
			Interface.writeOut(this.name + "'s targets(s) are no longer valid");
	}

	public void clearTurn() {
		if (turnMove != null && turnMove.resolved()) {
			turnMove = null;		
			this.targets.clear();
		}
	}
	
	public void usePassive(List<Monster> possTargets) { //look at; assume all fighters passed in
		if (passive != null) {
			List<Monster> sto = this.targets; //store previous targets
			Ability stoMove = this.turnMove;

			this.targets = new ArrayList<>();
			this.turnMove = passive;
			this.setTargets(possTargets);
			passive.useAbility();

			this.targets = sto;
			this.turnMove = stoMove;
		}

		if (this.getNumTar() != 0)
			this.targets.retainAll(possTargets);
	}

	public void addAttack(Ability adding) { //not sure if better
		List<Ability> moveStore = new ArrayList<>();
		for (Ability move: moveList)
			moveStore.add(move);

		moveStore.add(adding);
		moveList = moveStore.toArray(Ability[]::new);
	}

	/**
	 * modifies max stat value to newVal
	 */
	protected void setStatMax (Stat stat, float newVal) {
		stats.get(stat).setBase(newVal);
	}

	protected void modStatMax (Stat stat, float mod) {
		float modVal = mod + stats.get(stat).getBase();
		setStatMax(stat, modVal);
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
		float newVal = info.getTemp()+mod;
		float capOver = 0;

		if (capped) //keep eye on
			capOver = info.setTempCapped(newVal);
		else
			info.setTemp(newVal);

		return capOver;
	}

	/**
	 * turns on/off the inputted status
	 * @param duration positive value turns on for that amount of duration, negative turns off status
	 * @return {@code true} if status was successfully updated
	 */
	public boolean setStatus(Status status, int duration) { //could set special status
		if (duration > 0) {
			int endTurn = currentTurn() + duration;
			return onChecks(status, endTurn);
		} else
			return offChecks(status);
	}

	/**
	 * turns on/off the inputted status; defaults to one turn; 
	 * status may not always successful be changed, if the state
	 * of the monster is not correct
	 * @see {@link Monster#setStatus(Status, int)}
	 */
	public boolean setStatus(Status status, boolean toggle) {
		int duration = toggle ? 1 : 0;
		return setStatus(status, duration);
	}

	/** 
	 * returns the time of the status, and also updating state
	 * if the time is {@code 0}; if {@code 0}, the status will automatically be turned off; 
	 * multiple, consecutive calls of this method when status is initially {@code 0} will
	 * then be {@code -1} on calls afterwards
	 * @return {@code -1} not active, {@code 0} finished, 
	 * {@code >0} amount of time remaining 
	 */
	public int updateStatus(Status status) { //could add active mods here
		int timeLeft = this.getStatus(status);

		if (timeLeft == 0) { //keep eye on
			switch(status) {
				case SHIFT:
					ShapeShift.revert(this);
					break;
				case POTION:
					Equipment.unequip(this, Slot.POTION);
					break;
				default:
					this.setStatus(status, false);
			}
		}

		this.status.get(status).setCheck(true); //inefficient
		return timeLeft;
	}
	
	public void resetStatusChecks() {
		this.status.values().stream()
			.filter(info -> info.getEnd() > -1)
			.forEach(info -> info.setCheck(false));
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
		return this.getName() + " - " 
			+ getStat(Stat.HP) + " hp" + " - " 
			+ getStat(Stat.MP) + " mp" + " - "
			+ getStat(Stat.SPEED) + " speed";
	}


	public static int speedCompare(Monster a, Monster b) {
		Float aSpeed = a.getStat(Stat.SPEED), bSpeed = b.getStat(Stat.SPEED);
		return bSpeed.compareTo(aSpeed);
	}

	
}
