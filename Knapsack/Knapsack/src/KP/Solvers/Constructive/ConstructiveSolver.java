package KP.Solvers.Constructive;

import HERMES.Exceptions.NoSuchFeatureException;
import HERMES.Exceptions.NoSuchHeuristicException;
import HERMES.FeatureManager;
import HERMES.Selector.HeuristicSelector;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic;
import KP.Item;
import KP.Knapsack;
import KP.KnapsackProblem;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic.Heuristic;
import Utils.Statistical;
import Utils.Timer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides the methods to solve the knapsack problem.
 * <p>
 * @author José Carlos Ortiz Bayliss (jcobayliss@gmail.com)
 * @version 1.0
 */
public class ConstructiveSolver implements FeatureManager {

    private final Knapsack knapsack;
    private final List<Item> items;
    final Timer timer;

    /**
     * Creates a new instance of <code>ConstructiveSolver</code>.
     * <p>
     * @param problem The instance of the knapsack problem to solve.
     * <p>
     */
    public ConstructiveSolver(KnapsackProblem problem) {
        knapsack = new Knapsack(problem.getCapacity());
        items = new ArrayList(problem.getNbItems());
        items.addAll(Arrays.asList(problem.getItems()));
        timer = new Timer();
    }

    /**
     * Solves the knapsack problem by using a specific constructive heuristic.
     * <p>
     * @param heuristic The constructive heuristic used to solve the knapsack
     * problem.
     * @return The knapsack that contains the solution to the problem.
     */
    public Knapsack solve(ConstructiveHeuristic heuristic) {
        Item item;
        timer.start(-1);
        item = heuristic.nextItem(knapsack, items);        
        while (item != null) {
            knapsack.pack(item);
            items.remove(item);
            item = heuristic.nextItem(knapsack, items);
        }
        return knapsack;
    }

    /**
     * Solves the knapsack problem by using a heuristic selector.
     * <p>
     * @param selector The heuristic selector used to solve the knapsack
     * problem.
     * @return The knapsack that contains the solution to the problem.
     */
    public Knapsack solve(HeuristicSelector selector) {
        ConstructiveHeuristic heuristic;
        Item item;
        heuristic = null;
//        solve(); // USING DYNAMIC PROGRAMMING
        try {
            heuristic = getHeuristic(selector.getHeuristic(this));
            timer.start(-1);
            item = heuristic.nextItem(knapsack, items);
            while (item != null) {
                knapsack.pack(item);
                items.remove(item);
                heuristic = getHeuristic(selector.getHeuristic(this));
                item = heuristic.nextItem(knapsack, items);
            }
        } catch (NoSuchHeuristicException e) {
            System.out.println(e);
            System.out.println("The system will halt.");
            System.exit(1);
        }
        return knapsack;
    }

    /**
     * Solves a given instance by using dynamic programming.
     * <p>
     * @return The knapsack with the packed items.
     */
    public Knapsack solve() {
        int row;
        double tmpProfit;
        double[][] table;
        Item item;
        timer.start(-1);
        /*
         * Produces the table.
         */
        table = new double[knapsack.getCapacity() + 1][items.size()];
        for (int i = 0; i < table[0].length; i++) {
            item = items.get(i);
            for (int rowCapacity = 0; rowCapacity < table.length; rowCapacity++) {
                if (item.getWeight() <= rowCapacity) {
                    tmpProfit = item.getProfit();
                    if (i == 0) {
                        table[rowCapacity][i] = tmpProfit;
                    } else {
                        table[rowCapacity][i] = (int) Math.max(table[rowCapacity][i - 1], tmpProfit + table[rowCapacity - item.getWeight()][i - 1]);
                    }
                } else {
                    if (i > 0) {
                        table[rowCapacity][i] = table[rowCapacity][i - 1];
                    }
                }
            }
        }
        /*
         * Interprets the table to produce a solution.
         */
        row = knapsack.getCapacity();
        for (int i = items.size() - 1; i > 0; i--) {
            if (table[row][i] == table[row][i - 1]) {
                // Do nothing.
            } else {
                item = items.remove(i);
                knapsack.pack(item);
                row = row - item.getWeight();
            }
        }
        if (table[row][0] != 0) {
            item = items.remove(0);
            knapsack.pack(item);
        }
        return knapsack;
    }

