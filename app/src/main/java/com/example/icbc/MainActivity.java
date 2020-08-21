package com.example.icbc;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private EditText textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.input);
        // 设置输入长度限制
        textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        Button depositButton = findViewById(R.id.deposit);
        depositButton.setOnClickListener(this);
        Button loanButton = findViewById(R.id.loan);
        loanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        try {
            double amount = Double.parseDouble(textView.getText().toString());
            Log.d(TAG, "onClick: " + amount);
            switch (v.getId()) {
                case R.id.deposit:
                    intent = new Intent(MainActivity.this, CalculateDepositIncomeActivity.class);
                    intent.putExtra("amount", amount);
                    startActivity(intent);
                    break;
                case R.id.loan:
                    intent = new Intent(MainActivity.this, CalculateLoanIncomeActivity.class);
                    intent.putExtra("amount", amount);
                    startActivity(intent);
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, "onClick: " + e);
            Toast.makeText(this, "输入不合法", Toast.LENGTH_SHORT).show();
        }
    }
}
