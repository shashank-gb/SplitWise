package com.splitwise.models;

import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Node {
    final String val;
    Map<Node, Double> neighbors = new LinkedHashMap<>();

    public void addNeighbor(Node node, double weight) {
        neighbors.put(node, weight);
    }
}
