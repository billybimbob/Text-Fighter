package assets;

public abstract class Entity implements Comparable<Entity> {

    private static int idCount = 0;
    private int id; //think how to differentiate from monsters
    protected String name;

    protected Entity() {
        setId();
    }

    protected void setId() { this.id = idCount++; }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    

	@Override
	public boolean equals(Object other) {
		return other != null
			&& this.getClass() == other.getClass()
			&& this.id == ((Entity)other).id;
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
	public int compareTo (Entity other) {
        return Integer.valueOf(this.id).compareTo(Integer.valueOf(other.id));
    }
}