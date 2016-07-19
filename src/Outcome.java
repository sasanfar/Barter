import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

import ilog.concert.IloNumVar;

public class Outcome {
	public int ID;
	public IloNumVar g[];
	public int allocation[];
	public int calculatedU;

	public Outcome() {
		ID = Driver.outcomeSet.size();
		g = new IloNumVar[Driver.thetaSet.size()];
		Driver.outcomeSet.add(this);
		print();
	}

	public Outcome(int[] a) {
		ID = Driver.outcomeSet.size();
		Driver.outcomeSet.add(this);
		g = new IloNumVar[Driver.thetaSet.size()];
//		for (int i = 0; i < a.length; i++)
//			allocation[i] = a[i];
		allocation = Arrays.copyOf(a, a.length);
		print();
	}

	public int getID() {
		return ID;
	}

	public void setAllocation(int[] allocation) {
		for (int i : allocation) {
			this.allocation[i] = allocation[i];
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(this.allocation);
	}
	
	public void print() {
		try {
			String fileDirectory = Driver.directory + "/O" + ID + ".txt";
			File file = new File(fileDirectory);
			PrintWriter w = new PrintWriter(file);

				w.println(Arrays.toString(allocation));

			w.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
