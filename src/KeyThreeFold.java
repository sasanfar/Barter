public class KeyThreeFold<E,T> {
	Agent A;
	T T1;
	E T2;

	public KeyThreeFold(Agent a, T t1, E t2) {
		A = a;
		T1 = t1;
		T2 = t2;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KeyThreeFold))
			return false;
		KeyThreeFold ref = (KeyThreeFold) obj;
		return this.A.equals(ref.A) && this.T1.equals(ref.T1)
				&& this.T2.equals(ref.T2);
	}

	@Override
	public int hashCode() {
		return A.hashCode() ^ T1.hashCode() ^ T2.hashCode();
	}
}
