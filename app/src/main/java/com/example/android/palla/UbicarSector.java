package com.example.android.palla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class UbicarSector extends FragmentActivity {

    private List<List<LatLng>> listaPoligonos = new ArrayList<>();

    private List<LatLng> Sector1 = new ArrayList<>();
    private List<LatLng> Sector2 = new ArrayList<>();
    private List<LatLng> Sector3 = new ArrayList<>();
    private List<LatLng> Sector4 = new ArrayList<>();
    private List<LatLng> Sector5 = new ArrayList<>();

    private GoogleMap miGoogleMap;
    private LatLng destino;
    private String key;
    private int sectorDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        Sector1.add(new LatLng(4.684930, -74.048177));
        Sector1.add(new LatLng(4.683577, -74.045248));
        Sector1.add(new LatLng(4.681166, -74.040602));
        Sector1.add(new LatLng(4.679856, -74.038376));
        Sector1.add(new LatLng(4.677700, -74.039782));
        Sector1.add(new LatLng(4.675505, -74.040420));
        Sector1.add(new LatLng(4.674365, -74.041511));
        Sector1.add(new LatLng(4.675232, -74.042495));
        Sector1.add(new LatLng(4.680558, -74.052655));
        Sector1.add(new LatLng(4.681247, -74.054600));

        Sector2.add(new LatLng(4.674365, -74.041511));
        Sector2.add(new LatLng(4.675232, -74.042495));
        Sector2.add(new LatLng(4.680558, -74.052655));
        Sector2.add(new LatLng(4.681247, -74.054600));
        Sector2.add(new LatLng(4.678862, -74.058066));
        Sector2.add(new LatLng(4.671323, -74.043523));
        Sector2.add(new LatLng(4.673462, -74.042594));

        Sector3.add(new LatLng(4.671323, -74.043523));
        Sector3.add(new LatLng(4.673629, -74.048038));
        Sector3.add(new LatLng(4.670363, -74.049678));
        Sector3.add(new LatLng(4.666815, -74.052191));
        Sector3.add(new LatLng(4.663068, -74.048721));
        Sector3.add(new LatLng(4.665037, -74.046747));

        Sector4.add(new LatLng(4.704506, -74.042914));
        Sector4.add(new LatLng(4.703335, -74.032382));
        Sector4.add(new LatLng(4.694155, -74.034503));
        Sector4.add(new LatLng(4.696636, -74.043300));

        Sector5.add(new LatLng(4.676415, -74.055534));
        Sector5.add(new LatLng(4.675025, -74.052401));
        Sector5.add(new LatLng(4.671889, -74.053454));
        Sector5.add(new LatLng(4.672872, -74.056114));


        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        LatLng ubicacionOrigen = intent.getParcelableExtra("ubicacionOrigen");
        LatLng ubicacionDestino = intent.getParcelableExtra("ubicacionDestino");

        listaPoligonos.add(Sector1);
        listaPoligonos.add(Sector2);
        listaPoligonos.add(Sector3);
        listaPoligonos.add(Sector4);
        listaPoligonos.add(Sector5);

        sectorDestino = 0;

        for (int i = 0; i < listaPoligonos.size(); i++) {

            Boolean resultadoDestino = PolyUtil.containsLocation(ubicacionDestino, listaPoligonos.get(i), true);

            if (resultadoDestino.equals(true)) {
                sectorDestino = i + 1;
            }
        }

        for (int j = 0; j < listaPoligonos.size(); j++) {

            Boolean resultadoOrigen = PolyUtil.containsLocation(ubicacionOrigen, listaPoligonos.get(j), true);

            if (resultadoOrigen.equals(true)) {
                GeoFire sectorOrigenGeofire = new GeoFire(new Firebase("https://brilliant-heat-7882.firebaseio.com/SectoresOrigenLocalizacion/").child(String.valueOf(sectorDestino)).child(String.valueOf(j + 1)).child(key));
                sectorOrigenGeofire.setLocation("key",new GeoLocation(ubicacionOrigen.latitude,ubicacionOrigen.longitude));
                Firebase sectorDestinoFirebase = new Firebase("https://brilliant-heat-7882.firebaseio.com/SectoresDestino/").child(String.valueOf(sectorDestino)).child(String.valueOf(j + 1)).child(key);
                sectorDestinoFirebase.setValue(new PasajeroSector(System.currentTimeMillis())); }
        }

        finish();
    }
}