package com.splitwise.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class Expense {
    @JsonProperty("name")
    String desc;
    @JsonProperty("paid_by")
    String paidBy;
    double amount;
    @JsonProperty("split_type")
    String splitType;
    List<String> involved;
}
