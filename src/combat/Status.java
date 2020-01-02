package combat;

/**
 * <ul>
 * all statuses cause various effects while active:
 * <li> {@code BURN}: burning, takes damage over time </li>
 * <li> {@code CONTROL}: mind controlled, and will swap agression </li>
 * <li> {@code DODGE}: increased evasiness, leading to double speed</li>
 * <li> {@code FRENZY}: targets for attacks are random </li>
 * <li> {@code POISON}: poisoned, and will take damage over time to random stats</li>
 * <li> {@code POTION}: has a potion effect active</li>
 * <li> {@code REFLECT}: any damage received will reflect a portion back to the attacker </li>
 * <li> {@code SHIFT}: monster is shapeshifted into another form </li>
 * <li> {@code STUN}: stunned, cannot do any action </li>
 */
public enum Status {
    BURN, CONTROL, DODGE, FRENZY, POISON, POTION, REFLECT, SHIFT, STUN;
}