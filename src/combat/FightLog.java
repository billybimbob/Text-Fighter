package combat;

import java.util.*;
import java.util.Map.Entry;

import combat.moves.Ability;
import assets.Monster;

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

        public Ability getAttack() { return attack; }
        public float getDamage() { return damage; }
        public List<Status> getApplied() { return applied; }
    }

    //1st Monster key is target; 2nd Monster key is attacker
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
        
        if (targLogs == null)
            targLogs = new ArrayList<>();

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
        if (log == null) {
            System.err.println("log is null");
            return;
        }

        var round = newestRound();
        var targLog = round.get(log.target);

        if (targLog == null) {
            targLog = new ArrayList<>();
            round.put(log.target, targLog);
        }
        
        targLog.add(log);
    }


    @Override
    public String toString() {
        StringBuilder accum = new StringBuilder();
        
        for (Map<Monster, List<Log>> round: this.logs) //might want to be reversed
            for (Entry<Monster,List<Log>> entry: round.entrySet())  {
                String target = entry.getKey().getName();

                for (Log log: entry.getValue()) {
                    String attacker = log.attacker.getName();
                    double damage = log.getDamage();
                    accum.append(attacker + " hit " + target + " for " + damage + " damage");
                }
            }

        return accum.toString();
    }

}