package com.splitwise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splitwise.models.DirectedGraph;
import com.splitwise.models.Edge;
import com.splitwise.models.GroupReport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        File folder = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("test_cases")).getFile());
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        for (File file : files) {

            ObjectMapper mapper = new ObjectMapper();
            GroupReport report = mapper.readValue(file, GroupReport.class);

            DirectedGraph graph = new DirectedGraph();

            graph.addEdge("Gabe", "Bob", 30);
            graph.addEdge("Gabe", "David", 10);
            graph.addEdge("Fred", "Bob", 10);
            graph.addEdge("Fred", "Charlie", 30);
            graph.addEdge("Fred", "David", 10);
            graph.addEdge("Fred", "Ema", 10);
            graph.addEdge("Bob", "Charlie", 40);
            graph.addEdge("Charlie", "David", 20);
            graph.addEdge("David", "Ema", 50);

            System.out.println("===========Graph==============");
            graph.print();
            System.out.println("==============================");
            System.out.println();

            Map<String, Double> netBalances = graph.calculateNetBalances();
            System.out.println("=========Net Balances=========");
            netBalances.forEach((k, v) -> System.out.printf("• %-10s -> %10s \n", k, v));
            System.out.println("==============================");

            Map<String, Double> givers = netBalances.entrySet()
                    .stream().filter(entry -> entry.getValue() > 0.0)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Map<String, Double> receivers = netBalances.entrySet()
                    .stream().filter(entry -> entry.getValue() < 0.0)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            System.out.println("=============Givers===========");
            givers.forEach((k, v) -> System.out.printf("• %-10s -> %10s \n", k, v));
            System.out.println("==============================");

            System.out.println("===========Receivers===========");
            receivers.forEach((k, v) -> System.out.printf("• %-10s -> %10s \n", k, v));
            System.out.println("==============================");

            List<Edge> finalSettlement = sumOfSubset(givers, receivers);
            System.out.println("===========Receivers===========");
            assert finalSettlement != null;
            finalSettlement.forEach(x -> System.out.printf("• %-10s -> %10s (%.0f) \n", x.getFrom(), x.getTo(), Math.abs(x.getWeight())));
            System.out.println("==============================");
        }
    }


    static List<Edge> sumOfSubset(Map<String, Double> g, Map<String, Double> r) {
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