package com.example.android.palla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void cuentaListener(View view)
    {
        Intent cuentaIntent = new Intent(this,CrearCuenta.class);
        startActivity(cuentaIntent);
    }

    public void iniciarSesionListener(View view)
    {
        Intent iniciarIntent = new Intent(this,IniciarSesion.class);
        startActivity(iniciarIntent);
    }
}
