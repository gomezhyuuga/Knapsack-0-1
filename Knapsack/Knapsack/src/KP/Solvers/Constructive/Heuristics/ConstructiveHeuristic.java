package KP.Solvers.Constructive.Heuristics;

import KP.Item;
import KP.Knapsack;
import java.util.List;

/**
 * Provides the methods to create and use constructive heuristics to solve knapsack problems.
 * <p>
 * @author José Carlos Ortiz Bayliss (jcobayliss@gmail.com)
 * @version 1.0
 */
public class ConstructiveHeuristic {

    /**
     * Defines the different ways a suitable item is selected to be packed.
     */
    public enum Heuristic {

        /**
         * Packs the next available item (default order).
         */
        DEFAULT,
        /**
         * Packs the item with the highest profit.
         */
        MAX_PROFIT,
        /**
         * Packs the item with the highest rate profit/weight.
         */
        MAX_PROFIT_PER_WEIGHT_UNIT,
        /**
         * Packs the item with the smallest weight.
         */
        MIN_WEIGHT

    }

    private final Heuristic heuristic;

    /**
     * Creates a new instance of <code>ConstructiveHeuristic</code>.
     * <p>
     * @param heuristic The selection heuristic to be used by this constructive heuristic.
     */
    public ConstructiveHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Returns a suitable item to place in the knapsack.
     * <p>
     * @param knapsack The knapsack where the item will be packed.
     * @param items The items remaining to be packed.
     * @return The next item to pack.    
     */
    public Item nextItem(Knapsack knapsack, List<Item> items) {
        double value;
        Item selected;
        selected = null;        
        switch (heuristic) {            
            case DEFAULT:                
                for (Item item : items) {
                    if (knapsack.canPack(item)) {
                        selected = item;
                        break;
                    }
                }               
                return selected;
            case MAX_PROFIT:                
                value = -Double.MAX_VALUE;
                for (Item item : items) {
                    if (knapsack.canPack(item) && item.getProfit() > value) {
                        selected = item;
                        value = selected.getProfit();
                    }
                }               
                return selected;
            case MAX_PROFIT_PER_WEIGHT_UNIT:
                value = -Double.MAX_VALUE;
                for (Item item : items) {                    
                    if (knapsack.canPack(item) && item.getProfitPerWeightUnit() > value) {
                        selected = item;
                        value = selected.getProfitPerWeightUnit();
                    }
                }                
                return selected;
            case MIN_WEIGHT:
                value = Double.MAX_VALUE;
                for (Item item : items) {
                    if (knapsack.canPack(item) && item.getWeight() < value) {
                        selected = item;
                        value = selected.getWeight();
                    }
                }                
                return selected;            
        }
        return null;
    }

    /**
     * Returns the string representation of this constructive heuristic.
     * <p>
     * @return The string representation of this constructive heuristic.
     */
    public String toString() {
        return heuristic.toString();
    }

}
