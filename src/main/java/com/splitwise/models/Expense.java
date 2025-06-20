package com.splitwise.models;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Expense {
    String desc;
    User paidBy;
    double amount;
    String splitType;
    List<User> involved;
}
