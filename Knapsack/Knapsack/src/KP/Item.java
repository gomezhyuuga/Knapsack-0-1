package KP;

/**
 * Provides the methods to create and use items for the 0-1 knapsack problem.
 * <p>
 * @author José Carlos Ortiz Bayliss (jcobayliss@gmail.com)
 * @version 1.0
 */
public class Item {

    private final int id, weight;
    private final double profit;
    
    /**
     * Creates a new instance of <code>Item</code>.
     * <p>
     * @param id The identifier of this item.
     * @param profit The profit of this item.
     * @param weight The weight of this item.
     */
    public Item(int id, double profit, int weight) {
        this.id = id;
        this.profit = profit;
        this.weight = weight;        
    }
    
    /**
     * Returns the identifier of this item.
     * <p>
     * @return The identifier of this item.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the profit of this item.
     * <p>
     * @return The profits of this item.
     */
    public double getProfit() {
        return profit;
    }
    
    /**
     * Returns the weight of this item.
     * <p>
     * @return The weight of this item.
     */
    public int getWeight() {
        return weight;
    }
    
    /**
     * Returns the profit per weight unit of this item.
     * <p>
     * @return The profit per weight unit of this item.
     */
    public double getProfitPerWeightUnit() {        
        return profit / weight;
    }
    
    /**
     * Returns the string representation of this item.
     * <p>
     * @return The string representation of this item.
     */
    public String toString() {
        StringBuilder string;
        string = new StringBuilder();
        string.append("(").append(id).append(", ").append(weight).append(", ").append(profit).append(")");
        return string.toString();
    }    
    
}
