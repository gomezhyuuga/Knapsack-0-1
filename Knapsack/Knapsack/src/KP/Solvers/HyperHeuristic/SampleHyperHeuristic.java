package KP.Solvers.HyperHeuristic;

import HERMES.Exceptions.NoSuchFeatureException;
import HERMES.FeatureManager;
import HERMES.Selector.RuleBasedHeuristicSelector;

public class SampleHyperHeuristic extends RuleBasedHeuristicSelector {

    private int id;

    public SampleHyperHeuristic(String[] features, String[] heuristics) {
        super(features, heuristics);
        id = 0;
    }

    /*
     * This method is the core of the hyper-heuristic. do not forget to provide
     * your own implementation.
     */
    @Override
    public String getHeuristic(FeatureManager manager) {        
        /*
         * A naive hyper-heuristic (actually, a really poor one): This example
         * considers only one feature (the correlation between weight and
         * profit) and, when the corerlation is large (above 0.5) it changes to
         * a different heuristic.
         */
        String h;
        h = heuristics[id];                
        try {
            if (manager.getFeature(features[0]) > 0.5) {
                id++;
                if (id >= heuristics.length) {
                    id = 0;
                }
                return h;
            }
        } catch (NoSuchFeatureException exception) {
            System.err.println(exception);
            System.err.println("The system will halt.");
        }
        return h;
    }

}
