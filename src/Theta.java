import java.io.*;
import java.util.Arrays;

public class Theta {
	int agent;
	int ID;
	int t[];

	public Theta() {
		t = new int[Parameters.numberOfResources];
	}

	public Theta(int id, int agent, int[] x) {
		ID = id;
		this.agent = agent;
		t = Arrays.copyOf(x, x.length);
	}

	public void set(int[] x) {
		t = Arrays.copyOf(x, x.length);
	}

	@Override
	public String toString() {
		return "Theta" + ID;
	}

	public void print() {
		try {
			String fileDirectory = Driver.directory + "/T" + ID + ".txt";
			File file = new File(fileDirectory);
			PrintWriter w = new PrintWriter(file);
			w.println("Agent" + agent);
			w.println(Arrays.toString(t));
			w.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public int getID() {
		return ID;
	}

}
