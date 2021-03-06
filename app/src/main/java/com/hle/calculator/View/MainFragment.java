package com.hle.calculator.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.hle.calculator.Model.ResultService;
import com.hle.calculator.Model.SubOperation;
import com.hle.calculator.R;
import com.hle.calculator.ViewModel.MainViewModel;

import java.util.ArrayList;

public class MainFragment extends Fragment implements MaterialButton.OnCheckedChangeListener {

    private static final String HISTORY_STRING = "INPUT_STRING";
    private static final String RESULT_STRING = "RESULT_STRING";
    private MainViewModel mViewModel;

    private View rootView;
    private TextView historyTextView, resultTextView;
    private MaterialButton resetBtn, backBtn, plusBtn, minusBtn, multiplyBtn, divideBtn, equalsBtn, commaBtn;
    private MaterialButton zeroBtn, oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn, sixBtn, sevenBtn, eightBtn, nineBtn;

    private String historyString = "";
    private String resultString = "0.0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildGui();
        addListeners();

        //setting the data observers twice causes crash
        if (savedInstanceState == null) {
            mViewModel = new ViewModelProvider(MainFragment.this).get(MainViewModel.class);
            mViewModel.setResultService(new ResultService());
            setDataObservers();
        } else {
            historyString = savedInstanceState.getString(HISTORY_STRING);
            resultString = savedInstanceState.getString(RESULT_STRING);
            historyTextView.setText(historyString);
            resultTextView.setText(resultString);
        }
    }

    //observers will ensure updated text in textviews
    private void setDataObservers() {
        mViewModel.getLiveSubOperationList().observe(getViewLifecycleOwner(), new Observer<ArrayList<SubOperation>>() {
            @Override
            public void onChanged(ArrayList<SubOperation> subOperations) {
                if (subOperations == null){
                    resultTextView.setText("0.0");
                } else if (subOperations.size() > 0){
                    String string = subOperations.get(subOperations.size()-1).getOperator();
                    if (string.equals("=")){
                        resultString = mViewModel.calculateResult(subOperations);
                        resultTextView.setText(resultString);
                        mViewModel.setHistoryInput(resultString);
                        //not able to send a line shift in the livedata, Q is substitute, handled on concatenation
                        mViewModel.setHistoryInput("Q");
                        mViewModel.setLiveNumberString(null);
                        mViewModel.setLiveSubOperation(null);
                        mViewModel.setLiveSubOperationList(null);
                    }
                }
            }
        });

        mViewModel.getLiveHistoryString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                if (string != null){
                    historyTextView = rootView.findViewById(R.id.input_history_tv);
                    historyTextView.setText(string);
                    historyString = string;
                } else {
                    historyTextView.setText("");
                }
            }
        });

        mViewModel.getLiveError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                if (string != null && ! string.isEmpty()){
                    resultTextView.setText(string);
                    //not able to send a line shift in the livedata, Q is substitute, handled on concatenation
                    mViewModel.setHistoryInput("Q");
                } else {
                    resultTextView.setText("");
                }
            }
        });


        //empty observer needed for mediators to start observing in the viewmodel
        mViewModel.getLiveSubOperation().observe(getViewLifecycleOwner(), new Observer<SubOperation>() {
            @Override
            public void onChanged(SubOperation subOperation) {
                //do nothing
            }
        });

        //empty observer needed for mediators to start observing in the viewmodel
        mViewModel.getLiveNumberString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //do nothing
            }
        });
    }

    private void buildGui() {
        historyTextView = rootView.findViewById(R.id.input_history_tv);
        historyTextView.setMovementMethod(new ScrollingMovementMethod());
        historyTextView.setText(historyString);
        resultTextView = rootView.findViewById(R.id.result_tv);
        resultTextView.setText(resultString);
        resetBtn = rootView.findViewById(R.id.reset_btn);
        backBtn = rootView.findViewById(R.id.back_btn);
        plusBtn = rootView.findViewById(R.id.plus_btn);
        minusBtn = rootView.findViewById(R.id.minus_btn);
        multiplyBtn = rootView.findViewById(R.id.multiply_btn);
        divideBtn = rootView.findViewById(R.id.divide_btn);
        equalsBtn = rootView.findViewById(R.id.equals_btn);
        commaBtn = rootView.findViewById(R.id.comma_btn);
        zeroBtn = rootView.findViewById(R.id.zero_btn);
        oneBtn = rootView.findViewById(R.id.one_btn);
        twoBtn = rootView.findViewById(R.id.two_btn);
        threeBtn = rootView.findViewById(R.id.three_btn);
        fourBtn = rootView.findViewById(R.id.four_btn);
        fiveBtn = rootView.findViewById(R.id.five_btn);
        sixBtn = rootView.findViewById(R.id.six_btn);
        sevenBtn = rootView.findViewById(R.id.seven_btn);
        eightBtn = rootView.findViewById(R.id.eight_btn);
        nineBtn = rootView.findViewById(R.id.nine_btn);
    }

    private void addListeners() {
        resetBtn.addOnCheckedChangeListener(this);
        backBtn.addOnCheckedChangeListener(this);
        plusBtn.addOnCheckedChangeListener(this);
        minusBtn.addOnCheckedChangeListener(this);
        multiplyBtn.addOnCheckedChangeListener(this);
        divideBtn.addOnCheckedChangeListener(this);
        equalsBtn.addOnCheckedChangeListener(this);
        commaBtn.addOnCheckedChangeListener(this);
        zeroBtn.addOnCheckedChangeListener(this);
        oneBtn.addOnCheckedChangeListener(this);
        twoBtn.addOnCheckedChangeListener(this);
        threeBtn.addOnCheckedChangeListener(this);
        fourBtn.addOnCheckedChangeListener(this);
        fiveBtn.addOnCheckedChangeListener(this);
        sixBtn.addOnCheckedChangeListener(this);
        sevenBtn.addOnCheckedChangeListener(this);
        eightBtn.addOnCheckedChangeListener(this);
        nineBtn.addOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(MaterialButton button, boolean isChecked) {
        //Materialbutton, check/uncheck state not used, toggle check status after event
        if (isChecked){
            String buttonTxt = String.valueOf(button.getText());
            if (mViewModel != null && !buttonTxt.isEmpty()){
                //two copies needed for different mediators
                if (mViewModel.getHistoryInput() != null){
                    mViewModel.setHistoryInput(buttonTxt);
                }
                if (mViewModel.getButtonInput() != null){
                    mViewModel.setButtonInput(buttonTxt);
                }
            }
            button.setChecked(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(HISTORY_STRING, historyString);
        outState.putString(RESULT_STRING, resultString);
    }
}