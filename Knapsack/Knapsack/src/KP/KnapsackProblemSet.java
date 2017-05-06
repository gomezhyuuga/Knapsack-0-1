package KP;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Provides the methods to create and handle sets of knapsack problem instances.
 * <p>
 * @author Jose Carlos Ortiz Bayliss (jcobayliss@gmail.com)
 * @version 1.0
 */
public class KnapsackProblemSet {
    
    private final KnapsackProblem[] instances;
    
    /**
     * Defines the fraction of the set that will be used according to the purpose of the instances.
     */
    public enum Subset {

        /**
         * A subset of problems is used for training purposes.         
         */
        TRAIN,
        /**
         * A subset of instances is used for testing purposes.
         */
        TEST
    }
    
    /**
     * Creates a new instance of <code>KnapsackProblemSet</code>.
     * <p>
     * @param folderName The name of the folder where the instances are contained.     
     */
    public KnapsackProblemSet(String folderName) {
        this(folderName, Subset.TEST, 1.0, 0);
    }
    
    /**
     * Creates a new instance of <code>KnapsackProblemSet</code>.
     * <p>
     * @param folderName The name of the folder where the instances are contained.     
     * @param type The type of set to be created (training or test).
     * @param proportion The proportion of the instances used for training.
     * @param seed The seed to initialize the random number generator.
     */
    public KnapsackProblemSet(String folderName, Subset type, double proportion, long seed) {
        int i, n;
        List<String> fileNames;
        File file = new File(folderName);
        if (!file.exists() || !file.isDirectory()) {
            System.err.println("The path \'" + folderName + "\'is not a valid directory.");
            System.err.println("The system will halt.");
            System.exit(1);
        }
        fileNames = Arrays.asList(file.list());
        Collections.sort(fileNames);
        if (proportion != 1.0) {
            n = (int) Math.ceil(proportion * fileNames.size());
            Collections.shuffle(fileNames, new Random(seed));
            if (type == Subset.TRAIN) {
                fileNames = fileNames.subList(0, n);
            } else {
                fileNames = fileNames.subList(n, fileNames.size());
            }
        }
        i = 0;
        instances = new KnapsackProblem[fileNames.size()];
        for (String fileName : fileNames) {
            System.out.print("Loading \'" + folderName + "/" + fileName + "\'...");            
            instances[i++] = new KnapsackProblem(folderName + "/" + fileName);            
            System.out.println(" done.");
        }
    }
    
    /**
     * Returns the size of this set.
     * <p>
     * @return The size of this set. The size of the set is defined as the number of instances
     * contained.
     */
    public int getSize() {
        return instances.length;
    }

    /**
     * Returns the instances in this set.
     * <p>
     * @return The instances in this set.
     */
    public KnapsackProblem[] getInstances() {
        return instances;
    }
    
}
