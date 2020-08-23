package com.example.icbc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CalculateLoanIncomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CalculateLoanIncomeActivity";
    private double amount;
    private double duration;
    private int type;
    private EditText durationText;
    private EditText pointText;
    private TextView strikeRateText;
    private TextView spreadResult;
    private TextView incomeResult;
    private Calculator calculator = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_loan_income);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        amount = intent.getDoubleExtra("amount", 0.0);
        Log.d(TAG, "onCreate: " + amount);

        TextView amountText = findViewById(R.id.amount);
        // 大数表示不用E
        DecimalFormat doubleFormat = new DecimalFormat();
        doubleFormat.setMaximumFractionDigits(2);
        amountText.setText(doubleFormat.format(amount) + "元");

        durationText = findViewById(R.id.duration_input);
        // 设置输入为整数
        durationText.setInputType(InputType.TYPE_CLASS_NUMBER);
        // 设置输入长度限制
        durationText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        durationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                clearText();
            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
                clearText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        strikeRateText = findViewById(R.id.strike_rate);
        pointText = findViewById(R.id.point_input);
        // 设置输入为整数
        pointText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        // 设置输入长度限制
        pointText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        pointText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double pointRate = Integer.parseInt(pointText.getText().toString()) / 10000.0;
                    duration = getDuration();
                    if (duration != 0) {
                        ArrayList<Double> loanRate = calculator.getLoanRate(duration);
                        double baseRate = loanRate.get(0);
                        strikeRateText.setText(String.format("%.2f", (baseRate + pointRate) * 100));
                    }
                } catch (Exception e) {
                    Log.d(TAG, "afterTextChanged: " + e);
                }
            }
        });
        spreadResult = findViewById(R.id.spread_result);
        incomeResult = findViewById(R.id.income_result);

        Button calculateButton = findViewById(R.id.calculate);
        calculateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            int point = Integer.parseInt(pointText.getText().toString());
            duration = getDuration();
            double[] result = calculator.calculateLoanFTPIncome(amount, duration, point);
            spreadResult.setText(String.format("%.3f", result[0] * 100));
            incomeResult.setText(String.format("%.5f", result[1]));
        } catch (Exception e) {
            Toast.makeText(this, "输入不合法", Toast.LENGTH_SHORT).show();
        }
    }

    private double getDuration() {
        if (type == 1)
            return Double.parseDouble(durationText.getText().toString()) / 12.0;
        else if (type == 2)
            return Double.parseDouble(durationText.getText().toString());
        else
            return 0;
    }

    private void clearText() {
        pointText.setText("");
        strikeRateText.setText("");
    }

}
