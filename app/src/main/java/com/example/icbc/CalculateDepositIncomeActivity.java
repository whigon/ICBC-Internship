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

public class CalculateDepositIncomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CalculateDepositIncomeActivity";
    private double amount;
    private Spinner isCurrentSpinner;
    private EditText currentDurationText;
    private Spinner savingSpinner;
    private Spinner rateSpinner;
    private EditText valueText;
    private TextView strikeRateText;
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

        TextView amountText = findViewById(R.id.amount);
        // 大数表示不用E
        DecimalFormat doubleFormat = new DecimalFormat();
        doubleFormat.setMaximumFractionDigits(2);
        amountText.setText(doubleFormat.format(amount) + "元");

        strikeRateText = findViewById(R.id.strike_rate);
        isCurrentSpinner = findViewById(R.id.is_current_spinner);
        isCurrentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {// 如果选择了活期，锁定定期存款期限选择
                    savingSpinner.setEnabled(false);
                    currentDurationText.setEnabled(true);
                } else if (position == 2) {
                    savingSpinner.setEnabled(true);
                    currentDurationText.setEnabled(false);
                } else {// 默认情况下解锁
                    savingSpinner.setEnabled(true);
                    currentDurationText.setEnabled(true);
                }
                clearText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currentDurationText = findViewById(R.id.current_duration_input);
        // 设置输入为整数
        currentDurationText.setInputType(InputType.TYPE_CLASS_NUMBER);
        // 设置输入长度限制
        currentDurationText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        currentDurationText.addTextChangedListener(new TextWatcher() {
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

        savingSpinner = findViewById(R.id.saving_duration_spinner);
        savingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rateSpinner = findViewById(R.id.rate_type_spinner);
        rateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        valueText = findViewById(R.id.value_input);
        // 设置输入为数字
        valueText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valueText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    // 获取浮动比例/点数
                    double value = Double.parseDouble(valueText.getText().toString());
                    // 获取存款时长
                    double duration = getDuration();
                    ArrayList<Double> depositRate = null;

                    if (duration != 0) {
                        // 获取存款对应的基准利率
                        if (isCurrentSpinner.getSelectedItemPosition() == 1)
                            depositRate = calculator.getDepositRate(true, duration); //活期
                        else if (isCurrentSpinner.getSelectedItemPosition() == 2)
                            depositRate = calculator.getDepositRate(false, duration); //定期

                        if (depositRate != null) {
                            if (rateSpinner.getSelectedItemPosition() == 0)
                                strikeRateText.setText("");
                            else if (rateSpinner.getSelectedItemPosition() == 1) {
                                double baseRate = depositRate.get(0); //央行基准利率
                                value /= 100; //浮动比例
                                // 计算执行利率
                                strikeRateText.setText(String.format("%.2f", (baseRate + value) * 100));
                            } else if (rateSpinner.getSelectedItemPosition() == 2) {
                                double baseRate = depositRate.get(1); //工行挂牌利率
                                value /= 10000; //1BP = 0.01%
                                // 计算执行利率
                                strikeRateText.setText(String.format("%.2f", (baseRate + value) * 100));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "afterTextChanged: " + e);
                }
            }
        });

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
                    Toast.makeText(this, "请选择存款类型", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    // 必须整数
                    duration = Integer.parseInt(currentDurationText.getText().toString()) / 360.0;
                    result = calculator.calculateDepositFTPIncome(amount, true, duration, rateSpinner.getSelectedItemPosition(), valueText.getText().toString());
                    break;
                case 2:
                    if (savingSpinner.getSelectedItemPosition() == 0)
                        Toast.makeText(this, "请选择定期期限", Toast.LENGTH_SHORT).show();
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

            spreadResult.setText(String.format("%.3f", result[0] * 100));
            incomeResult.setText(String.format("%.5f", result[1]));
        } catch (Exception e) {
            Toast.makeText(this, "输入不合法", Toast.LENGTH_SHORT).show();
        }

    }

    private double getDuration() {
        try {
            switch (isCurrentSpinner.getSelectedItemPosition()) {
                case 1:
                    // 必须整数
                    return Integer.parseInt(currentDurationText.getText().toString()) / 360.0;
                case 2:
                    if (savingSpinner.getSelectedItemPosition() == 0)
                        return 0;
                    else if (savingSpinner.getSelectedItemPosition() == 1)
                        return 7 / 360.0;
                    else if (savingSpinner.getSelectedItemPosition() == 2)
                        return 0.25;
                    else if (savingSpinner.getSelectedItemPosition() == 3)
                        return 0.5;
                    else if (savingSpinner.getSelectedItemPosition() == 4)
                        return 1;
                    else if (savingSpinner.getSelectedItemPosition() == 5)
                        return 2;
                    else if (savingSpinner.getSelectedItemPosition() == 6)
                        return 3;
                    else if (savingSpinner.getSelectedItemPosition() == 7)
                        return 5;
                default:
                    return 0;
            }
        } catch (Exception e) {
            Toast.makeText(this, "输入不合法", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    private void clearText() {
        valueText.setText("");
        strikeRateText.setText("");
    }
}
