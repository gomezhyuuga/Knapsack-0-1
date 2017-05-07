
import KP.Item;
import KP.Solvers.Constructive.ConstructiveSolver;
import KP.KnapsackProblem;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic.Heuristic;
import KP.Solvers.HyperHeuristic.SampleHyperHeuristic;
import Utils.Statistical;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Run {

    public enum Method {
        DEFAULT,
        MAX_PROFIT,
        MAX_PROFIT_PER_WEIGHT_UNIT,
        MIN_WEIGHT,
        SIMPLE_HYPER,
        HYUUGA_HYPER
    }

    public static class WinnerTable {

        private Map<Method, Integer> winners;
        private int n;

        public WinnerTable(int n) {
            this.n = n;
            winners = new HashMap<>();
            Method[] vals = {
                Method.DEFAULT,
                Method.MAX_PROFIT,
                Method.MAX_PROFIT_PER_WEIGHT_UNIT,
                Method.MIN_WEIGHT,
                Method.SIMPLE_HYPER,
                Method.HYUUGA_HYPER
            };
            for (int i = 0; i < vals.length; i++) {
                winners.put(vals[i], 0);
            }
        }

        public void setWinner(Method m) {
            int times = winners.get(m);
            winners.put(m, times + 1);
        }

        @Override
        public String toString() {
            return this.winners.toString();
        }
    }

    public static void main(String[] args) {

        String[] features, heuristics;
        int TEST = 18;
        KnapsackProblem problem;
        ConstructiveSolver solver;
        SampleHyperHeuristic hh;

        WinnerTable winners = new WinnerTable(TEST);
        System.out.println(winners);
        ArrayList<KnapsackProblem> problems = new ArrayList();

        for (int index = 0; index < TEST; index++){
            String instanceName = String.format("_20_%03d.kp", index);

            KnapsackProblem defProblem = new KnapsackProblem("../Instances/GA-DEFAULT" + instanceName);
            KnapsackProblem maxProblem = new KnapsackProblem("../Instances/GA-MAXPROFIT" + instanceName);
            KnapsackProblem maxPWProblem = new KnapsackProblem("../Instances/GA-MAXPROFITWEIGHT" + instanceName);
            KnapsackProblem minProblem = new KnapsackProblem("../Instances/GA-MINWEIGHT" + instanceName);
            
            problems.add(defProblem);
            problems.add(maxProblem);
            problems.add(maxPWProblem);
            problems.add(minProblem);
        }
        
        System.out.println(problems.size());

        for (KnapsackProblem p: problems) {
            Map<Method, Double> results = new HashMap<>();

            
            KnapsackProblem defProblem = p;
            features = new String[]{
                "NORM_MEAN_WEIGHT",
                "NORM_MEDIAN_WEIGHT",
                "NORM_STD_WEIGHT",
                "NORM_MEAN_PROFIT",
                "NORM_MEDIAN_PROFIT",
                "NORM_STD_PROFIT",
                "NORM_CORRELATION",
                "LQ_PROFIT",
                "LQ_WEIGHT",
                "UQ_WEIGHT",
                "UQ_PROFIT"
            };
            heuristics = new String[]{
                "DEFAULT",
                "MIN_WEIGHT",
                "MAX_PROFIT",
                "MAX_PROFIT_PER_WEIGHT_UNIT"
            };
            double profit;

            profit = use(defProblem, Heuristic.DEFAULT);
            results.put(Method.DEFAULT, profit);
            System.out.println("Profit with default: " + profit);

            profit = use(defProblem, Heuristic.MIN_WEIGHT);
            results.put(Method.MIN_WEIGHT, profit);
            System.out.println("Profit with min-weight: " + profit);

            profit = use(defProblem, Heuristic.MAX_PROFIT);
            results.put(Method.MAX_PROFIT, profit);
            System.out.println("Profit with max-profit: " + profit);

            profit = use(defProblem, Heuristic.MAX_PROFIT_PER_WEIGHT_UNIT);
            results.put(Method.MAX_PROFIT_PER_WEIGHT_UNIT, profit);
            System.out.println("Profit with max-profit/weight: " + profit);

            hh = new SampleHyperHeuristic(features, heuristics);
            solver = new ConstructiveSolver(defProblem);
            profit = solver.solve(hh).getProfit();
            results.put(Method.SIMPLE_HYPER, profit);
            System.out.println("Profit with hyper-heuristic: " + profit);

            List<Item> items = Arrays.stream(defProblem.getItems()).collect(Collectors.toList());
            List<NGKnapsack.NGItem> my_items = items.stream().map(i -> new NGKnapsack.NGItem(i.getProfit(), i.getWeight())).collect(Collectors.toList());
            NGKnapsack kk = new NGKnapsack(my_items, defProblem.getCapacity());
            kk.solve();
            profit = kk.getTotalProfit();
            System.out.println("HYUUGA: " + profit);
            results.put(Method.HYUUGA_HYPER, profit);

            // Get Winner
            // If there is a tie, then all get incremented
            Optional<Double> winner = results.values().stream().max(Double::compare);
            results.forEach((m, p2) -> {
                if (p2.equals(winner.get())) {
                    winners.setWinner(m);
                }
            });
        }
        System.out.println(winners);
        
        
    }

    public static double use(KnapsackProblem problem, Heuristic heuristic) {
        return new ConstructiveSolver(problem)
                .solve(new ConstructiveHeuristic(heuristic))
                .getProfit();
    }

    public static class NGKnapsack {

        public static class NGItem {

            public double profit;
            public double weight;

            public NGItem(double profit, double weight) {
                this.profit = profit;
                this.weight = weight;
            }

            public double getProfit() {
                return this.profit;
            }

            public double getWeight() {
                return this.weight;
            }

            public String toString() {
                return "(W " + weight + ", P " + profit + ")";
            }
        }

        public double capacity = 0.0;
        public List<NGItem> items;
        public List<NGItem> packedItems;

        public NGKnapsack(List<NGItem> items, double capacity) {
            this.capacity = capacity;
            this.items = items;
            this.packedItems = new ArrayList<>();
        }

        public void removeItem(NGItem item) {
            this.items.remove(item);
        }

        public void packItem(NGItem item) {
            this.packedItems.add(item);
            this.items.remove(item);
            this.capacity -= item.getWeight();
        }

        public double[] getValues(ToDoubleFunction<NGItem> fn) {
            return this.items.stream().mapToDouble(fn).toArray();
        }

        public double getWeightSTD() {
            return Statistical.stdev(getValues(NGItem::getWeight));
        }

        public double getProfitSTD() {
            return Statistical.stdev(getValues(NGItem::getProfit));
        }

        public double getProfitMean() {
            return Statistical.mean(getValues(NGItem::getProfit));
        }

        public double getWeightMean() {
            return Statistical.mean(getValues(NGItem::getWeight));
        }

        public double getWeightUpperQ() {
            return Statistical.upperQuartile(getValues(NGItem::getWeight));
        }

        public double getProfitUpperQ() {
            return Statistical.upperQuartile(getValues(NGItem::getProfit));
        }

        public double getWeightLowerQ() {
            return Statistical.lowerQuartile(getValues(NGItem::getWeight));
        }

        public double getProfitLowerQ() {
            return Statistical.lowerQuartile(getValues(NGItem::getProfit));
        }

        public double getTotalProfit() {
            return this.packedItems.stream().mapToDouble(NGItem::getProfit).sum();
        }

        public void solve() {
            int count = 0;
            while (!items.isEmpty()) {
                // Remove items that doesn't fit
                this.items.removeIf(overweight(this.capacity));
                if (this.items.isEmpty()) {
                    break;
                }

                Optional<NGItem> selectedItem;
                double mean_weight = getWeightMean();
                double mean_profit = getProfitMean();
                double std_weight = getWeightSTD();
                double std_profit = getProfitSTD();
                double upperQ_weight = getWeightUpperQ();
                double upperQ_profit = getProfitUpperQ();
                double lowerQ_weight = getWeightLowerQ();
                double lowerQ_profit = getProfitLowerQ();

                /*
                    RULE 1
                    5. Si el valor es alto (mayor STD) y su peso se encuentra dentro de la (STD), agregar
                        => IF W >= STD_PROFIT && LOWER_Q <= W <= UPPER_Q, GRAB MAXPROFIT_WEIGHT
                 */
                List<NGItem> tmp;
                tmp = this.items.stream()
                        .filter(NGKnapsack.highValue(std_profit))
                        .filter(NGKnapsack.weightIQR(lowerQ_weight, upperQ_weight))
                        .collect(Collectors.toList());
                selectedItem = tmp.stream().max(NGKnapsack::maxProfitWeight);
//                tmp.stream().forEach(System.out::println);
//                System.out.println("--- COUNT: " + tmp.size() + " SELECTED " + selectedItem);
//                List<NGItem> others = this.items.stream().filter(i -> !tmp.contains(i)).collect(Collectors.toList());
//                System.out.println("OTHER ITEMS: ");
//                others.forEach(System.out::println);
//                System.out.println("----");

                if (selectedItem.isPresent()) {
                    packItem(selectedItem.get());
                    continue;
                }
                // There is no item that matches RULE 1
                // RULE 2
//                tmp = this.items.stream()
//                        .filter(NGKnapsack.lowWeight(lowerQ_weight))
//                        .filter(NGKnapsack.profitIQR(lowerQ_profit, upperQ_profit))
//                        .collect(Collectors.toList());
//                selectedItem = tmp.stream().max(NGKnapsack::maxProfitWeight);
//                if (selectedItem.isPresent()) {
//                    packItem(selectedItem.get());
//                    continue;
//                }

                // Default: MAX PROFIT
                System.out.println("CANT FIND RULE");
                packItem(this.items.stream().max(NGKnapsack::maxProfit).get());
            }
        }

        /**
         * Has the item a high value? TRUE if its value its above the STD
         *
         * @param profit_std - STD of the profits
         * @return
         */
        public static Predicate<NGItem> highValue(double profit_std) {
            return i -> i.getProfit() > profit_std;
        }

        public static Predicate<NGItem> lowWeight(double weight_std) {
            return i -> i.getWeight() < weight_std;
        }

        /**
         * Equals to: .filter(i -> lowerQ_weight <= i.getWeight())
         * .filter(i -> i.getWeight() <= upperQ_weight) @para
         *
         *
         * @param lower_q - 1st Quartile
         * @param upper_q - 3rd Quartile
         * @return
         */
        public static Predicate<NGItem> weightIQR(double lower_q, double upper_q) {
            return i -> i.getWeight() >= lower_q && i.getWeight() <= upper_q;
        }

        public static Predicate<NGItem> profitIQR(double lower_q, double upper_q) {
            return i -> i.getProfit() >= lower_q && i.getProfit() <= upper_q;
        }

        /**
         * Is the item too heavy to fit in the bag? TRUE if the item's weight
         * exceeds the capacity
         *
         * @param capacity - Current capacity of the bag
         * @return
         */
        public static Predicate<NGItem> overweight(double capacity) {
            return p -> p.getWeight() > capacity;
        }

        // HEURISTICS
        public static int maxProfit(NGItem i, NGItem j) {
            return Double.compare(i.getProfit(), j.getProfit());
        }

        public static int minWeight(NGItem i, NGItem j) {
            return Double.compare(i.getWeight(), j.getWeight()) * -1;
        }

        public static int maxProfitWeight(NGItem i, NGItem j) {
            double pWi = i.getProfit() / i.getWeight();
            double pWj = j.getProfit() / j.getWeight();
            return Double.compare(pWi, pWj);
        }
    }
}
