public class KeyTwoFold <T>{
	Agent A;
	T T;

	public KeyTwoFold(Agent a, T t) {
		A = a;
		T = t;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KeyTwoFold))
			return false;
		KeyTwoFold ref = (KeyTwoFold) obj;
		return this.A.equals(ref.A) && this.T.equals(ref.T);
	}

	@Override
	public int hashCode() {
		return A.hashCode() ^ T.hashCode();
	}
}
