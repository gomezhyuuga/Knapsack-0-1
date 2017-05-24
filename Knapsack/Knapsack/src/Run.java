
import KP.Item;
import KP.Solvers.Constructive.ConstructiveSolver;
import KP.KnapsackProblem;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic;
import KP.Solvers.Constructive.Heuristics.ConstructiveHeuristic.Heuristic;
import KP.Solvers.HyperHeuristic.SampleHyperHeuristic;
import Utils.Statistical;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class Run {
    
    public static int START = 0;
    public static int END = 14;
    
    public static void main(String[] args) {
//        System.out.println("Training...");
//        run(0, 14);
        System.out.println("Testing...");
        run(100, 200);
    }
    
    public static void run(int start, int end) {
        String[] features, heuristics;
        KnapsackProblem problem;
        ConstructiveSolver solver;
        SampleHyperHeuristic hh;

        ArrayList<KnapsackProblem> problems = new ArrayList();

        for (int index = start; index < end; index++) {
            String instanceName = String.format("_50_%03d.kp", index);

            KnapsackProblem defProblem = new KnapsackProblem("../Instances/Test I/GA-MaxDefault" + instanceName);
            KnapsackProblem maxProblem = new KnapsackProblem("../Instances/Test I/GA-MaxProfit" + instanceName);
            KnapsackProblem maxPWProblem = new KnapsackProblem("../Instances/Test I/GA-MaxProfitPerWeight" + instanceName);
            KnapsackProblem minProblem = new KnapsackProblem("../Instances/Test I/GA-MinWeight" + instanceName);

            problems.add(defProblem);
            problems.add(maxProblem);
            problems.add(maxPWProblem);
            problems.add(minProblem);
        }
        WinnerTable winners = new WinnerTable(problems.size());

        List<NGKnapsack.Rule> rules = new ArrayList<>();

        for (KnapsackProblem p : problems) {
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
//            System.out.println("Profit with default: " + profit);

            profit = use(defProblem, Heuristic.MIN_WEIGHT);
            results.put(Method.MIN_WEIGHT, profit);
//            System.out.println("Profit with min-weight: " + profit);

            profit = use(defProblem, Heuristic.MAX_PROFIT);
            results.put(Method.MAX_PROFIT, profit);
//            System.out.println("Profit with max-profit: " + profit);

            profit = use(defProblem, Heuristic.MAX_PROFIT_PER_WEIGHT_UNIT);
            results.put(Method.MAX_PROFIT_PER_WEIGHT_UNIT, profit);
//            System.out.println("Profit with max-profit/weight: " + profit);

            hh = new SampleHyperHeuristic(features, heuristics);
            solver = new ConstructiveSolver(defProblem);
            profit = solver.solve(hh).getProfit();
            results.put(Method.SIMPLE_HYPER, profit);
//            System.out.println("Profit with hyper-heuristic: " + profit);

            List<Item> items = Arrays.stream(defProblem.getItems()).collect(Collectors.toList());
            List<NGKnapsack.NGItem> my_items = items.stream().map(i -> new NGKnapsack.NGItem(i.getProfit(), i.getWeight())).collect(Collectors.toList());
            NGKnapsack kk = new NGKnapsack(my_items, defProblem.getCapacity());
            kk.solve();
            profit = kk.getTotalProfit();
            results.put(Method.HR_HYPER, profit);
            String instanceName = defProblem.getId();
            System.out.println(instanceName + ", " + profit);

            // Get Winner
            // If there is a tie, then all get incremented
            Optional<Double> winner = results.values().stream().max(Double::compare);
            results.forEach((m, p2) -> {
                if (p2.equals(winner.get())) {
                    winners.setWinner(m);
                }
            });
            winners.addRules(kk.getRules());
        }

        System.out.println(winners);
    }

    public enum Method {
        DEFAULT,
        MAX_PROFIT,
        MAX_PROFIT_PER_WEIGHT_UNIT,
        MIN_WEIGHT,
        SIMPLE_HYPER,
        HR_HYPER
    }

    public static class WinnerTable {

        private final Map<Method, Integer> winners;
        private final int n;
        private ArrayList<NGKnapsack.Rule> rules;

        public WinnerTable(int n) {
            this.n = n;
            winners = new HashMap<>();
            Method[] vals = Method.values();
            for (Method val : vals) {
                winners.put(val, 0);
            }
            this.rules = new ArrayList<>();
        }

        public void setWinner(Method m) {
            int times = winners.get(m);
            winners.put(m, times + 1);
        }

        public void addRules(List<NGKnapsack.Rule> rules) {
            this.rules.addAll(rules);
        }

        public double getScore(Method m) {
            return getWins(m) * 100.0 / this.n;
        }

        public int getWins(Method m) {
            return this.winners.get(m);
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            String ruler = String.join("", Collections.nCopies(45, "=")) + "\n";
            DecimalFormat decim = new DecimalFormat("00.000");
            s.append(ruler);
            s.append(String.format("%30s\n", "TABLE OF RESULTS"));
            s.append(ruler);
            for (Method m : Method.values()) {
                s.append(String.format("%45s", String.format("%s: (%02d/%d) %s%% \n",
                        m.toString(),
                        getWins(m),
                        this.n,
                        decim.format(getScore(m)))));
            }
            s.append(ruler);
            s.append(String.format("%28s\n", "RULES USED"));
            s.append(ruler);
            Map<String, List<NGKnapsack.Rule>> rr = rules.stream().collect(Collectors.groupingBy((t) -> {
                return t.name();
            }));
            int size = rules.size();
            for (Map.Entry<String, List<NGKnapsack.Rule>> entry : rr.entrySet()) {
                String key = entry.getKey();
                List<NGKnapsack.Rule> value = entry.getValue();
                int times = value.size();
                s.append(String.format("%10s: (%03d/%d) %s%%\n",
                        key,
                        times,
                        size,
                        decim.format(times * 100.0 / size)));
            }
            s.append(ruler);
            return s.toString();
        }
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

            public double getRatio() {
                return getProfit() / getWeight();
            }

            @Override
            public String toString() {
                return "(W " + weight + ", P " + profit + ")";
            }

            public static NGItem maxProfit(NGItem i, NGItem j) {
                return (i.getProfit() > j.getProfit()) ? i : j;
            }
        }

        public enum Rule {
            REMOVE,
            RULE1,
            RULE2,
            MAXPROFIT
        }

        public double capacity = 0.0;
        public List<NGItem> items;
        public List<NGItem> packedItems;
        public List<Rule> rules;

        public NGKnapsack(List<NGItem> items, double capacity) {
            this.rules = new ArrayList<>();
            this.capacity = capacity;
            this.items = items;
            this.packedItems = new ArrayList<>();
        }

        public void removeItem(NGItem item) {
            this.items.remove(item);
        }

        public void packItem(NGItem item, Rule rule) {
            this.packedItems.add(item);
            this.rules.add(rule);
            this.items.remove(item);
            this.capacity -= item.getWeight();
        }

        public double[] getValues(ToDoubleFunction<NGItem> fn) {
            return this.items.stream().mapToDouble(fn).toArray();
        }

        public List<Rule> getRules() {
            return this.rules;
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
        public double getProfitMedian() {
            return Statistical.median(getValues(NGItem::getProfit));
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
                int size = this.items.size();
                this.items.removeIf(overweight(this.capacity));
                if (this.items.size() != size) {
                    rules.add(Rule.REMOVE);
                }
                if (this.items.isEmpty()) {
                    break;
                }

                Optional<NGItem> selectedItem;
//                double mean_weight = getWeightMean();
                double mean_profit = getProfitMean();
//                double median_profit = getProfitMedian();
//                double std_weight = getWeightSTD();
                double std_profit = getProfitSTD();
                double upperQ_weight = getWeightUpperQ();
//                double upperQ_profit = getProfitUpperQ();
                double lowerQ_weight = getWeightLowerQ();
//                double lowerQ_profit = getProfitLowerQ();
                
                // RULE 2
                Optional<NGItem> i1 = this.items.stream()
                        .filter(NGKnapsack.lowWeight(lowerQ_weight))
                        .max(NGKnapsack::maxProfit);
                Optional<NGItem> i2 = this.items.stream()
                        .filter(NGKnapsack.weightIQR(lowerQ_weight, upperQ_weight))
                        .max(NGKnapsack::maxProfitWeight);
                if (i1.isPresent() && i2.isPresent()) {
                    NGItem ii = i1.get().getProfit() > i2.get().getProfit() ? i1.get() : i2.get();
                    packItem(ii, Rule.RULE1);
                    continue;
                }

                /*
                    RULE 1
                    5. Si el valor es alto (mayor STD) y su peso se encuentra dentro de la (STD), agregar
                        => IF W >= STD_PROFIT && LOWER_Q <= W <= UPPER_Q, GRAB MAXPROFIT_WEIGHT
                 */
                selectedItem = this.items.stream()
                        .filter(NGKnapsack.highValue(mean_profit + std_profit))
                        .filter(NGKnapsack.weightIQR(lowerQ_weight, upperQ_weight))
                        .max(NGKnapsack::maxProfitWeight);

                if (selectedItem.isPresent()) {
                    packItem(selectedItem.get(), Rule.RULE2);
                    continue;
                }
                
                NGItem iToPack = this.items.stream().max(NGKnapsack::maxProfit).get();
                // Default: MAX PROFIT
                packItem(iToPack, Rule.MAXPROFIT);
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
         * Equals to:
         * .filter(i -> lowerQ_weight <= i.getWeight())
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

        public static int maxWeight(NGItem i, NGItem j) {
            return Double.compare(i.getWeight(), j.getWeight());
        }

        public static int maxProfitWeight(NGItem i, NGItem j) {
            double pWi = i.getProfit() / i.getWeight();
            double pWj = j.getProfit() / j.getWeight();
            return Double.compare(pWi, pWj);
        }
    }
}
