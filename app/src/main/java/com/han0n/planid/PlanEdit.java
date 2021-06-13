package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.han0n.planid.databinding.ActivityPlanBinding;

import java.util.ArrayList;
import java.util.HashMap;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD;

public class PlanEdit extends AppCompatActivity {

    private ActivityPlanBinding binding;
    MaterialTimePicker picker;

    private FirebaseAuth firebaseAuth;

    public PlanEdit() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getSupportActionBar().hide(); // Oculta la toolbar_actionbar

        //Para que al pulsar enter del teclado Android se cierre en el caso de Descripción
        EditText descripcion = findViewById(R.id.txtDesc);
        descripcion.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descripcion.setRawInputType(InputType.TYPE_CLASS_TEXT);

        //Inicializa Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //ACCIÓN del botón de guardar
        binding.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        //ACCIÓN del EditText de la Alarma
        binding.txtAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build();
                picker.show(getSupportFragmentManager(), "timePicker");
                /* Si se pulsa el botón de Aceptar una vez en el picker */
                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hora = picker.getHour();
                        minuto = picker.getMinute();
                        String hMFormato = String.format("%02d : %02d", hora, minuto);
                        binding.campoAlarma.setHint(hMFormato);// Se setea al Hint del campo Alarma
                        picker = new MaterialTimePicker.Builder()// y al nuevo picker por si cambia
                                        .setHour(hora)
                                        .setMinute(minuto)
                                        .build();
                        binding.btnPonerAlarma.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //ACCIÓN del Botón de ATRAS
        binding.btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }// Método onCreate

    private String actividad="", descripcion="";
    private int hora=0, minuto=1;// Para cuando se ponga a las 00:01 no se muestre después
    private void validarDatos() {// pues indicará que no se ha guardado valor para la hora

        actividad = binding.txtActividad.getText().toString().trim();
        descripcion = binding.txtDesc.getText().toString().trim();

        if (TextUtils.isEmpty(actividad)){
            Toast.makeText(this, "No se ha ingresado la actividad", Toast.LENGTH_SHORT).show();
        }else {
            subidaFirebase();
        }

    }

    private void subidaFirebase() {

        HashMap<String, Object> valores = new HashMap<>();
        // Se usará como id
        long timestamp = System.currentTimeMillis();

        valores.put("id", timestamp);
        valores.put("actividad", actividad);
        valores.put("descripcion", descripcion);
        valores.put("hora", hora);
        valores.put("minuto", minuto);
        valores.put("uid", ""+firebaseAuth.getUid());

        // Añadiendo a la BD
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notas");
        ref.child(""+timestamp)
                .setValue(valores)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override// Si se añade sin problemas la nota...
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PlanEdit.this, "Nota creada con éxito", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PlanEdit.this, Listado.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlanEdit.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

}