import java.io.*;
import java.util.Arrays;

public class Theta {
	int agent;
	int ID;
	/**
	 * The array of type profile, where the index corresponds to one particular resource
	 */
	int table[];

	public Theta() {
		table = new int[Parameters.RESOURCES];
	}

	public Theta(int id, int agent, int[] x) {
		ID = id;
		this.agent = agent;
		table = Arrays.copyOf(x, x.length);
	}

	public void set(int[] x) {
		table = Arrays.copyOf(x, x.length);
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
			w.println(Arrays.toString(table));
			w.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public int getID() {
		return ID;
	}

}
