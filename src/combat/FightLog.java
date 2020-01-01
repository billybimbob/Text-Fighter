package combat;

import combat.moves.Ability;
import assets.Monster;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FightLog {

	public static class Log {
		private Monster attacker, target;
		private float damage;
		private Ability attack;
		private List<Status> applied;
	
		public Log(Monster attacker, Monster target, Ability ability, float damage, List<Status> applied) {
			this.attacker = attacker;
			this.target = target;
			this.attack = ability;
			this.damage = damage;
			this.applied = Collections.unmodifiableList(applied);
		}
	
		public Monster getAttacker() { return attacker; }
		public Monster getTarget() { return target; }
		public Ability getAttack() { return attack; }
		public float getDamage() { return damage; }
		public List<Status> getApplied() { return applied; }
	
		private String appliedStr() {
			Function<Status, String> getName = Status::name;
			return applied.stream()
				.map(getName.andThen(String::toLowerCase))
				.collect(Collectors.joining(", "));
		}
	
		@Override
		public String toString() {
			return attacker.getName() + " used " + attack.getName() 
				+ " on" + target.getName() + " for " + damage 
				+ " damage, and applied " + appliedStr();
		}
	}

	 //key is the target of log
	private List< Map<Monster, List<Log>> > logs;

	FightLog() {
		this.logs = new ArrayList<>();
	}

	//private methods
	private Map<Monster, List<Log>> newestRound() {
		return logs.get(roundCount()-1);
	}

	//default methods
	int roundCount() { return logs.size(); }

	void newRound() {
		logs.add(new HashMap<>());
	}
	void clear() {
		logs.clear();
	}

	//public methods
	/* accessors */
	/**
	 * returns combat info based on round and target of combat
	 * @param round
	 * @param target
	 * @return key is the attacker, value is the log
	 */
	public List<Log> getLogs (int round, Monster target) {
		List<Log> targLogs = null;
		try {
			targLogs = logs.get(round).get(target);
		} catch (IndexOutOfBoundsException e) { } //handle case if on first round
		
		if (targLogs == null) {
			System.err.println("no logs found");
			targLogs = new ArrayList<>();
		}

		return targLogs;
	}

	public float getTurnDamage(int round, Monster target) {
		float totalDam = 0;
		for(Log log: this.getLogs(round, target))
			totalDam += log.getDamage();
		
		return totalDam;
	}


	/* mutators */
	public void addLog(Log log) {
		if (log == null)
			throw new NullPointerException("log is null");

		var round = newestRound();
		if (!round.containsKey(log.getTarget())) //slower?
			round.put(log.getTarget(), new ArrayList<>());
		
		round.get(log.getTarget()).add(log);
	}


	@Override
	public String toString() {
		StringBuilder accum = new StringBuilder();
		this.logs.forEach(round -> {
			round.entrySet().forEach(log -> {
				accum.append(log.toString());
			});
		});
		return accum.toString();
	}

}