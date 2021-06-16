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
import com.google.firebase.auth.FirebaseUser;
import com.han0n.planid.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ProgressDialog alerta;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LaunchScreen);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PlanID);// Para que no se vea el LaunchScreen de fondo
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getSupportActionBar().hide(); // Oculta la toolbar_actionbar al inicio de la App

        //INICIALIZACIÓN de FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        alerta = new ProgressDialog(this);
        alerta.setTitle(R.string.espere);
        alerta.setCanceledOnTouchOutside(false);

        //ACCIÓN del texto txtReg
        binding.txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registro.class));
            }
        });

        //ACCIÓN del Botón de LOGIN
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    /* VALIDA los datos antes del LOGIN */
    private String email="", pswd="";
    private void validarDatos() {

        email = binding.txtEmail.getText().toString().trim();
        pswd = binding.txtPswd.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if (TextUtils.isEmpty(email))
                Toast.makeText(this, R.string.falta_email, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.email_no_valido, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pswd)){
            Toast.makeText(this, R.string.falta_pswd, Toast.LENGTH_SHORT).show();
        }
        else{// Los datos se han validado
            loginCuenta();
        }
        
    }

    private void loginCuenta() {
        alerta.setMessage(this.getResources().getString(R.string.acceso));
        alerta.show();// Hasta que no termina de crear cuenta no se cierra

        firebaseAuth.signInWithEmailAndPassword(email, pswd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(Login.this, Listado.class));
                        finish();// Cuando entra al listado finaliza esta Activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override// NO se ha loggrado hacer LOGIN
                    public void onFailure(Exception e) {
                        alerta.dismiss();
                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        /* CHECKEO de si hay una Cuenta ya Logeada y va directo a su Listado */
        FirebaseUser cuenta = FirebaseAuth.getInstance().getCurrentUser();
        if (cuenta != null ) {
            startActivity(new Intent(Login.this, Listado.class));
            finish();// Cuando entra al Listado finaliza esta Activity
        }
    }

}