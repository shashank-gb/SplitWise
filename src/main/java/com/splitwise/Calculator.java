package com.splitwise;

import com.splitwise.models.Edge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Calculator {

    static List<Edge> getFinalSettlements(Map<String, Double> g, Map<String, Double> r) {
        List<Edge> finalSettlement = new ArrayList<>();
        for (Map.Entry<String, Double> entry : g.entrySet()) {
            double target = entry.getValue();
            List<Map.Entry<String, Double>> match = findSumOfSubset(r, target);
            if (match != null) {
                for (Map.Entry<String, Double> debtor : match) {
                    finalSettlement.add(new Edge(entry.getKey(), debtor.getKey(), debtor.getValue()));
                    r.remove(debtor.getKey());
                }
            } else {
                System.out.println("No subset");
                return null;
            }
        }
        return finalSettlement;
    }

    static List<Map.Entry<String, Double>> findSumOfSubset(Map<String, Double> r, double target) {
        List<Map.Entry<String, Double>> entries = new ArrayList<>(r.entrySet());
        int n = entries.size();
        int total = 1 << n;

        for (int mask = 1; mask < total; mask++) {
            double sum = 0.0;
            List<Map.Entry<String, Double>> subset = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    Map.Entry<String, Double> entry = entries.get(i);
                    sum += entry.getValue();
                    subset.add(entry);
                }
            }

            if (Math.abs(sum + target) < 1e-6) {
                return subset;
            }
        }
        return null;
    }

}
