package com.splitwise.models;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class DirectedGraph {
    List<Edge> edges;
    Map<String, Node> nodes = new LinkedHashMap<>();

    DirectedGraph(List<Edge> edges) {
        for (Edge edge : edges) {
            addEdge(edge.from, edge.to, edge.weight);
        }
    }

    public void addNode(String val) {
        nodes.putIfAbsent(val, new Node(val));
    }

    public void addEdge(String from, String to, double weight) {
        addNode(from);
        addNode(to);
        nodes.get(from).addNeighbor(nodes.get(to), weight);
    }

    public Map<String, Double> calculateNetBalances() {
        Map<String, Double> netBal = new HashMap<>();
        Map<String, Double> incomingTotals = new HashMap<>();
        Map<String, Double> outgoingTotals = new HashMap<>();
        for (Node node : nodes.values()) {
            double totalOut = 0.0;
            for (Map.Entry<Node, Double> entry : node.neighbors.entrySet()) {
                String to = entry.getKey().val;
                double weight = entry.getValue();

                totalOut += weight;

                incomingTotals.put(to, incomingTotals.getOrDefault(to, 0.0) + weight);
            }
            outgoingTotals.put(node.val, totalOut);
        }

        for (String name : nodes.keySet()) {
            double incoming = incomingTotals.getOrDefault(name, 0.0);
            double outgoing = outgoingTotals.getOrDefault(name, 0.0);
            double net = incoming - outgoing;
            netBal.put(name, net);
        }
        return netBal;
    }

    public void print() {
        for (Node node : nodes.values()) {
            System.out.printf("• %-10s -> ", node.val);
            if (node.neighbors.isEmpty()) {
                System.out.println("[no outing edges]");
            } else {
                List<String> edges = new ArrayList<>();
                for (Map.Entry<Node, Double> entry : node.neighbors.entrySet()) {
                    edges.add(String.format("%s (%.2f)", entry.getKey().val, entry.getValue()));
                }
                System.out.println(String.join(", ", edges));
            }
        }
    }
}
