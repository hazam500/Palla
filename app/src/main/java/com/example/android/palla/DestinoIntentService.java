package com.example.android.palla;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DestinoIntentService extends IntentService {


    private ResultReceiver mReceiver;

    private String locationAmpliada;


    public DestinoIntentService() {
        super("DestinoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            mReceiver = intent.getParcelableExtra("receiver");

            String location = intent.getStringExtra("direccion");

            locationAmpliada = location + ",Bogota";

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = null;

            String errorMessage = "";

            try {
                // Using getFromLocation() returns an array of Addresses for the area immediately
                // surrounding the given latitude and longitude. The results are a best guess and are
                // not guaranteed to be accurate.

                addresses = geocoder.getFromLocationName(locationAmpliada, 3);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
            }

            Address address = addresses.get(0);

            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            Bundle bundle = new Bundle();
            bundle.putDouble("Latitud", latitude);
            bundle.putDouble("Longitud", longitude);
            mReceiver.send(0, bundle);
        }



    }


}
