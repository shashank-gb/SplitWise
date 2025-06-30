package com.splitwise;

import com.splitwise.models.Edge;
import com.splitwise.models.Expense;

import java.util.*;

public class Calculator {

    public static Map<String, Double> computeNetBalances(List<Expense> expenses) {
        Map<String, Double> netBalances = new HashMap<>();

        for (Expense expense : expenses) {
            double share = expense.getAmount() / expense.getInvolved().size();
            String payer = expense.getPaidBy();

            // The payer paid full amount → they are owed money
            netBalances.put(payer, netBalances.getOrDefault(payer, 0.0) + expense.getAmount());

            // Each involved person owes their share
            for (String person : expense.getInvolved()) {
                netBalances.put(person, netBalances.getOrDefault(person, 0.0) - share);
            }
        }

        return netBalances;
    }

    public static List<Edge> finalSettlement(Map<String, Double> netBalances) {
        List<Edge> result = new ArrayList<>();

        PriorityQueue<Map.Entry<String, Double>> creditors = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        PriorityQueue<Map.Entry<String, Double>> debtors = new PriorityQueue<>((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> entry : netBalances.entrySet()) {
            double balance = entry.getValue();
            if (Math.abs(balance) < 1e-6) continue;
            if (balance > 0) {
                debtors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), balance));
            } else {
                creditors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), balance));
            }
        }

        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            Map.Entry<String, Double> debtor = debtors.poll();
            Map.Entry<String, Double> creditor = creditors.poll();

            double debit = debtor.getValue();
            double credit = -creditor.getValue();

            double settledAmount = Math.min(debit, credit);

            result.add(new Edge(debtor.getKey(), creditor.getKey(), settledAmount));

            double newDebtorBal = debit - settledAmount;
            double newCreditorBal = credit - settledAmount;

            if (newDebtorBal > 1e-6) {
                debtors.add(new AbstractMap.SimpleEntry<>(debtor.getKey(), newDebtorBal));
            }
            if (newCreditorBal > 1e-6) {
                creditors.add(new AbstractMap.SimpleEntry<>(creditor.getKey(), -newCreditorBal));
            }
        }

        return result;
    }

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
