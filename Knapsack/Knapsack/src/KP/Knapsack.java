package KP;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides the methods to create and use knapsacks for the 0-1 knapsack problem.
 * <p>
 * @author José Carlos Ortiz Bayliss (jcobayliss@gmail.com)
 * @version 1.0
 */
public class Knapsack {

    private int capacity;
    private double profit;    
    private final List<Item> items;

    /**
     * Creates a new instance of <code>Knapsack</code>.
     * <p>     
     * @param capacity The capacity of this knapsack.     
     */
    public Knapsack(int capacity) {        
        this.capacity = capacity;
        profit = 0;
        items = new LinkedList();
    }

    /**
     * Creates a new instance of <code>Knapsack</code> from an existing instance (copy constructor).
     * <p>
     * @param knapsack The instance of <code>Knapsack</code> to copy to this instance.
     */
    public Knapsack(Knapsack knapsack) {        
        capacity = knapsack.capacity;
        profit = knapsack.profit;
        items = new LinkedList(knapsack.items);
    }

    /**
     * Returns the current capacity of this knapsack.
     * <p>
     * @return The current capacity of this knapsack.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the current profit of this knapsack.
     * <p>
     * @return The current profit of this knapsack.
     */
    public double getProfit() {        
        return profit;
    }

    public Item[] getItems() {        
        return items.toArray(new Item[items.size()]);        
    }
    
    /**
     * Revises if the item provided can be packed in this knapsack.
     * <p>
     * @param item The item to be packed.
     * @return <code>true</code> if the item can be packed in this knapsack, <code>false</code>
     * otherwise.
     */
    public boolean canPack(Item item) {
        return item.getWeight() <= getCapacity();
    }

    /**
     * Packs an item into this knapsack.
     * <p>
     * @param item The item to pack.
     * @return <code>true</code> if the item was successfully packed, <code>false</code> otherwise.
     */
    public boolean pack(Item item) {        
        if (item.getWeight() <= capacity) {
            items.add(item);
            capacity -= item.getWeight();
            profit += item.getProfit();            
            return true;
        }
        return false;
    }
    
    /**
     * Returns the string representation of this knapsack.
     * <p>
     * @return The string representation of this knapsack.
     */
    public String toString() { 
        StringBuilder string;
        string = new StringBuilder();
        for (Item item : items) {
            string.append(item.toString()).append(" ");
        }
        return string.toString().trim();
    }

}
