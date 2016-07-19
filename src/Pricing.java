import java.util.Arrays;

import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;

public class Pricing {
	IloCplex p;

	IloIntVar x[][];
	Theta t;
	public double obj;
	public boolean builtOutcome;

	public Pricing(Theta _t) {
		t = _t;
	}

	public void solve() {
		try {
			p = new IloCplex();
			x = new IloIntVar[Parameters.numberOfAgents + 1][Parameters.numberOfResources];

			for (Agent a : Driver.agentSet) {
				for (Resource r : Driver.resourceSet) {
					x[a.getID()][r.getID()] = p.boolVar("x[" + a + "][" + r
							+ "]");
				}
			}
			IloLinearNumExpr expr = p.linearNumExpr();

			double vProba = Driver.master.ProbaDual.get("Proba " + t);
			expr.setConstant(-vProba);
			for (Agent a : Driver.agentSet) {
				for (Resource r : Driver.resourceSet) {
					expr.addTerm(t.t[a.getID()][r.getID()],
							x[a.getID()][r.getID()]);
				}
			}

			for (Agent a : Driver.agentSet) {
				double u = 0;
				for (Theta t : Driver.thetaSet) {
					if (!t.equals(this.t)
							&& Driver.master.ICDual.get("IC " + a + "," + t) != null) {
						u += Driver.master.ICDual.get("IC " + a + "," + t);
					}
				}
				if (Driver.master.IRDual.get("IR " + a + "," + t) != null)
					u += Driver.master.IRDual.get("IR " + a + "," + t);
				for (Resource r : Driver.resourceSet) {
					expr.addTerm(u * t.t[a.getID()][r.getID()],
							x[a.getID()][r.getID()]);
				}

				double y = 0;
				for (Theta t : Driver.thetaSet) {
					if (!t.equals(this.t)
							&& Driver.master.ICDual.get("IC " + a + "," + t) != null) {
						y += Driver.master.ICDual.get("IC " + a + "," + t);
					}
					for (Resource r : Driver.resourceSet) {
						expr.addTerm(y * t.t[a.getID()][r.getID()],
								x[a.getID()][r.getID()]);
					}
				}
			}

			p.addMaximize(expr);

			// C1
			for (Resource r : Driver.resourceSet) {
				expr = p.linearNumExpr();
				for (Agent a : Driver.agentSet) {
					expr.addTerm(1, x[a.getID()][r.getID()]);
				}
				p.addEq(expr, 1,"C1");
			}  

			expr = p.linearNumExpr();

			// C2
			for (Resource r : Driver.resourceSet) {
				for (Agent a1 : Driver.agentSet) {
					for (Agent a2 : Driver.agentSet) {
						if ((Driver.currentLocation[t.ID][r.getID()] == a1
								.getID()) && (!a1.equals(a2))
						// && (!a1.equals(Driver.barter))
						// && (!a2.equals(Driver.barter))
						) {
							expr = p.linearNumExpr();
							expr.addTerm(1, x[a2.getID()][r.getID()]);
							p.addEq(0, expr,"C2");
						}
					}
				}
			}

			 p.exportModel(Driver.modelDirectory + "/Pricing_"
			 + Driver.iteration + "," + t + ".lp");
			if (p.solve()) {
				obj = p.getObjValue();
				System.out.println("Pricing solved!         ->      " + obj);
				Driver.out.println("  Pricing" + t + " =  " + obj);
				if (p.getObjValue() > 0) {
					int id = buildNewOutcome();
					System.out.println("Built Outcome        ->        Outcome"
							+ id);
				}
			}
			p.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int buildNewOutcome() throws UnknownObjectException, IloException {

		int[] locations = new int[Parameters.numberOfResources];
		for (int r = 0; r < Parameters.numberOfResources; r++) {
			for (int a = 0; a < Parameters.numberOfAgents; a++) {
				if (p.getValue(x[a][r]) == 1) {
					locations[r] = a;
				}
			}
		}
		Outcome o = new Outcome(locations);
		Driver.out.flush();
		Driver.out.println("        Outcome built: " + o.getID());

		Driver.currentLocation[t.getID()] = Arrays.copyOf(o.allocation,
				o.allocation.length);

		return o.getID();
	}

}
