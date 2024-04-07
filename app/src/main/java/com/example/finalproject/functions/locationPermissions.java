package com.example.finalproject.functions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;

public class locationPermissions{
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    public static void requestPermissions(Activity activity, CurrencyCodeCallback callback) {
        if (checkLocationPermission(activity)) {
            getLastLocation(activity, callback);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private static boolean checkLocationPermission(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



    public static void getLastLocation(Activity activity, CurrencyCodeCallback callback) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        if (checkLocationPermission(activity)) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    executorService.execute(() -> getCountryInfo(activity, latitude, longitude, callback));
                }
            });
        }
    }

    private static void getCountryInfo(Context context, double latitude, double longitude, CurrencyCodeCallback callback) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String countryCode = address.getCountryCode();
                Currency currency = Currency.getInstance(new Locale("", countryCode));
                String currencyCode = currency.getCurrencyCode();

                handler.post(() -> callback.onCurrencyCodeAvailable(currencyCode));
            }
        } catch (IOException e) {
            handler.post(() -> callback.onError(e));
        }
    }
}