package com.hle.calculator.Model;

import androidx.annotation.NonNull;

public class SubOperation {
    private String numberString;
    private String operator;

    public SubOperation(String numberString, String operator) {
        this.numberString = numberString;
        this.operator = operator;
    }

    public String getNumberString() {
        return numberString;
    }

    public void setNumberString(String numberString) {
        this.numberString = numberString;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @NonNull
    @Override
    public String toString() {
        return "Number: " + numberString + ", operator: " + operator;
    }
}
