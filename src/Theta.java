import java.io.*;
import java.util.Arrays;

public class Theta {

	int ID;
	int t[][];

	public Theta() {
		t = new int[Parameters.numberOfAgents][Parameters.numberOfResources];
	}

	public Theta(int id, int[][] x) {
		ID = id;
		t = Arrays.copyOf(x, x.length);
	}

	public void set(Agent a, int[] x) {
		t[a.getID()] = Arrays.copyOf(x, x.length);
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
			for (int i = 0; i < t.length; i++)
				w.println(Arrays.toString(t[i]));
			w.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public int getID() {
		return ID;
	}

}
