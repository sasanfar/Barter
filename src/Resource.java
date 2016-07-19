public class Resource {
	static int nextID = 0;
	int ID;
	Agent location;

	public Resource() {
		ID = Driver.resourceSet.size();
		Driver.resourceSet.add(this);
	}

	public int getID() {
		return ID;
	}

	public Agent getLocation() {
		return location;
	}

	public void setLocation(Agent a) {
		location = a;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Resource && ((Resource) o).ID == (this.ID))
			return true;
		return false;
	}
	@Override
	public String toString(){
		return "R"+ID;
	}
}
