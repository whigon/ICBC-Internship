package com.example.icbc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CalculateDepositIncomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CalculateDepositIncomeActivity";
    private double amount;
    private Spinner isCurrentSpinner;
    private EditText currentDurationText;
    private Spinner savingSpinner;
    private Spinner rateSpinner;
    private EditText valueText;
    private TextView spreadResult;
    private TextView incomeResult;
    private Calculator calculator = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_deposit_income);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        amount = intent.getDoubleExtra("amount", 0.0);
        Log.d(TAG, "onCreate: " + amount);

        isCurrentSpinner = findViewById(R.id.is_current_spinner);
        currentDurationText = findViewById(R.id.current_duration_input);
        savingSpinner = findViewById(R.id.saving_duration_spinner);
        rateSpinner = findViewById(R.id.rate_type_spinner);
        valueText = findViewById(R.id.value_input);

        spreadResult = findViewById(R.id.spread_result);
        incomeResult = findViewById(R.id.income_result);

        Button button = findViewById(R.id.calculate);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        double duration = 0;
        double[] result = new double[]{0.0, 0.0};

        try {
            switch (isCurrentSpinner.getSelectedItemPosition()) {
                case 0:
                    Toast.makeText(this, "请选择存款类型是否为活期", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    // 必须整数
                    duration = Integer.parseInt(currentDurationText.getText().toString()) / 360.0;
                    result = calculator.calculateDepositFTPIncome(amount, true, duration, rateSpinner.getSelectedItemPosition(), valueText.getText().toString());
                    break;
                case 2:
                    if (savingSpinner.getSelectedItemPosition() == 0)
                        Toast.makeText(this, "请选择定期时长", Toast.LENGTH_SHORT).show();
                    else if (savingSpinner.getSelectedItemPosition() == 1)
                        duration = 7 / 360.0;
                    else if (savingSpinner.getSelectedItemPosition() == 2)
                        duration = 0.25;
                    else if (savingSpinner.getSelectedItemPosition() == 3)
                        duration = 0.5;
                    else if (savingSpinner.getSelectedItemPosition() == 4)
                        duration = 1;
                    else if (savingSpinner.getSelectedItemPosition() == 5)
                        duration = 2;
                    else if (savingSpinner.getSelectedItemPosition() == 6)
                        duration = 3;
                    else if (savingSpinner.getSelectedItemPosition() == 7)
                        duration = 5;

                    result = calculator.calculateDepositFTPIncome(amount, false, duration, rateSpinner.getSelectedItemPosition(), valueText.getText().toString());
                    break;
            }

            spreadResult.setText(String.format("%.5f", result[0]));
            incomeResult.setText(String.format("%.5f", result[1]));
        } catch (Exception e) {
            Toast.makeText(this, "输入不合法", Toast.LENGTH_SHORT).show();
        }

    }


}
