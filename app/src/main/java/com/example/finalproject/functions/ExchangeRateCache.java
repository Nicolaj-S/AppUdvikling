package com.example.finalproject.functions;

import com.example.finalproject.Domain.ExchangeRateResponse;

public class ExchangeRateCache {
    private static ExchangeRateCache instance;
    private ExchangeRateResponse cachedRates;
    private long lastFetchTime = 0;
    private static final long CACHE_DURATION = 1000 * 60 * 60; // 1 hour in milliseconds

    private ExchangeRateCache() {}

    public static synchronized ExchangeRateCache getInstance() {
        if (instance == null) {
            instance = new ExchangeRateCache();
        }
        return instance;
    }

    public ExchangeRateResponse getCachedRates() {
        // Check if cache is older than CACHE_DURATION and clear if it is
        if (System.currentTimeMillis() - lastFetchTime > CACHE_DURATION) {
            cachedRates = null;
        }
        return cachedRates;
    }

    public void setCachedRates(ExchangeRateResponse rates) {
        this.cachedRates = rates;
        this.lastFetchTime = System.currentTimeMillis();
    }
}
