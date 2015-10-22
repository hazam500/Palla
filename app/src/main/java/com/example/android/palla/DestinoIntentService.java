package com.example.android.palla;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
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

            Log.e("lugar", location);

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

            if(addresses.size()==2 || addresses.size()==3 ) {
                Address address2 = addresses.get(1);
                Address address3 = addresses.get(2);

                double latitude2 = address2.getLatitude();
                double longitude2 = address2.getLongitude();

                double latitude3 = address3.getLatitude();
                double longitude3 = address3.getLongitude();

                Log.e("lat", String.valueOf(latitude2));
                Log.e("long", String.valueOf(longitude2));

                Log.e("lat", String.valueOf(latitude3));
                Log.e("long", String.valueOf(longitude3));
            }

            Bundle bundle = new Bundle();



            bundle.putDouble("Latitud", latitude);
            bundle.putDouble("Longitud", longitude);
            mReceiver.send(0, bundle);
        }



    }


}
