package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.han0n.planid.databinding.ActivityRegistroBinding;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private ProgressDialog alerta;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide(); // Oculta la toolbar_actionbar

        //INICIALIZACIÓN de FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        alerta = new ProgressDialog(this);
        alerta.setTitle("Por favor, espere");
        alerta.setCanceledOnTouchOutside(false);

        //ACCIÓN del Botón de ATRAS
        binding.btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //ACCIÓN del Botón de REGISTRO
        binding.btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    /* VALIDA los datos antews de crear la cuenta */
    private String nombre="", email="", pswd="";
    private void validarDatos(){

        nombre = binding.txtNombre.getText().toString().trim();
        email = binding.txtEmail.getText().toString().trim();
        pswd = binding.txtPswd.getText().toString().trim();
        String pswdRe = binding.txtPswdRe.getText().toString().trim();

        if(TextUtils.isEmpty(nombre)){
            Toast.makeText(this, "No se ha ingresado un nombre", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "No se ha ingresado un e-mail válido", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pswd)){
            Toast.makeText(this, "No se ha ingresado la contraseña", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pswdRe)){
            Toast.makeText(this, "Confirma la contraseña", Toast.LENGTH_SHORT).show();
        }
        else if (!pswd.equals(pswdRe)){
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
        else{
            crearCuenta();
        }

    }

    private void crearCuenta() {
        alerta.setMessage("Creando cuenta...");
        alerta.show();// Hasta que no termina de crear cuenta no se cierra

        //CREACIÓN DEL Usuario en Firebase
        firebaseAuth.createUserWithEmailAndPassword(email, pswd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override// Creación de la CUENTA EXITOSA
                    public void onSuccess(AuthResult authResult) {
                        upInfoCuenta();// Se añade a Firebase realtime DB
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override// Creación de la CUENTA FALLIDA
                    public void onFailure(Exception e) {
                        alerta.dismiss();// No se ha creado la cuenta
                        Toast.makeText(Registro.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void upInfoCuenta() {
        alerta.setMessage("Guardando información de la cuenta...");

        String uid = firebaseAuth.getUid();
        //Recogiendo info que va a la BD
        HashMap<String, Object> valores = new HashMap<>();
        valores.put("uid", uid);
        valores.put("email", email);
        valores.put("pswd", pswd);
        valores.put("nombre", nombre);

        //Añadiendo a DB los datos del registro
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
        ref.child(uid)
                .setValue(valores)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override// Se añaden los datos del reg con ÉXITO
                    public void onSuccess(Void aVoid) {
                        alerta.dismiss();
                        Toast.makeText(Registro.this, "Cuenta creada con éxito...", Toast.LENGTH_SHORT).show();
                        // Va al listado porque la cuenta ya ha sido creada, no entra al Login
                        startActivity(new Intent(Registro.this, Listado.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override// No se logran añadir los datos
                    public void onFailure(Exception e) {
                        alerta.dismiss();
                        Toast.makeText(Registro.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}