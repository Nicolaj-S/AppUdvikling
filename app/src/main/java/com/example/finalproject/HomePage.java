package com.example.finalproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Domain.ExchangeRateResponse;
import com.example.finalproject.Domain.User;
import com.example.finalproject.functions.CurrencyCodeCallback;
import com.example.finalproject.functions.CurrenyFetch;
import com.example.finalproject.functions.ExchangeRateCache;
import com.example.finalproject.functions.locationPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class HomePage extends AppCompatActivity {
    private EditText etAmountA;
    private TextView tvCurrencyA;
    private EditText etAmountB;
    private Spinner spinnerCurrencyB;
    private double conversionRate = 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        etAmountA = findViewById(R.id.etAmountA);
        tvCurrencyA = findViewById(R.id.tvCurrencyA);
        etAmountB = findViewById(R.id.etAmountB);
        spinnerCurrencyB = findViewById(R.id.spinnerCurrencyB);

        // Request permissions and fetch exchange rates
        locationPermissions.requestPermissions(this, new CurrencyCodeCallback() {
            @Override
            public void onCurrencyCodeAvailable(String currencyCode) {
                tvCurrencyA.setText(currencyCode); // Set the local currency
                getEXchangeRates(currencyCode); // Fetch exchange rates
            }

            @Override
            public void onError(Exception e) {
                Log.e("HomePage", "Error fetching location or currency code", e);
            }
        });

        // Setup text change listener for real-time conversion
        etAmountA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                convertCurrency();
            }
        });
    }

    private void getEXchangeRates(String currencyCode) {
        ExchangeRateResponse cachedRates = ExchangeRateCache.getInstance().getCachedRates();
        if (cachedRates != null) {
            // Use cached rates if available
            updateUIWithRatesAndCurrencies(cachedRates);
        } else {
            // Fetch new rates if not cached or cache is expired
            new Thread(() -> {
                ExchangeRateResponse rates = CurrenyFetch.getExchangeRate(currencyCode);
                if (rates != null && rates.conversion_rates != null) {
                    // Cache the newly fetched rates
                    ExchangeRateCache.getInstance().setCachedRates(rates);
                    runOnUiThread(() -> updateUIWithRatesAndCurrencies(rates));
                }
            }).start();
        }
    }

    private void updateUIWithRatesAndCurrencies(ExchangeRateResponse rates) {
        List<String> currencyList = new ArrayList<>(rates.conversion_rates.keySet());
        Collections.sort(currencyList);

        runOnUiThread(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, currencyList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCurrencyB.setAdapter(adapter);

            int defaultPosition = adapter.getPosition("USD");
            if(defaultPosition == -1) {
                defaultPosition = adapter.getPosition("DKK");
            }
            spinnerCurrencyB.setSelection(defaultPosition);

            int finalDefaultPosition = defaultPosition;
            spinnerCurrencyB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedCurrency = (String) parentView.getItemAtPosition(position);
                    Double selectedRate = rates.conversion_rates.get(selectedCurrency);
                    if (selectedRate != null) {
                        conversionRate = selectedRate;
                        convertCurrency();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    spinnerCurrencyB.setSelection(finalDefaultPosition);
                }
            });
        });
    }

    private void convertCurrency() {
        String amountText = etAmountA.getText().toString();
        if (!amountText.isEmpty()) {
            double amount = Double.parseDouble(amountText);
            double convertedAmount = amount * conversionRate;
            etAmountB.setText(String.format(Locale.getDefault(), "%.2f", convertedAmount));
        }
    }
}