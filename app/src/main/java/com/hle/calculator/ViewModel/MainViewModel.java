package com.hle.calculator.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.hle.calculator.Model.ResultService;
import com.hle.calculator.Model.SubOperation;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private final String operators = "=*/-+";
    private ResultService resultService;

    //separate data stream needed for the input history view due to mediator limitations
    private MutableLiveData<String> historyInput = new MutableLiveData<>();
    private MediatorLiveData<String> liveHistoryString = new MediatorLiveData<>();

    private MutableLiveData<String> buttonInput = new MutableLiveData<>();
    private MediatorLiveData<String> liveNumberString = new MediatorLiveData<>();

    private MutableLiveData<SubOperation> liveSubOperation = new MutableLiveData<>();
    private MediatorLiveData<ArrayList<SubOperation>> liveSubOperationList = new MediatorLiveData<>();

    private MutableLiveData<String> liveError = new MutableLiveData<>();


    //empty constructor required
    public MainViewModel(){}

    public void setResultService(ResultService service){
        this.resultService = service;
    }

    public MutableLiveData<String> getHistoryInput() {
        if (historyInput == null){
            historyInput = new MutableLiveData<>();
        }
        return historyInput;
    }

    public void setHistoryInput(String inputString) {
        if (inputString == null){
            historyInput.setValue(null);
        }
        historyInput.setValue(inputString);
    }

    public MutableLiveData<String> getButtonInput() {
        if (buttonInput == null){
            buttonInput = new MutableLiveData<>();
        }
        return buttonInput;
    }

    public void setButtonInput(String input) {
        if (input == null){
            buttonInput.setValue(null);
        }
        buttonInput.setValue(input);
    }

    public void setLiveSubOperation(SubOperation subOperation) {
        if (subOperation == null){
            liveSubOperation.setValue(null);
        } else {
            liveSubOperation.setValue(subOperation);
        }
    }

    public MutableLiveData<SubOperation> getLiveSubOperation() {
        if (liveSubOperation == null) {
            liveSubOperation = new MediatorLiveData<>();
        }
        return liveSubOperation;
    }

    public void setLiveHistoryString(String string) {
        if (string == null){
            string = "";
        }
        liveHistoryString.setValue(string);
    }

    public MutableLiveData<String> getLiveHistoryString() {
        if (liveHistoryString == null){
            liveHistoryString = new MediatorLiveData<>();
        }
        liveHistoryString.addSource(historyInput, new Observer<String>() {
            @Override
            public void onChanged(String input) {
                if (input != null) {
                    String string = "";
                    if (liveHistoryString.getValue() != null) {
                        string = liveHistoryString.getValue();
                    }
                    if (resultService.isNumber(input) || input.equals(".") || operators.contains(input)){
                        string += input;
                        setLiveHistoryString(string);
                    } else if (input.equals(" ") && string.length()>0){
                        setLiveHistoryString(string.substring(0, string.length()-1));
                    } else if (input.equals("Q")){
                        string += "\n";
                        setLiveHistoryString(string);
                    }
                }
            }
        });
        return liveHistoryString;
    }

    public void setLiveNumberString(String numberString) {
        liveNumberString.setValue(numberString);
    }


    public MutableLiveData<String> getLiveNumberString() {
        if (liveNumberString == null){
            liveNumberString = new MediatorLiveData<>();
        }
        liveNumberString.addSource(buttonInput, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String string = handleButtonString(s);
                setLiveNumberString(string);
            }
        });
        return liveNumberString;
    }


    //list will be used for calculating result
    public void setLiveSubOperationList(ArrayList<SubOperation> operationList) {
        if (operationList == null){
            operationList = new ArrayList<>();
        }
        liveSubOperationList.setValue(operationList);
    }

    public MutableLiveData<ArrayList<SubOperation>> getLiveSubOperationList() {
        if (liveSubOperationList == null || (liveSubOperationList.getValue() == null || liveSubOperationList.getValue().isEmpty())){
            liveSubOperationList = new MediatorLiveData<>();
        }
        liveSubOperationList.addSource(liveSubOperation, new Observer<SubOperation>() {
            @Override
            public void onChanged(SubOperation subOperation) {
                ArrayList<SubOperation> list = new ArrayList<>();

                if (subOperation != null) {
                    if (liveSubOperationList.getValue() != null) {
                        list = liveSubOperationList.getValue();
                    } else {
                        list = new ArrayList<>();
                    }
                    //checking if new operation is "legal"; not /0
                    if (!subOperation.getNumberString().isEmpty() && resultService.isNumber(subOperation.getNumberString())){
                        double number = Double.parseDouble(subOperation.getNumberString());
                        if (list.size() > 0 && list.get(list.size() - 1).getOperator().equals("/") && number == 0) {
                            setLiveError("DIV 0");
                        } else {
                            list.add(subOperation);
                        }
                    }
                }
                setLiveSubOperationList(list);
            }
        });
        return liveSubOperationList;
    }

    public LiveData<String> getLiveError() {
        if (liveError == null ){
            liveError = new MutableLiveData<>();
        }
        return liveError;
    }

    public void setLiveError(String error) {
        if (error == null){
            error = "";
        }
        liveError.setValue(error);
    }

    public String calculateResult(ArrayList<SubOperation> operations) {
        if (operations != null && ! operations.isEmpty()){
            //passing in operators except =
            return resultService.calculateResult(operators.substring(1), operations).get(0).getNumberString();
        }
        else {
            return null;
        }
    }


    private String handleButtonString(String buttonString) {
        String tempString = "";
        if (liveNumberString.getValue() != null) {
            tempString = liveNumberString.getValue();
        }
        //checking for number char to add to current number string
        if (resultService.isNumber(buttonString)) {
            tempString += buttonString;
            return tempString;
        } else if (buttonString.equals(".")){
            if (tempString.contains(".")){
                setLiveError(". ERR");
            } else {
                tempString += buttonString;
            }
            return tempString;
        } else if (operators.contains(buttonString) || buttonString.equals("=")){
            handleOperator(buttonString);
        } else if (buttonString.equals("AC")){
            resetAll();
            //handling space char sent from back button
        } else if (buttonString.equals(" ")){
            if (tempString != null && !tempString.isEmpty()){
                return tempString.substring(0, tempString.length()-1);
            }
            setLiveError("");
        }
        return  null;
    }

    private void handleOperator(String buttonString) {
        String numberString = "";

        //handling empty number by adding 0. Would be preferable to replace
        if (liveNumberString.getValue() == null || liveNumberString.getValue().isEmpty()) {
            numberString = "0.0";
        } else {
            numberString = liveNumberString.getValue();
        }

        //triggering a result calculation
        SubOperation subOperation = new SubOperation(numberString,  buttonString);
        setLiveSubOperation(subOperation);
        liveNumberString.setValue(null);
    }

    public void resetAll() {
        setLiveError("");
        setLiveHistoryString("");
        setLiveSubOperation(null);
        setLiveSubOperationList(null);
        setHistoryInput("");
        setLiveNumberString("");
    }
}