    /**
     * Returns the unpacked items in this solver.
     * <p>
     * @return The unpacked items in this solver.
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Returns the current capacity of this knapsack.
     * <p>
     * @return The current capacity of this knapsack.
     */
    public int getCapacity() {
        return knapsack.getCapacity();
    }

    /**
     * Returns the profit of this solution.
     * <p>
     * @return The profit of this solution.
     */
    public double getProfit() {
        return knapsack.getProfit();
    }

    /**
     * Returns the elapsed time since the search started.
     * <p>
     * @return The elapsed time since the search started.
     */
    public long getElapsedTime() {
        return timer.getElapsedTime();
    }

    /**
     * Returns the constructive heuristic that corresponds to the string
     * identifier provided.
     * <p>
     * @param heuristic The string identifier of the constructive heuristic to
     * retrieve.
     * @return The constructive heuristic that corresponds to the string
     * identifier provided.
     * @throws HERMES.Exceptions.NoSuchHeuristicException
     */
    public ConstructiveHeuristic getHeuristic(String heuristic) throws NoSuchHeuristicException {        
        switch (heuristic) {
            case "DEFAULT":
                return new ConstructiveHeuristic(Heuristic.DEFAULT);
            case "MAX_PROFIT":
                return new ConstructiveHeuristic(Heuristic.MAX_PROFIT);
            case "MAX_PROFIT_PER_WEIGHT_UNIT":
                return new ConstructiveHeuristic(Heuristic.MAX_PROFIT_PER_WEIGHT_UNIT);
            case "MIN_WEIGHT":
                return new ConstructiveHeuristic(Heuristic.MIN_WEIGHT);
            default:
                throw new NoSuchHeuristicException("Heuristic \'" + heuristic + "\' not recognized by the system.");
        }
    }

    @Override
    public double getFeature(String feature) throws NoSuchFeatureException {
        int i;
        double[] x, y;
        switch (feature) {
            case "NORM_MEAN_WEIGHT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getWeight();
                }
                return Statistical.mean(x) / Statistical.max(x);
            case "NORM_MEDIAN_WEIGHT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getWeight();
                }
                return Statistical.median(x) / Statistical.max(x);
            case "NORM_STD_WEIGHT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getWeight();
                }
                return Statistical.stdev(x) / Statistical.max(x);
            case "NORM_MEAN_PROFIT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getProfit();
                }
                return Statistical.mean(x) / Statistical.max(x);
            case "NORM_MEDIAN_PROFIT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getProfit();
                }
                return Statistical.median(x) / Statistical.max(x);
            case "NORM_STD_PROFIT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getProfit();
                }
                return Statistical.stdev(x) / Statistical.max(x);
            case "LQ_PROFIT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getProfit();
                }
                return Statistical.lowerQuartile(x);
            case "UQ_PROFIT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getProfit();
                }
                return Statistical.upperQuartile(x);
            case "LQ_WEIGHT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getWeight();
                }
                return Statistical.lowerQuartile(x);
            case "UQ_WEIGHT":
                i = 0;
                x = new double[items.size()];
                for (Item item : items) {
                    x[i++] = item.getWeight();
                }
                return Statistical.upperQuartile(x);
            case "NORM_CORRELATION":
                i = 0;
                x = new double[items.size()];
                y = new double[items.size()];
                for (Item item : items) {
                    x[i] = item.getWeight();
                    y[i++] = item.getProfit();
                }
                return Statistical.correlation(x, y) / 2 + 0.5;
            default:
                throw new NoSuchFeatureException("Feature \'" + feature + "\' is not recognized by the system.");
        }
    }

    @Override
    public String toString() {
        return knapsack.toString();
    }
}
