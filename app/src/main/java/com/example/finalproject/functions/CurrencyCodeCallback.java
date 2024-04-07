package com.example.finalproject.functions;

public interface CurrencyCodeCallback {
    void onCurrencyCodeAvailable(String currencyCode);

    void onError(Exception e);
}
