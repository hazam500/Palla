package com.example.android.palla;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText email;
    private EditText contrasena;
    private EditText recontrasena;
    private Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        email = (EditText)findViewById(R.id.email);
        contrasena = (EditText)findViewById(R.id.contrasena);
        recontrasena = (EditText)findViewById(R.id.recontrasena);

    }

    public void registrar(View view) {
        if(email.getText().toString().equals("")){
            email.setError("Por favor introduzca su email");
            return;
        }
        if(contrasena.getText().toString().equals("")){
            contrasena.setError("Por favor introduzca su contraseña");
            return;
        }
        if(recontrasena.getText().toString().equals("")){
            recontrasena.setError("Por favor introduzca su contraseña");
            return;
        }
        if(!contrasena.getText().toString().equals(recontrasena.getText().toString())){
            recontrasena.setError("Las contraseñas no coinciden, por favor introduzca su contraseña nuevamente");
            return;
        }

       Firebase.setAndroidContext(this);
        Firebase firebase = new Firebase("https://brilliant-heat-7882.firebaseio.com/UsuarioPasajero");
        firebase.createUser(email.getText().toString(), contrasena.getText().toString(), new Firebase.ValueResultHandler<Map<String,Object>>() {

            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                RegistroActivity.this.finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Snackbar.make(email,"Error al crear la cuenta",Snackbar.LENGTH_LONG).show();
            }
        });

    }
}
