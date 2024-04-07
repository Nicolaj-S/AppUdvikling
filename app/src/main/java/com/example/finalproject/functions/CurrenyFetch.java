package com.example.finalproject.functions;

import android.util.Log;

import com.example.finalproject.Domain.ExchangeRateResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class CurrenyFetch {
    public static String apiKey = "0a16d2882be43d94ec7e0721";
    public static ExchangeRateResponse getExchangeRate(String fromCurrency) {
        String requestUrl = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", apiKey, fromCurrency);
        ExchangeRateResponse exchangeRateResponse = null;

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                exchangeRateResponse = gson.fromJson(response.toString(), ExchangeRateResponse.class);
            } else {
                Log.d("GET request", "GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exchangeRateResponse;
    }
}