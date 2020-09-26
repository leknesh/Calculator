package com.hle.calculator.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

//separating calculations from view
public class ResultService {

    private double result;

    public ResultService(){}

    //used to avoid uncaught number parse exceptions
    public boolean isNumber(String inputString){
        if (inputString == null || inputString.isEmpty()){
            return false;
        }
        try {
            Double.parseDouble(inputString);
            return true;
        } catch (NumberFormatException ex){
            return false;
        }
    }
    // recursion used to adhere to mathematical order of operation
    public ArrayList<SubOperation> calculateResult(String operators, ArrayList<SubOperation> subOperations) {
        double subResult = 0;
        double thisNumber = 0;
        double nextNumber = 0;

        if (operators == null || operators.isEmpty()){
            return subOperations;
        }
        String currentOperator = operators.substring(0, 1);
        Log.d("TAG", "currentOperator = " + currentOperator);

        for (SubOperation subOperation: subOperations){
            Log.d("TAG", "New method round: " + subOperation.toString());
        }
        SubOperation thisOperation, nextOperation;
        String thisOperator, nextOperator;
        if (subOperations.size() <= 1){
            return subOperations;
        } else {
            for (int i=0; i<subOperations.size()-1; i++ ) {
                thisOperation = subOperations.get(i);
                nextOperation = subOperations.get(i + 1);
                Log.d("TAG", "This and next operations = " + thisOperation + " " + nextOperation);
                if (isNumber(thisOperation.getNumberString())) {
                    thisNumber = Double.parseDouble(thisOperation.getNumberString());
                }
                if (isNumber(nextOperation.getNumberString())) {
                    nextNumber = Double.parseDouble(nextOperation.getNumberString());
                }
                thisOperator = thisOperation.getOperator();
                nextOperator = nextOperation.getOperator();
                //multiply top priority, first hit calculates, updates list and breaks for-loop

                if (thisOperation.getOperator().equals(currentOperator)) {
                    switch (currentOperator) {
                        case "*":
                            subResult = thisNumber * nextNumber;
                            Log.d("TAG", "New subres = " + subResult + ", operation = " + thisOperator);
                            break;
                        case "/":
                            if (nextNumber != 0)
                                subResult = thisNumber / nextNumber;
                            Log.d("TAG", "New subres = " + subResult + ", operation = " + thisOperator);
                            break;
                        case "-":
                            subResult = thisNumber - nextNumber;
                            Log.d("TAG", "New subres = " + subResult + ", operation = " + thisOperator);
                            break;
                        case "+":
                            subResult = thisNumber + nextNumber;
                            Log.d("TAG", "New subres = " + subResult + ", operation = " + thisOperator);
                            break;
                        default:
                            //when nothing matches current operator: moving to next
                            break;
                    }
                    subOperations.set(i + 1, new SubOperation(String.valueOf(subResult), nextOperator));
                    subOperations.remove(thisOperation);
                    i++;
                    Log.d("TAG", "New subres = " + subResult + ", newlistsize = " + subOperations.size());
                    Log.d("TAG", "nextOperation = " + new SubOperation(String.valueOf(subResult), nextOperator).toString());
                    calculateResult(operators, subOperations);
                }
            }
            if (operators.length()>1){
                operators = operators.substring(1);
                calculateResult(operators, subOperations);
            } else {
                return subOperations;
            }
        }
        return subOperations;
    }
}
