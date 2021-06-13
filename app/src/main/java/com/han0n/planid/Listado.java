package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.han0n.planid.databinding.ActivityListadoBinding;

import java.util.ArrayList;
import java.util.Objects;

public class Listado extends AppCompatActivity {

    private ActivityListadoBinding binding;

    //Firebase
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
    private FirebaseAuth firebaseAuth;

    //Array para cargar las notas y su Adaptador
    private ArrayList<NotaMDL> notaArrayList;
    private NotaADPT notaADPT;
    private String cuentaUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getSupportActionBar().hide(); // Oculta la toolbar_actionbar


        cuentaUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference nuevaRef = mRef.child(cuentaUid);
        nuevaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("", "Nombre: " + dataSnapshot.child("nombre").getValue());
                String nombre = String.valueOf(dataSnapshot.child("nombre").getValue())+"!";
                binding.txtNombre.setText(nombre);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        // ACCIÓN del botón de CERRAR SESIÓN
        firebaseAuth = FirebaseAuth.getInstance();
        binding.btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(Listado.this, Login.class));
                finish();
            }
        });

        binding.btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Listado.this, PlanEdit.class));
            }
        });

        cargarNotas();

    }

    private void cargarNotas(){
        notaArrayList = new ArrayList<>();
        // Aquí las coge todas
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notas");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Limpia las notas antes de añadir las nuevas
                notaArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // OBTIENE el uid de todas las notas:
                    String uid = ds.getValue(NotaMDL.class).uid;
                    /* Si el uid de las notas es igual al de la Cuenta Actual (cuentaUid)*/
                    if(uid.equals(cuentaUid)){
                        // OBTIENE sus notas:
                        NotaMDL modelo = ds.getValue(NotaMDL.class);
                        // AGREGA sus notas:
                        notaArrayList.add(modelo);
                    }

                }
                // SETTEA el Adaptador
                notaADPT = new NotaADPT(Listado.this, notaArrayList);
                // SETTEAMOS al recycleview
                binding.recNotas.setAdapter(notaADPT);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}