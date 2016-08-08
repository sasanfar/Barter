import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;
import ilog.concert.*;

public class Master {
	private IloCplex master;

	private List<IloRange> ProbaRange = new ArrayList<IloRange>();
	public Map<String, Double> ProbaDual = new HashMap<String, Double>();

	private List<IloRange> IRRange = new ArrayList<IloRange>();
	public Map<String, Double> IRDual = new HashMap<String, Double>();

	private List<IloRange> ICRange = new ArrayList<IloRange>();
	public Map<String, Double> ICDual = new HashMap<String, Double>();

	public void solve() {
		try {
			master = new IloCplex();

			// VARIABLES
			for (Outcome o : Driver.outcomeSet) {
				for (Theta t : Driver.thetaSet) {
					o.g[t.ID] = master.numVar(0, 1, "g[" + o.getID() + "]["
							+ t.ID + "]");
				}
			}

			// OBJECTIVE
			IloLinearNumExpr expr = master.linearNumExpr();
			for (Theta t : Driver.thetaSet) {
				for (Outcome o : Driver.outcomeSet) {
					for (Agent a : Driver.agentSet) {
						// if (!a.equals(Driver.barter))
						if (t.agent == a.getID())
							expr.addTerm(a.U(o, t.table), o.g[t.ID]);
					}
				}
			}

			master.addMaximize(expr);

			// CONSTRAINTS

			// PROBA
			for (Theta t : Driver.thetaSet) {
				expr = master.linearNumExpr();
				for (Outcome o : Driver.outcomeSet) {
					expr.addTerm(1, o.g[t.ID]);
				}
				ProbaRange.add(master.addEq(1, expr, "Proba " + t));
			}

			// IR
			for (Agent a : Driver.agentSet) {
				for (Theta t : Driver.thetaSet)
					if (t.agent == a.getID()) {
						expr = master.linearNumExpr();
						for (Outcome o : Driver.outcomeSet) {
							expr.addTerm(a.U(o, t.table), o.g[t.ID]);
						}
						IRRange.add(master.addGe(expr,
								a.U(Driver.init, t.table), "IR " + a + "," + t));
					}
			}
			
//			// IR Ex-Post
//			for (Agent a : Driver.agentSet) {
//				for (Theta t : Driver.thetaSet) {
//					for (Outcome o : Driver.outcomeSet) {
//						expr = master.linearNumExpr();
//						expr.addTerm(
//								a.U(o, t.table) - a.U(Driver.init, t.table),
//								o.g[t.ID]);
//						master.addGe(expr, 0);
//					}
//				}
//			}

			// IC
			for (Agent a : Driver.agentSet) {
				for (Theta t : Driver.thetaSet)
					if (t.agent == a.getID() && t.ID != a.getTruth()) {
						expr = master.linearNumExpr();
						for (Outcome o : Driver.outcomeSet) { // left hand side
																// of the
																// equation
							expr.addTerm(
									-1
											* a.U(o, Driver.thetaSet.get(a
													.getTruth()).table),
									o.g[t.ID]);
						}
						for (Outcome o : Driver.outcomeSet) {// right hand side
																// of the
																// equation
							expr.addTerm(a.U(o,
									Driver.thetaSet.get(a.getTruth()).table),
									o.g[a.getTruth()]);
						}
						ICRange.add(master.addGe(expr, 0, "IC " + a + "," + t));
					}
			}

			master.exportModel(Driver.modelDirectory + "/Master_"
					+ Driver.iteration + ".lp");
			if (master.solve()) {
				printSolution();
				saveDuals();
			} else {
				Driver.out.flush();
				Driver.out.println("Master not solved");
			}
			master.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printSolution() throws IloException, UnknownObjectException {
		Outcome solution[] = new Outcome[Driver.thetaSet.size()];
		Driver.out.flush();
		Driver.out.println("*************************************");
		Driver.out.println("Master objective = " + master.getObjValue());
		for (Outcome o : Driver.outcomeSet) {
			for (Theta t : Driver.thetaSet)
				if (master.getValue(o.g[t.ID]) > 0) {
					Driver.out.println("g[" + o.getID() + "][" + t.ID + " :A"
							+ t.agent + "]= " + master.getValue(o.g[t.ID])
							+ "        O= " + o.getID());
					solution[t.ID] = o;
				}
		}

		Driver.out.flush();
		System.out.println("*************************************");
		System.out.println("Master objective = " + master.getObjValue());
		for (Outcome o : Driver.outcomeSet) {
			for (Theta t : Driver.thetaSet)
				if (master.getValue(o.g[t.ID]) > 0) {
					System.out.println("g[" + o.getID() + "][" + t.ID + "]= "
							+ master.getValue(o.g[t.ID]));
				}
		}

		System.out.println();

		// Driver.out.println("Current locations: "
		// + Arrays.toString(Driver.currentLocation));
	}

	private void saveDuals() throws UnknownObjectException, IloException {
		// PROBA
		Driver.out.println();
		Driver.out.println("Proba Duals:");
		for (IloRange rng : ProbaRange) {
			double d = master.getDual(rng);
			String s = rng.getName();
			ProbaDual.put(s, d);
			Driver.out.print(s + ": " + d + "      ");
		}

		// IC
		Driver.out.println();
		Driver.out.println();
		Driver.out.println("IC Duals:");

		boolean allAreZero = true;
		for (IloRange rng : ICRange) {
			double d = master.getDual(rng);
			if (d != 0)
				allAreZero = false;
			String s = rng.getName();
			ICDual.put(s, d);
			Driver.out.print(s + ": " + d + "      ");
		}
		if (allAreZero) {
			Driver.out.println();
			Driver.out.println();
			Driver.out.println("====================================");
			Driver.out.println("||  All IC dual values are zero!  ||");
			Driver.out.print("====================================");
		}

		// IR
		Driver.out.println();
		Driver.out.println();
		Driver.out.println("IR Duals:");
		allAreZero = true;
		for (IloRange rng : IRRange) {
			double d = master.getDual(rng);
			if (d != 0)
				allAreZero = false;
			String s = rng.getName();
			IRDual.put(s, d);
			Driver.out.print(s + ": " + d + "      ");
		}
		if (allAreZero) {
			Driver.out.println();
			Driver.out.println();
			Driver.out.println("====================================");
			Driver.out.println("||  All IR dual values are zero!  ||");
			Driver.out.print("====================================");
		}

		Driver.out.println();
		Driver.out.println();

	}

	public void MIP() {
		try {
			master = new IloCplex();

			// VARIABLES
			for (Outcome o : Driver.outcomeSet) {
				for (Theta t : Driver.thetaSet) {
					o.g[t.ID] = master.boolVar("g[" + o.getID() + "][" + t.ID
							+ "]");
				}
			}

			// OBJECTIVE

			IloLinearNumExpr expr = master.linearNumExpr();
			for (Theta t : Driver.thetaSet) {
				for (Outcome o : Driver.outcomeSet) {
					for (Agent a : Driver.agentSet) {
						if (t.agent == a.getID())
							expr.addTerm(a.U(o, t.table), o.g[t.ID]);
					}
				}
			}

			master.addMaximize(expr);

			// CONSTRAINTS

			// PROBA
			for (Theta t : Driver.thetaSet) {
				expr = master.linearNumExpr();
				for (Outcome o : Driver.outcomeSet) {
					expr.addTerm(1, o.g[t.ID]);
				}
				ProbaRange.add(master.addEq(1, expr, "Proba " + t));
			}

//			// IR
			for (Agent a : Driver.agentSet) {
				for (Theta t : Driver.thetaSet)
					if (t.agent == a.getID()) {
						expr = master.linearNumExpr();
						for (Outcome o : Driver.outcomeSet) {
							expr.addTerm(a.U(o, t.table), o.g[t.ID]);
						}
						IRRange.add(master.addGe(expr,
								a.U(Driver.init, t.table), "IR " + a + "," + t));
					}
			}
			
//			// IR Ex-Post
//						for (Agent a : Driver.agentSet) {
//							for (Theta t : Driver.thetaSet) {
//								for (Outcome o : Driver.outcomeSet) {
//									expr = master.linearNumExpr();
//									expr.addTerm(
//											a.U(o, t.table) - a.U(Driver.init, t.table),
//											o.g[t.ID]);
//									master.addGe(expr, 0);
//								}
//							}
//						}
			// IC
			for (Agent a : Driver.agentSet) {
				for (Theta t : Driver.thetaSet)
					if (a.getTruth() == t.getID())
						for (Theta t2 : Driver.thetaSet)
							// if (t.t != Driver.thetaSet.get(0).t)
							if (t.agent == a.getID() && t2.agent == a.getID()
									&& t2 != t) {
								expr = master.linearNumExpr();
								for (Outcome o : Driver.outcomeSet) {
									expr.addTerm(-a.U(o, t.table),
											o.g[t2.getID()]);
								}
								for (Outcome o : Driver.outcomeSet) {
									expr.addTerm(a.U(o, t.table),
											o.g[t.getID()]);
								}
								ICRange.add(master.addGe(expr, 0, "IC " + a
										+ "," + t));
							}
			}

			master.exportModel(Driver.modelDirectory + "/MIP.lp");
			if (master.solve()) {
				printSolutionMIP();
			} else {
				Driver.out.flush();
				Driver.out.println("*************************************");
				Driver.out.println("MIP not solved");
				Driver.out.flush();
				Driver.out.println(master.getCplexStatus());
				Driver.out.flush();
			}
			master.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printSolutionMIP() throws IloException, UnknownObjectException {
		Outcome solution[] = new Outcome[Driver.thetaSet.size()];
		Driver.out.flush();
		Driver.out.println("*************************************");
		Driver.out.println("Mixed Integer Problem objective = "
				+ master.getObjValue());
		for (Theta t : Driver.thetaSet) {
			for (Outcome o : Driver.outcomeSet)
				if (master.getValue(o.g[t.ID]) > 0) {
					Driver.out.println("g[" + o.getID() + "][" + t.ID + " :A"
							+ t.agent + "]= " + master.getValue(o.g[t.ID]));
					solution[t.ID] = o;
				}
		}

		Driver.out.flush();
		System.out.println("*************************************");
		System.out.println("Mixed Integer Problem objective = "
				+ master.getObjValue());
		for (Outcome o : Driver.outcomeSet) {
			for (Theta t : Driver.thetaSet)
				if (master.getValue(o.g[t.ID]) > 0) {
					System.out.println("g[" + o.getID() + "][" + t.ID + " :A"
							+ t.agent + "]= " + master.getValue(o.g[t.ID]));
				}
		}

		System.out.println();

		
		// Driver.out.println("Current locations: "
		// + Arrays.toString(Driver.currentLocation));
	}
}
