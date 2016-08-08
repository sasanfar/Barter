import java.io.*;
import java.util.*;

import ilog.cplex.IloCplex.Param;

public class Driver {
	static String directory;
	static String modelDirectory;
	static PrintWriter out;

	static Outcome init;
	static Agent barter;
	static Master master;
	static Pricing pricing;

	static public int currentLocation[];

	public static List<Agent> agentSet = new ArrayList<Agent>();
	public static List<Resource> resourceSet = new ArrayList<Resource>();
	public static List<Outcome> outcomeSet = new ArrayList<Outcome>();
	public static List<Theta> thetaSet = new ArrayList<Theta>();
	public static int[][][] utilities;

	public static int iteration = 0;

	// static double[] oldPricingObj;
	// static double[] newPricingObj;

	static long startTime;
	static long endTime;

	public static void main(String[] args) {
		try {
			createFiles();
			long strt = System.currentTimeMillis();
			initialize();
			long end = System.currentTimeMillis();

			out.println("Number of agents= " + agentSet.size());
			out.println("Number of resources= " + resourceSet.size());
			out.println("Number of types per agent= " + Parameters.TYPES_AGENT);
			out.println("Initilizing time = " + (end - strt) + " milliseconds");
			System.out.println("Number of agents= " + agentSet.size());
			System.out.println("Number of resources= " + resourceSet.size());
			System.out.println("Number of types per agent= "
					+ Parameters.TYPES_AGENT);
			System.out.println("Initilizing time = " + (end - strt)
					+ " milliseconds");

			startTime = System.currentTimeMillis();
			run();
		} catch (Exception e) {
			if (e instanceof FileNotFoundException)
				System.out.println("Files not created!");
			else {
				out.flush();
				out.println(e.getMessage());
				out.flush();
			}
		} finally {
			endTime = System.currentTimeMillis();
			afterMath();
		}
	}

	private static void run() {
		boolean end = true;
		boolean redundantPricingObj = false;
		// oldPricingObj = new double[thetaSet.size()];
		// newPricingObj = new double[thetaSet.size()];
		do {
			end = true;
			redundantPricingObj = true;

			out.println();
			out.println("-------------------------------------------------");
			out.println("Iteration: " + iteration);
			out.flush();

			System.out.println("-------------------------------------------");
			System.out.println("Iteration: " + iteration);
			System.out.println("-------------------------------------------");

			master = new Master();
			master.solve();

			for (Theta theta : thetaSet) {
				pricing = new Pricing(theta);
				pricing.solve();
				// newPricingObj[theta.ID] = pricing.obj;
				if (pricing.obj > 0) {
					end = false;
					currentLocation = Arrays.copyOf(
							outcomeSet.get(outcomeSet.size() - 1).allocation,
							Parameters.RESOURCES);
					break;
				} else if (theta.equals(thetaSet.get(thetaSet.size() - 1)))
					end = true;
			}

			// for (double d : newPricingObj) {
			// if (d > 0)
			// end = false;
			// }

			// for (int i = 0; i < thetaSet.size(); i++) {
			// Double d1 = newPricingObj[i];
			// Double d2 = oldPricingObj[i];
			// if (!d1.equals(d2))
			redundantPricingObj = false;
			// }

			// oldPricingObj = Arrays.copyOf(newPricingObj, thetaSet.size());

			iteration++;
			// System.gc();
		} while (!end && !redundantPricingObj);

		System.out.println("-------------------------------------------------");
		System.out.println("Mixed Integer Problem");
		System.out.println("-------------------------------------------------");

		out.println("-------------------------------------------------");
		out.println("Mixed Integer Problem");

		master = new Master();
		master.MIP();

		out.flush();
		if (redundantPricingObj) {
			out.println("Program terminated because of redundant columns!!!");
			System.out
					.println("Program terminated because of redundant columns!!!");
		}

		if (end) {
			out.println("The end criteria for the GC algorithm was met!");
			System.out
					.println("The end criteria for the GC algorithm was met!");
		}
	}

	private static void initialize() throws FileNotFoundException {

		barter = new Agent();

		for (int i = 0; i < Parameters.AGENTS - 1; i++) {
			Agent a = new Agent();
			// a.setT(a.getID());
		}

		for (int i = 0; i < Parameters.RESOURCES; i++) {
			Resource r = new Resource();
		}

		buildThetas();

		buildInitialO();
		// int[] xx = { 4, 2, 1, 1, 2 };
		// Outcome o = new Outcome(xx);

		// buildExtraOutcome();

		currentLocation = new int[Parameters.RESOURCES];
		for (int r = 0; r < resourceSet.size(); r++) {
			currentLocation[r] = init.allocation[r];
		}
	}

	private static void buildThetas() throws FileNotFoundException {
		for (Agent a : agentSet) {
			boolean truthSelected = false;
			for (int i = 0; i < Parameters.TYPES_AGENT; i++) {
				int[] x = new int[Parameters.RESOURCES];
				for (int j = 0; j < x.length; j++)
					x[j] = (int) (Math.random() * Parameters.RESOURCES);
				Theta t = new Theta(thetaSet.size(), a.getID(), x);
				thetaSet.add(t);
				if (!truthSelected && Math.random() > 0.5) {
					truthSelected = true;
					agentSet.get(a.getID()).setTruth(t.getID());
				}
				if (!truthSelected && i == Parameters.TYPES_AGENT - 1) {
					truthSelected = true;
					agentSet.get(a.getID()).setTruth(t.getID());
				}
			}
		}

	}

	private static void buildInitialO() {
		// init = new Outcome(Parameters.startingLocations);

		int[] x = new int[Parameters.RESOURCES];

		for (int i = 0; i < x.length; i++) {
			int y = 0;
			do {
				y = (int) (Math.random() * (resourceSet.size() - 1));
			} while (y == 0);
			x[i] = y;
		}

		init = new Outcome(x);
	}

	private static void buildExtraOutcome() {
		int[] x = new int[Parameters.RESOURCES];
		for (int i = 0; i < x.length; i++) {
			x[i] = (int) (Math.random() * Parameters.AGENTS);
		}
		Outcome o = new Outcome(x);
	}

	private static void createFiles() throws FileNotFoundException {
		directory = "C:/Users/Sasan/workspace/Barter/Files "
				+ (new Date()).toString().replace(' ', '_').replace(':', '-')
						.substring(4) + "/";
		;
		modelDirectory = directory;
		File file = new File(directory);
		if (!file.exists())
			file.mkdirs();
		File[] files = file.listFiles();
		for (File f : files) {
			f.delete();
		}

		file = new File(directory + "/ results.txt");

		File model = new File(modelDirectory);
		if (!model.exists())
			model.mkdirs();
		out = new PrintWriter(file);
	}

	private static void afterMath() {

		out.flush();
		Driver.out.println("==================================");
		out.flush();
		Driver.out.println("   Program was terminated in " + iteration
				+ " iterations!");
		out.flush();
		System.out.println("==================================");
		System.out.println("   Program was terminated in " + iteration
				+ " iterations!");

		Driver.out.println("Optimization execution time: "
				+ (endTime - startTime) + " milliseconds");
		out.flush();
		System.out.println("Optimization execution time: "
				+ (endTime - startTime) + " milliseconds");

	}
}
