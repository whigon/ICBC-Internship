package com.example.icbc;

import android.content.Intent;
import android.os.Bundle;
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

public class CalculateLoanIncomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CalculateLoanIncomeActivity";
    private double amount;
    private int type;
    private EditText durationText;
    private EditText pointText;
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

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        durationText = findViewById(R.id.duration_input);
        pointText = findViewById(R.id.point_input);
        spreadResult = findViewById(R.id.spread_result);
        incomeResult = findViewById(R.id.income_result);

        Button calculateButton = findViewById(R.id.calculate);
        calculateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        double duration;
        try {
            int point = Integer.parseInt(pointText.getText().toString());

            if (type == 1)
                duration = Double.parseDouble(durationText.getText().toString()) / 12.0;
            else if (type == 2)
                duration = Double.parseDouble(durationText.getText().toString());
            else
                duration = 0;

            double[] result = calculator.calculateLoanFTPIncome(amount, duration, point);
            spreadResult.setText(String.format("%.5f", result[0]));
            incomeResult.setText(String.format("%.5f", result[1]));
        } catch (Exception e) {
            Toast.makeText(this, "输入不合法", Toast.LENGTH_SHORT).show();
        }
    }
}
