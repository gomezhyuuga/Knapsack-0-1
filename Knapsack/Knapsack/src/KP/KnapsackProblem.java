package KP;

import Utils.Files;
import java.text.DecimalFormat;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Provides the methods to create and use knapsack problems.
 * <p>
 * @author José Carlos Ortiz Bayliss (jcobayliss@gmail.com)
 * @version 2.0
 */
public class KnapsackProblem {

    private final int capacity;
    private final String fileName;
    private final Item[] items;

    /**
     * Creates a new instance of <code>KnapsackProblem</code>.
     * <p>
     * @param fileName The name of the file to initialize this instance.
     */
    public KnapsackProblem(String fileName) {
        int weight, i;
        double profit;
        String string;
        StringTokenizer fileTokenizer, lineTokenizer;
        string = Files.load(fileName);        
        fileTokenizer = new StringTokenizer(string, "\n");        
        lineTokenizer = new StringTokenizer(fileTokenizer.nextToken().trim(), ", \t");
        items = new Item[Integer.parseInt(lineTokenizer.nextToken())];
        capacity = Integer.parseInt(lineTokenizer.nextToken());
        //items = new Item[20];
        //capacity = 50;
        /*
         * Reads the information about the items.
         */
        i = 0;        
        while (fileTokenizer.hasMoreTokens()) {
            lineTokenizer = new StringTokenizer(fileTokenizer.nextToken().trim(), ", \t");
            weight = Integer.parseInt(lineTokenizer.nextToken().trim());            
            profit = Double.parseDouble(lineTokenizer.nextToken().trim());            
            items[i] = new Item(i, profit, weight);
            i++;
        }
        this.fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
    }    
    
    /**
     * Creates a new instance of <code>KnapsackProblem</code>.
     * <p>
     * @param items
     * @param capacity
     */
    public KnapsackProblem(List<Item> items, int capacity) {
        int i;
        this.capacity = capacity;
        this.items = new Item[items.size()];
        i = 0;
        for (Item item : items) {
            this.items[i++] = item;
        }
        fileName =  "Not provided";
    }

    /**
     * Returns the capacity of the knapsack in this knapsack problem.
     * <p>
     * @return The capacity of the knapsack in this knapsack problem.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the number of items in this knapsack problem.
     * <p>
     * @return The number of items in this knapsack problem.
     */
    public int getNbItems() {
        return items.length;
    }

    /**
     * Returns the items in this knapsack problem.
     * <p>
     * @return The items in this knapsack problem.
     */
    public Item[] getItems() {
        Item[] tmp;
        tmp = new Item[items.length];
        System.arraycopy(items, 0, tmp, 0, tmp.length);
        return tmp;
    }

    /**
     * Returns the unique identifier of this instance.
     * <p>
     * @return The unique identifier of this instance.
     */
    public String getId() {
        return fileName;
    }
    
    /**
     * Saves this knapsack problem into a text file.
     * <p>
     * @param fileName The name of the file where the instance is to be saved.
     */
    public void save(String fileName) {
        StringBuilder string;
        DecimalFormat format;
        string = new StringBuilder();        
        string.append(items.length).append(", ").append(capacity).append("\r\n");
        format = new DecimalFormat("0.000");
        for (Item item : items) {
            string.append(item.getWeight()).append(", ").append(format.format(item.getProfit())).append("\r\n");
        }
        Utils.Files.save(string.toString().trim(), fileName);
    }
    
    /**
     * Returns the string representation of this knapsack problem.
     * <p>
     * @return The string representation of this knapsack problem.
     */
    public String toString() {
        StringBuilder string;
        string = new StringBuilder();
        string.append(items.length).append(", ").append(capacity).append("\n");
        for (Item item : items) {
            string.append(item.toString()).append("\n");
        }
        return string.toString().trim();
    }

}
