package com.splitwise;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.splitwise.models.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.splitwise.Calculator.getFinalSettlements;

public class Main {
    public static void main(String[] args) throws IOException {

        File folder = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("test_cases")).getFile());
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        assert files != null;
        for (File file : files) {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(file);
            List<String> users = mapper.convertValue(root.get("users"), new TypeReference<List<String>>() {});
            List<Expense> expenses = mapper.convertValue(root.get("expenses"), new TypeReference<List<Expense>>() {});
            Map<String, Double> balances = new HashMap<>();
            for (JsonNode balanceNode : root.get("balances")) {
                String name = balanceNode.get("name").asText();
                double balance = balanceNode.get("balance").asDouble();
                balances.put(name, balance);
            }
            List<Edge> settlements = mapper.convertValue(root.get("settlements"), new TypeReference<List<Edge>>() {});

            GroupReport report = new GroupReport();
            report.setUsers(users);
            report.setExpenses(expenses);
            report.setExpectBalances(balances);
            report.setExpectSettlements(settlements);

            List<Edge> edges = getEdgesFromExpenses(report.getExpenses());
            DirectedGraph graph = new DirectedGraph(edges);

            /*graph.addEdge("Gabe", "Bob", 30);
            graph.addEdge("Gabe", "David", 10);
            graph.addEdge("Fred", "Bob", 10);
            graph.addEdge("Fred", "Charlie", 30);
            graph.addEdge("Fred", "David", 10);
            graph.addEdge("Fred", "Ema", 10);
            graph.addEdge("Bob", "Charlie", 40);
            graph.addEdge("Charlie", "David", 20);
            graph.addEdge("David", "Ema", 50);*/

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

            List<Edge> finalSettlement = getFinalSettlements(givers, receivers);
            System.out.println("===========Receivers===========");
            assert finalSettlement != null;
            finalSettlement.forEach(x -> System.out.printf("• %-10s -> %10s (%.0f) \n", x.getFrom(), x.getTo(), Math.abs(x.getAmount())));
            System.out.println("==============================");
        }
    }

    static List<Edge> getEdgesFromExpenses(List<Expense> expenses) {
        List<Edge> edges = new ArrayList<>();
        for (Expense expense : expenses) {
            double splitAmt = expense.getAmount() / expense.getInvolved().size();
            for (String user : expense.getInvolved()) {
                edges.add(new Edge(expense.getPaidBy(), user, splitAmt));
            }
        }
        return edges;
    }

}