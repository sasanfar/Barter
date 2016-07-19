public class Agent {

	private int ID;
//	private int t;
	public int[][] p;

	public Agent() {
		ID = Driver.agentSet.size();
		p = new int[Parameters.numberOfTypesPerAgent][Parameters.numberOfResources];

		Driver.agentSet.add(this);
	}

	@Override
	public boolean equals(Object a) {
		if (this.ID == ((Agent) a).ID)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "A" + ID;
	}

	public int getID() {
		return ID;
	}

//	public int t() {
//		return t;
//	}
//
//	public void setT(int p) {
//		t = p;
//	}

	public int U(Outcome o, int[] t) {
		int u = 0;
		for (int r = 0; r < o.allocation.length; r++) {
			if (o.allocation[r] == this.ID)
				u += t[r];
		}
		return u;
	}
}
