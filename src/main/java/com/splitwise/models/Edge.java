package com.splitwise.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Edge {
    String from, to;
    double weight;
}
