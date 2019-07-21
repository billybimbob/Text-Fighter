package combat;

import java.util.*;
import assets.*;

class FightLog {

    public static class LogInfo {
        private Monster attacker;
        private float damage;

        private LogInfo(Monster attacker, float damage) {
            this.attacker = attacker;
            this.damage = damage;
        }

        public Monster getAttacker() { return attacker; }
        public float getDamage() { return damage; }
    }

    private List<Map<Monster, List<LogInfo>>> log; //keys to map are the receiver

    public FightLog() {
        this.log = new ArrayList<>();
    }

    private Map<Monster, List<LogInfo>> newestRound() {
        return log.get(roundCount()-1);
    }


    /*
     * accessors
     */
    public int roundCount() {
        return log.size(); 
    }
    public List<LogInfo> getInfo (int round, Monster target) {
        List<LogInfo> info = null;
        try {
            info = log.get(round).get(target);
        } catch (IndexOutOfBoundsException e) { } //handle case if on first round
        
        if (info == null) //blank array, might keep eye on
            info = new ArrayList<>();
        return info;
    }


    /*
     * mutators
     */
    public void newRound() {
        log.add(new HashMap<>());
    }

    public void addLog(Monster attacker, Monster target, float damage) {
        Map<Monster, List<LogInfo>> round = newestRound();
        LogInfo newInfo = new LogInfo(attacker, damage);
        
        List<LogInfo> targLog = round.get(target);

        if (targLog == null) {
            targLog = new ArrayList<>();
            targLog.add(newInfo);
            round.put(target, targLog);
        } else {
            targLog.add(newInfo);
        }
    }

    public void clear() {
        log.clear();
    }

    @Override
    public String toString() {
        StringBuilder accum = new StringBuilder();
        
        for (Map<Monster, List<LogInfo>> round: log) //might want to be reversed
            for (Map.Entry<Monster, List<LogInfo>> entry: round.entrySet())  {
                String target = entry.getKey().getName();

                for (LogInfo info: entry.getValue()) {
                    String attacker = info.getAttacker().getName();
                    double damage = info.getDamage();
                    accum.append(attacker + " hit " + target + " for " + damage + " damage");
                }
            }

        return accum.toString();
    }

}