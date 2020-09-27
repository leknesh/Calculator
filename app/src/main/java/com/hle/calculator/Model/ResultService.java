package com.hle.calculator.Model;

import java.util.ArrayList;

//separating calculations from view
public class ResultService {

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

    // recursion used to adhere to mathematical order of operation.
    // Each method run will use a new operator
    public ArrayList<SubOperation> calculateResult(String operators, ArrayList<SubOperation> subOperations) {
        SubOperation thisOperation, nextOperation;
        String currentOperator;
        double subResult = 0;
        double thisNumber = 0;
        double nextNumber = 0;

        //after going through all four operators the result is returned
        if (operators == null || operators.isEmpty()){
            return subOperations;
        } else {
            currentOperator = operators.substring(0, 1);
        }
        //if list is finished, the final result is returned as suboperationlist containing (result, =)
        if (subOperations.size() <= 1){
            return subOperations;
        } else {
            for (int i=0; i<subOperations.size()-1; i++ ) {
                thisOperation = subOperations.get(i);
                nextOperation = subOperations.get(i + 1);

                //checking numbers to avoid exceptions
                if (isNumber(thisOperation.getNumberString())) {
                    thisNumber = Double.parseDouble(thisOperation.getNumberString());
                }
                if (isNumber(nextOperation.getNumberString())) {
                    nextNumber = Double.parseDouble(nextOperation.getNumberString());
                }

                //used for checking if the calculation should be performed, then performing calculation
                if (thisOperation.getOperator().equals(currentOperator)) {
                    switch (currentOperator) {
                        case "*":
                            subResult = thisNumber * nextNumber;
                            break;
                        case "/":
                            //division error should not get here, but handled just in case
                            if (nextNumber != 0)
                                subResult = thisNumber / nextNumber;
                            break;
                        case "-":
                            subResult = thisNumber - nextNumber;
                            break;
                        case "+":
                            subResult = thisNumber + nextNumber;
                            break;
                        default:
                            //when nothing matches current operator: moving to next
                            break;
                    }
                    // a match and subsequent calculation will be followed by updating and shortening the
                    // suboperationslist and calling the method again with new list
                    subOperations.set(i + 1, new SubOperation(String.valueOf(subResult), nextOperation.getOperator()));
                    subOperations.remove(thisOperation);
                    calculateResult(operators, subOperations);
                }
            }
            //this point will only be reached if there are no more occurrences of the current operator,
            // meaning the next operator can be used
            if (operators.length()>1){
                operators = operators.substring(1);
                calculateResult(operators, subOperations);
            } else {
                return subOperations;
            }
        }
        //this will never be reached due to the recursion setup
        return subOperations;
    }
}
