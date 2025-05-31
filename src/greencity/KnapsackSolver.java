package greencity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Dynamic‑programming 0–1 knapsack solver with integer weight scaling.
 * We round cost to whole lira (₺). For budgets ≤ 500 000 the DP table
 * is well under typical memory limits.
 */
public class KnapsackSolver {
    private static final Logger log = Logger.getLogger(KnapsackSolver.class.getName());

    /**
     * @param devices list of items
     * @param budgetTRY maximum spend in TRY
     * @return list of selected devices forming an optimal solution
     */
    public static List<Device> solve(List<Device> devices, double budgetTRY) {
        int n = devices.size();
        int W = (int) Math.round(budgetTRY); // weight capacity (₺)
        log.info("Running DP knapsack: n=" + n + ", budget=" + W + " lira");

        // dp[i][w] – best total objective using first i items with cost ≤ w
        double[][] dp = new double[n + 1][W + 1];
        boolean[][] take = new boolean[n + 1][W + 1];

        for (int i = 1; i <= n; i++) {
            Device d = devices.get(i - 1);
            int wt = (int) Math.round(d.costTRY());
            double val = d.objectiveValue();
            for (int w = 0; w <= W; w++) {
                if (wt <= w && dp[i - 1][w - wt] + val > dp[i - 1][w]) {
                    dp[i][w] = dp[i - 1][w - wt] + val;
                    take[i][w] = true;
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
            if (i % 50 == 0) {
                log.info("Processed " + i + " / " + n + " devices...");
            }
        }

        // reconstruct solution
        List<Device> chosen = new ArrayList<>();
        int w = W;
        for (int i = n; i >= 1; i--) {
            if (take[i][w]) {
                Device d = devices.get(i - 1);
                chosen.add(d);
                w -= (int) Math.round(d.costTRY());
            }
        }
        Collections.reverse(chosen);
        log.info("DP complete. Chosen devices: " + chosen.size());
        return chosen;
    }
}