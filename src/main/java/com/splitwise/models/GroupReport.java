package com.splitwise.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class GroupReport {
    @Getter
    final List<User> users;
    @Getter
    final List<Expense> expenses;
    Map<String, Double> expectBalances;
    List<Edge> expectSettlements;
}
