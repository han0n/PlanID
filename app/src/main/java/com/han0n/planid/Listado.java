package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.han0n.planid.databinding.ActivityListadoBinding;

import java.util.Objects;

public class Listado extends AppCompatActivity {

    private ActivityListadoBinding binding;

    //Firebase
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getSupportActionBar().hide(); // Oculta la toolbar_actionbar


        String cuentaUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference nuevaRef = mRef.child(cuentaUid);
        nuevaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("", "Nombre: " + dataSnapshot.child("nombre").getValue());
                String nombre = String.valueOf(dataSnapshot.child("nombre").getValue());
                binding.txtNombre.setText(nombre);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        binding.btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(Listado.this, Login.class));
                finish();
            }
        });

    }

}