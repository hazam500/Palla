package com.example.android.palla;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText contrasena;
    private LinearLayout login;
    private LinearLayout cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        contrasena = (EditText) findViewById(R.id.contrasena);
        login = (LinearLayout) findViewById(R.id.login);
        cargando = (LinearLayout) findViewById(R.id.cargando);

    }

    public void iniciarSesion(View view) {
        if (username.getText().toString().equals("")) {
            username.setError("Por favor introduzca su email");
            return;
        }
        if (contrasena.getText().toString().equals("")) {
            contrasena.setError("Por favor introduzca su contraseña");
            return;
        }

        login.setVisibility(View.GONE);
        cargando.setVisibility(View.VISIBLE);

        Firebase.setAndroidContext(this);

        final Firebase firebase = new Firebase("https://brilliant-heat-7882.firebaseio.com/");

        firebase.authWithPassword(username.getText().toString(), contrasena.getText().toString(), new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                esconderCarga();
                Intent intent = new Intent(MainActivity.this,DestinoActivity.class);
                intent.putExtra("Uid",authData.getUid());
                startActivity(intent);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                esconderCarga();
                Snackbar.make(login, "No autenticado", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void esconderCarga() {
        login.setVisibility(View.VISIBLE);
        cargando.setVisibility(View.GONE);
    }

    public void registrar(View view) {
        Intent intent = new Intent(this,RegistroActivity.class);
        startActivity(intent);
        overridePendingTransition(0,android.R.anim.slide_in_left);
    }
}
