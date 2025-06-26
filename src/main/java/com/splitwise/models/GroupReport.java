package com.splitwise.models;

import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;


@Data
public class GroupReport {
    @Getter
    List<String> users;
    @Getter
    List<Expense> expenses;
    Map<String, Double> expectBalances;
    List<Edge> expectSettlements;
}
