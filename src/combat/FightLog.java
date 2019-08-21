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
    private List <Map<Monster, Map<Monster, Log>>> logs;


    FightLog() {
        this.logs = new ArrayList<>();
    }

    //private methods
    private Map<Monster, Map<Monster, Log>> newestRound() {
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
    public Map<Monster, Log> getLog (int round, Monster target) {
        Map<Monster, Log> info = null;
        try {
            info = logs.get(round).get(target);
        } catch (IndexOutOfBoundsException e) { } //handle case if on first round
        
        if (info == null) //blank map, might keep eye on
            info = new HashMap<>();
        
        return info;
    }

	public float getTurnDamage(int round, Monster target) {
		float totalDam = 0;
		for(Entry<Monster, Log> info: this.getLog(round, target).entrySet())
			totalDam += info.getValue().getDamage();
		
		return totalDam;
	}


    /* mutators */
    public void addLog(Log log) {
        if (log == null) {
            System.err.println("log is null");
            return;
        }

        Map<Monster, Map<Monster, Log>> round = newestRound();
        Map<Monster, Log> targLog = round.get(log.target);

        if (targLog == null) {
            targLog = new HashMap<>();
            targLog.put(log.attacker, log);
            round.put(log.target, targLog);
        } else {
            if (targLog.containsKey(log.attacker)) //each attacker expected to target target once per round at most
                System.err.println("dup attacker");
            targLog.put(log.attacker, log); //will override potentially
        }
    }


    @Override
    public String toString() {
        StringBuilder accum = new StringBuilder();
        
        for (Map<Monster, Map<Monster, Log>> round: logs) //might want to be reversed
            for (Entry<Monster, Map<Monster, Log>> entry: round.entrySet())  {
                String target = entry.getKey().getName();

                for (Entry<Monster, Log> info: entry.getValue().entrySet()) {
                    String attacker = info.getKey().getName();
                    double damage = info.getValue().getDamage();
                    accum.append(attacker + " hit " + target + " for " + damage + " damage");
                }
            }

        return accum.toString();
    }

}