package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class Listado extends AppCompatActivity implements RecycleViewClickInterface{

    private ActivityListadoBinding binding;

    //Firebase
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
    private FirebaseAuth firebaseAuth;

    //Array para cargar las notas y su Adaptador
    private ArrayList<NotaMDL> notaArrayList;
    private NotaADPT notaADPT;
    private String cuentaUid;

    //Para eliminar con el swipe
    NotaMDL modelo; /* Reutilizado | Tambien en cargarNotas(); */
    DatabaseReference ref; /* Reutilizado | Tambien en cargarNotas(); */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cargarNotas();


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

        // DESLIZAR para borrar
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT |ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Eliminado de la vista + recogiendo del Modelo NotaMDL:
                NotaMDL notaSwipeada = notaArrayList.remove(viewHolder.getAdapterPosition());
                notaADPT.notifyDataSetChanged();
                // Eliminado de la DB:
                long id = notaSwipeada.getId(); //OBTENEMOS EL id del elemento que se desliza
                //Log.d("AAA", ""+id);
                ref.child(String.valueOf(id)) //INICIALIZADO Een cargarNotas();
                        .removeValue(); // Sin comprobación de si se elimina BIEN
            }
        };

        // Instanciamiento del swipe directamente en el onCreate
        new ItemTouchHelper(callback).attachToRecyclerView(binding.recNotas);
    }

    private void cargarNotas(){
        notaArrayList = new ArrayList<>();
        // Aquí las coge todas
        ref = FirebaseDatabase.getInstance().getReference("notas");
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
                        modelo = ds.getValue(NotaMDL.class);
                        // AGREGA sus notas:
                        notaArrayList.add(modelo);
                    }

                }
                // SETTEA el Adaptador
                notaADPT = new NotaADPT(Listado.this, notaArrayList, Listado.this);
                // SETTEAMOS al recycleview
                binding.recNotas.setAdapter(notaADPT);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /* ACCIÓN PARA EDITAR una Nota */
    @Override
    public void onItemClick(int position) {
        //Log.d("AAA", ""+position);
        NotaMDL notaSeleccionada = notaArrayList.get(position);
        notaADPT.notifyDataSetChanged();
        // Eliminado de la DB:
        long id = notaSeleccionada.getId(); //OBTENEMOS EL id del elemento que se clica
        Intent intent = new Intent(this, PlanEdit.class);
        intent.putExtra("planId", id);
        startActivity(intent);
        //Log.d("AAA", ""+id);// Comprueba que corresponde a la id de la BD
    }

    @Override
    public void onLongItemClick(int position) {

    }
}