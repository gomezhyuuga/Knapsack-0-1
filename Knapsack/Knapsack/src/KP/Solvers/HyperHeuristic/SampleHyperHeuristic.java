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
//        features = new String[] {
//            "NORM_MEAN_WEIGHT",
//            "NORM_MEDIAN_WEIGHT",
//            "NORM_STD_WEIGHT",
//            "NORM_MEAN_PROFIT",
//            "NORM_MEDIAN_PROFIT",
//            "NORM_STD_PROFIT",
//            "NORM_CORRELATION",
//        };
//uristics = new String[]{
//            "DEFAULT",
//            "MIN_WEIGHT",
//            "MAX_PROFIT",
//            "MAX_PROFIT_PER_WEIGHT_UNIT"
//        };
        try {
//            double mean_weight = manager.getFeature("NORM_MEAN_WEIGHT");
//            double std_weight = manager.getFeature("NORM_STD_WEIGHT");
//            double upper_quartile_weight = manager.getFeature("UQ_WEIGHT");
//            double lower_quartile_weight = manager.getFeature("LQ_WEIGHT");
//            
//            double mean_profit = manager.getFeature("NORM_MEAN_PROFIT");
//            double std_profit = manager.getFeature("NORM_STD_PROFIT");
//            double upper_quartile_profit = manager.getFeature("UQ_PROFIT");
//            double lower_quartile_profit = manager.getFeature("LW_PROFIT");
//            if (manager.getFeature("ACUM") == 1) {
//                return "MAX_PROFIT";
//            }
//            if (manager.getFeature(h))
            
//            if (manager.getFeature("NORM_STD_PROFIT") > 0.5 &&
//                    manager.getFeature("NORM_STD_WEIGHT") == 0.5) {
//                
//            }
            
            if (manager.getFeature(features[0]) > 0.5) {
                id++;
                if (id >= heuristics.length) {
                    id = 0;
                }
                return h;
            }
            return h;
        } catch (NoSuchFeatureException exception) {
            System.err.println(exception);
            System.err.println("The system will halt.");
        }
        return h;
    }

}
