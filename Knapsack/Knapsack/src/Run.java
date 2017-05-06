
import KP.Item;
import KP.Solvers.Constructive.ConstructiveSolver;
import KP.KnapsackProblem;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic.Heuristic;
import KP.Solvers.HyperHeuristic.SampleHyperHeuristic;
import Utils.Statistical;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class Run {

    public static void main(String[] args) {

        String[] features, heuristics;
        KnapsackProblem problem;
        ConstructiveSolver solver;
        SampleHyperHeuristic hh;
        problem = new KnapsackProblem("../Instances/GA-DEFAULT_20_001.kp");

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

        List<Item> items = Arrays.stream(problem.getItems()).collect(Collectors.toList());
        System.out.println("ITEMS: " + items.size());

        // Eliminar los que no quepan
        List<NGKnapsack.NGItem> my_items = items.stream().map(i -> new NGKnapsack.NGItem(i.getProfit(), i.getWeight())).collect(Collectors.toList());
        NGKnapsack kk = new NGKnapsack(my_items, problem.getCapacity());
        kk.solve();
        System.out.println("MY DEFAULT: " + kk.getTotalProfit());
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
                if (this.items.isEmpty()) break;
                
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
                        => IF W >= STD_PROFIT && LOWER_Q <= W <= UPPER_Q, MED_PROFIT
                */
                List<NGItem> tmp = this.items.stream()
//                        .filter(i -> i.getProfit() > std_profit)
                        .filter(NGKnapsack.highValue(std_profit))
                        .filter(NGKnapsack.weightIQR(lowerQ_weight, upperQ_weight))
                        .collect(Collectors.toList());
                selectedItem = tmp.stream().max(NGKnapsack::maxProfit);
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
                
                // Default: MAX PROFIT
                packItem(this.items.stream().max(NGKnapsack::maxProfitWeight).get());
            }
        }
        /**
         * Has the item a high value?
         * TRUE if its value its above the STD
         * @param profit_std - STD of the profits
         * @return 
         */
        public static Predicate<NGItem> highValue(double profit_std) {
            return i -> i.getProfit() >= profit_std;
        }
        /**
         * Equals to:
         * .filter(i -> lowerQ_weight <= i.getWeight())
         * .filter(i -> i.getWeight() <= upperQ_weight)
         * @param lower_q - 1st Quartile
         * @param upper_q - 3rd Quartile
         * @return 
         */
        public static Predicate<NGItem> weightIQR(double lower_q, double upper_q) {
            return i -> i.getWeight()>= lower_q && i.getWeight() <= upper_q;
        }
        /**
         * Is the item too heavy to fit in the bag?
         * TRUE if the item's weight exceeds the capacity
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
