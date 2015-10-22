package com.example.android.palla;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DestinoActivity extends FragmentActivity implements OnMapReadyCallback {

    private DireccionResultReceiver direccionResultReceiver;
    private EditText destinoEditText;
    private Double latitud;
    private Double longitud;
    private TextView latitudTextView;
    private TextView longitudTextView;
    private GoogleMap migoogleMap;
    private CameraPosition inicialCameraPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destino);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinoEditText = (EditText) findViewById(R.id.destinoEdiTtext);
        direccionResultReceiver = new DireccionResultReceiver(new Handler()); /*Paso necesario agregado por Cristian, no lo había colocado yo*/
    }


    public void localizarDestino(View view) {

        String destino = destinoEditText.getText().toString();

        Intent intent = new Intent(this, DestinoIntentService.class);

        intent.putExtra("receiver", direccionResultReceiver);

        intent.putExtra("direccion", destino);

        startService(intent);


    }


    class DireccionResultReceiver extends ResultReceiver /*recibe los datos del IntentService*/ {
        public DireccionResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from IntentService
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            inicialCameraPosition = new CameraPosition.Builder().target(new LatLng(0,0))
                    .zoom(15.5f).bearing(300).tilt(50).build();

            // Display the address string or an error message sent from the intent service.
            latitud = resultData.getDouble("Latitud");
            longitud = resultData.getDouble("Longitud");
            Log.v("RECEIVER", "Receiving " + latitud + " " + longitud);


            latitudTextView = (TextView) findViewById(R.id.latitudTextView);
            longitudTextView = (TextView) findViewById(R.id.longitudTextView);

            latitudTextView.setText(String.valueOf(latitud));
            longitudTextView.setText(String.valueOf(longitud));

            migoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud,longitud),15));

            migoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title("Tu destino"));






        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        migoogleMap = googleMap;
    }


}
