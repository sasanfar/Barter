public class Parameters {
	/**
	 * the agent at position 0 is always the barter, therefore the first theta!
	 */
	public static int numberOfAgents = 4;
	public static int numberOfResources = 10;
	public static int numberOfTypesPerAgent = 3;
//	public static final double M = 100000;
	// public static int[][] thetaSet = { { -0, -0, -0, -0, -0 },
	// { 0, 0, 4, 0, 2 }, { 0, 1, 4, 5, 0 }, { 3, 5, 1, 2, 4 },
	// { 2, 4, 1, 2, 1 }, { 3, 3, 2, 2, 4 }, { 1, 5, 4, 3, 0 },
	// { 4, 4, 5, 5, 0 }, { 0, 0, 3, 3, 4 }, { 1, 4, 4, 4, 3 },
	// { 1, 0, 1, 2, 5 }, { 0, 0, 4, 1, 3 }, { 1, 5, 3, 2, 4 },
	// { 0, 4, 4, 0, 5 }, { 3, 4, 2, 1, 0 }, { 3, 3, 1, 0, 2 },
	// { 4, 4, 3, 2, 2 }, { 1, 5, 4, 2, 3 }, { 5, 4, 4, 0, 5 },
	// { 5, 0, 5, 4, 3 } };

	public static int[][] t0 = { { -0, -0, -0, -0, -0 }, { 0, 0, 4, 0, 2 },
			{ 0, 1, 4, 5, 0 }, { 3, 5, 1, 2, 4 }, { 2, 4, 1, 2, 1 },
			{ 3, 3, 2, 2, 4 } };

	public static int[][] t1 = { { 2, 4, 1, 2, 1 }, { 3, 3, 2, 2, 4 },
			{ 1, 5, 4, 3, 0 }, { 4, 4, 5, 5, 0 }, { 1, 5, 3, 2, 4 },
			{ 0, 0, 3, 3, 4 } };

	public static int[][] t2 = { { 1, 4, 4, 4, 3 }, { 1, 0, 1, 2, 5 },
			{ 0, 0, 4, 1, 3 }, { 1, 5, 3, 2, 4 }, { 0, 4, 4, 0, 5 },
			{ 1, 5, 3, 2, 4 } };
	
	public static int[][] t3 = { { 3, 3, 1, 0, 2 }, { 1, 5, 3, 2, 4 },
			{ 4, 4, 3, 2, 2 }, { 1, 5, 4, 2, 3 }, { 5, 4, 4, 0, 5 },
			{ 5, 0, 5, 4, 3 } };
	/**
	 * each element indicates the ID of each resource. agent's IDs correspond to
	 * the location of each element.
	 */
	public static int startingLocations[] = { 1, 2, 3, 4 };

}

// public class Parameters {
//
// public static int numberOfAgents = 5;
// public static int numberOfResources = 4;
// public static int numberOfAdditionalTypes = 5; // + numberOfAgents
// public static final double M = 100000;
// public static int[][] agentPreferences = new
// int[numberOfAgents][numberOfResources];
//
// }
