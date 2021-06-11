package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.han0n.planid.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity{

    //Binding de la vista
    private ActivityMainBinding binding;

    private static final int RC_SING_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "GOOGLE_SIG_IN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //CONFIGURACIÓN del sign-in de Google
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //INICIALIZANDO Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //ACCIÓN DEL BOTÓN
            binding.btnRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //ACCEDIENDO
                Log.d(TAG, "onClick: accediendo Google SignIn");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityIfNeeded(intent, RC_SING_IN);
            }
        });

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //RESPUESTA devuelta desde el lanzamiento del Intent desde GoogleSignIn Api.getSignInIntent(...);
        if(requestCode == RC_SING_IN){
            Log.d(TAG, "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //INICIO de Google en PROCESO, AHORA Autoriza CON Firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthConCuentaGoogle(account);
            }
            catch (Exception e){
                //INICIO fallido
                Log.d(TAG, "onActivityResult:" + e.getMessage());
            }
        }
    }

    private void firebaseAuthConCuentaGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthConCuentaGoogle: dándose el auth a Firebase con cuenta de Google");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //LOGIN EXITOSO
                        Log.d(TAG, "onSuccess: Logged In");

                        //OBTENIENDO el login del usuario
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //OBTENIENDO INFO del usuario
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d(TAG, "onSuccess: Email" + email);
                        Log.d(TAG, "onSuccess: Logged In" + uid);

                        //COMPRUEBA SI el usuario es NUEVO O EXISTENTE
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            //USUARIO es NUEVO - Cuenta
                            Log.d(TAG, "onSuccess: Cuenta Creada...\n" + email);
                            Toast.makeText(MainActivity.this, "Cuenta creada...\n" + email, Toast.LENGTH_SHORT).show();

                            //INICIANDO ACTIVITY CONFIGURACIÓN
                            startActivity(new Intent(MainActivity.this, ListActivity.class));
                        }
                        else{
                            //USUARIO EXISTENTE - Logeado
                            Log.d(TAG, "onSuccess: Usuario Existente...\n" + email);
                            Toast.makeText(MainActivity.this, "Usuario Existente...\n" + email, Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //LOGIN FALLIDO
                        Log.d(TAG,"onFailure: Loggin failed" + e.getMessage());
                    }
                });
    }
}

