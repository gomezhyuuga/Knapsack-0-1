import KP.Solvers.Constructive.ConstructiveSolver;
import KP.KnapsackProblem;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic.Heuristic;
import KP.Solvers.HyperHeuristic.SampleHyperHeuristic;

public class Run {

    public static void main(String[] args) {
                
        String[] features, heuristics;        
        KnapsackProblem problem;
        ConstructiveSolver solver;
        SampleHyperHeuristic hh;        
        problem = new KnapsackProblem("../Instances/GA-MAXPROFITWEIGHT_20_020.kp");
        
        
        features = new String[] {
            "NORM_CORRELATION",
        };
        
        heuristics = new String[]{
            "DEFAULT",
            "MIN_WEIGHT",
            "MAX_PROFIT",
            "MAX_PROFIT_PER_WEIGHT_UNIT"
        };
        solver = new ConstructiveSolver(problem);
        System.out.println("Profit with default: " + solver.solve(new ConstructiveHeuristic(Heuristic.DEFAULT)).getProfit());
        solver = new ConstructiveSolver(problem);
        System.out.println("Profit with min-weight: " + solver.solve(new ConstructiveHeuristic(Heuristic.MIN_WEIGHT)).getProfit());
        solver = new ConstructiveSolver(problem);
        System.out.println("Profit with max-profit: " + solver.solve(new ConstructiveHeuristic(Heuristic.MAX_PROFIT)).getProfit());
        solver = new ConstructiveSolver(problem);
        System.out.println("Profit with max-profit/weight: " + solver.solve(new ConstructiveHeuristic(Heuristic.MAX_PROFIT_PER_WEIGHT_UNIT)).getProfit());
        hh = new SampleHyperHeuristic(features, heuristics);
        solver = new ConstructiveSolver(problem);
        System.out.println("Profit with hyper-heuristic: " + solver.solve(hh).getProfit());
    }
}
