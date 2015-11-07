package com.example.android.palla;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DestinoActivity extends FragmentActivity implements OnMapReadyCallback {

    private DireccionResultReceiver direccionResultReceiver;
    private EditText destinoEditText;
    private Double destinoLatitud;
    private Double destinoLongitud;
    private GoogleMap migoogleMap;
    private String key;
    private Button confirmarDestino;
    private Button localizarDestino;
    private Button ubicacionActual;
    static final int UBICACIONACTUAL = 1;
    static final int SECTOR = 2;
    private LatLng ubicacion_actual;
    private GeoFire buscandoGeoFire;
    private GeoFire actualGeoFire;
    private Marker busMarker;
    private OrigenResultReceiver origenResultReceiver;
    private LatLng origen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destino);

        Intent intent = getIntent();
        key = intent.getStringExtra("Uid");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinoEditText = (EditText) findViewById(R.id.destinoEdiTtext);
        direccionResultReceiver = new DireccionResultReceiver(new Handler());
        origenResultReceiver = new OrigenResultReceiver(new Handler());
        confirmarDestino = (Button) findViewById(R.id.confirmaDestino);
        ubicacionActual = (Button) findViewById(R.id.ubicacionActual);
        localizarDestino = (Button) findViewById(R.id.localizar_destino);

        Firebase.setAndroidContext(this);
    }

    public void localizarDestino(View view) {

        String destino = destinoEditText.getText().toString();
        Intent intent = new Intent(this, DestinoIntentService.class);
        intent.putExtra("receiver", direccionResultReceiver);
        intent.putExtra("direccion", destino);
        localizarDestino.setVisibility(View.GONE);
        confirmarDestino.setVisibility(View.VISIBLE);
        startService(intent);
    }

    class DireccionResultReceiver extends ResultReceiver /*recibe los datos del IntentService*/ {
        public DireccionResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            destinoLatitud = resultData.getDouble("Latitud");
            destinoLongitud = resultData.getDouble("Longitud");
            buscandoGeoFire = new GeoFire(new Firebase("https://brilliant-heat-7882.firebaseio.com/pasajero_destino/"));
            buscandoGeoFire.setLocation(key, new GeoLocation(destinoLatitud, destinoLongitud));
            migoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(destinoLatitud, destinoLongitud), 17));
            migoogleMap.addMarker(new MarkerOptions().position(new LatLng(destinoLatitud, destinoLongitud)));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        migoogleMap = googleMap;
        migoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        migoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.673981, -74.056467), 17));
    }

    public void confirmarDestino(View view) {

        Intent intent = new Intent(this, OrigenIntentService.class);
        intent.putExtra("receiver", origenResultReceiver);
        intent.putExtra("destinoLatitud", destinoLatitud);
        intent.putExtra("destinoLongitud", destinoLongitud);
        startService(intent);

        confirmarDestino.setVisibility(View.GONE);
        ubicacionActual.setVisibility(View.VISIBLE);
    }

    private class OrigenResultReceiver extends ResultReceiver {
        public OrigenResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            origen = resultData.getParcelable("origen");
            migoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 16));
            migoogleMap.addMarker(new MarkerOptions().position(origen));
        }
    }

    public void confirmarOrigen(View view) {
        Intent sectorIntent = new Intent(DestinoActivity.this, UbicarSector.class);
        sectorIntent.putExtra("key", key);
        sectorIntent.putExtra("ubicacionOrigen",origen);
        sectorIntent.putExtra("ubicacionDestino", new LatLng(destinoLatitud, destinoLongitud));
        startActivity(sectorIntent);/*Ubica el sector de destino*/

    }

    public void ubicacionActual(View view) {

        Firebase ref = new Firebase("https://brilliant-heat-7882.firebaseio.com/pasajero_destino/");

        ref.child("cb610a8e-6a91-4dbb-a47b-d7f04b48b416").child("l").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Double> locacion = (ArrayList<Double>) snapshot.getValue();
                busMarker = migoogleMap.addMarker(new MarkerOptions().position(new LatLng(locacion.get(0), locacion.get(1))));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        ref.child("cb610a8e-6a91-4dbb-a47b-d7f04b48b416").child("l").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Double> locacion = (ArrayList<Double>) snapshot.getValue();
                animateMarkerTo(busMarker, locacion.get(0), locacion.get(1));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


}
