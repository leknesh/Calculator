package com.hle.calculator.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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

    private MainViewModel mViewModel;

    private View rootView;
    private TextView historyTextView, resultTextView;
    private MaterialButton resetBtn, backBtn, plusBtn, minusBtn, multiplyBtn, divideBtn, equalsBtn, commaBtn;
    private MaterialButton zeroBtn, oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn, sixBtn, sevenBtn, eightBtn, nineBtn;

    private ResultService resultService;
    private String tempString = "";
    private Boolean zeroError = true;
    private final String TAG = "TAG";

    public static MainFragment newInstance() {
        return new MainFragment();
    }

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
        mViewModel = new ViewModelProvider(MainFragment.this).get(MainViewModel.class);
        resultService = new ResultService();
        mViewModel.setResultService(resultService);

        buildGui();
        setDataObservers();
    }

    //observers will ensure updated text in textviews
    private void setDataObservers() {
        //initialisation needed for mediators to start observing
        mViewModel.getLiveSubOperationList().observe(getViewLifecycleOwner(), new Observer<ArrayList<SubOperation>>() {
            @Override
            public void onChanged(ArrayList<SubOperation> subOperations) {
                if (subOperations == null){
                    resultTextView.setText("0.0");
                } else if (subOperations.size() > 0){
                    Log.d(TAG, "subop list size: " + subOperations.size());
                    String string = subOperations.get(subOperations.size()-1).getOperator();
                    if (string.equals("=")){
                        String result = mViewModel.calculateResult(subOperations);
                        resultTextView.setText(result);
                        mViewModel.setHistoryInput(result);
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
                    historyTextView.setText(string);
                } else {
                    historyTextView.setText("");
                }
            }
        });

        mViewModel.getZeroError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    resultTextView.setText(R.string.div_error);
                }
                mViewModel.setZeroError(false);
            }
        });

        mViewModel.getLiveSubOperation().observe(getViewLifecycleOwner(), new Observer<SubOperation>() {
            @Override
            public void onChanged(SubOperation subOperation) {
                //do nothing
            }
        });

        mViewModel.getLiveNumberString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //do nothing
            }
        });
    }


    private void buildGui() {
        historyTextView = rootView.findViewById(R.id.input_history_tv);
        resultTextView = rootView.findViewById(R.id.result_tv);
        resultTextView.setText("0.0");
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
        
        addListeners();
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
            if (!buttonTxt.isEmpty()){
                //two copies needed for different mediators
                mViewModel.setHistoryInput(buttonTxt);
                mViewModel.setButtonInput(buttonTxt);
            }
            button.setChecked(false);
        }
    }
}