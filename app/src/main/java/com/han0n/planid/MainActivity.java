package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail;
    private EditText txtContrasenia;

    private String email = "";
    private String contrasenia = "";

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail = findViewById(R.id.editEmail);
        txtContrasenia = findViewById(R.id.editContrasenia);
        Button btnLogin = findViewById(R.id.btnRegistrar);

        btnLogin.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btnRegistrar:
                email = txtEmail.getText().toString();
                contrasenia = txtContrasenia.getText().toString();

                if(!email.isEmpty() && !contrasenia.isEmpty()){
                    registrarUsuario();
                }
                else{
                    Toast.makeText(this, "Completa los campos para el registro",
                            Toast.LENGTH_LONG);
                }
                break;
        }
    }

    private void registrarUsuario(){
        firebaseAuth.createUserWithEmailAndPassword(email,contrasenia)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override // Crear usuario
                    public void onComplete(@NonNull Task<AuthResult> crearUsuario) {
                        if(crearUsuario.isSuccessful()){

                            Map<String, Object> valores = new HashMap<>();
                            valores.put("email", email);
                            valores.put("contrasenia", contrasenia);

                            String id = firebaseAuth.getCurrentUser().getUid();

                            databaseReference.child("Usuarios").child(id).setValue(valores)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override // Crear datos en la BD
                                        public void onComplete(@NonNull Task<Void> crearDatosBD) {
                                            if(crearDatosBD.isSuccessful()){
                                                startActivity(new Intent(MainActivity.this, ListActivity.class));
                                                finish();
                                            }else{

                                            }
                                        }
                                    });
                        }else{

                        }
                    }
                });
    }
}

