package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.han0n.planid.databinding.ActivityPlanBinding;

import java.util.ArrayList;
import java.util.HashMap;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD;

public class PlanEdit extends AppCompatActivity {

    private ActivityPlanBinding binding;
    private MaterialTimePicker reloj;

    private FirebaseAuth firebaseAuth;

    // Si viene del OnClick para editar el Plan:
    private String planId_;// Este es el que se pasa
    private DatabaseReference ref;
    private String hora_="", minuto_="";//**NO SETEA LA HORA**(Ya sí, era problema del formato)
    private int hora=0, minuto=1;

    // Para que guarde la hora y pase de construir un selector de hora de 0
    boolean relojSeteado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recuperamos la id si se viente de pulsar una Nota
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            binding.txtCabecera.setText(R.string.editar_nota);
            long id = extras.getLong("planId");
            planId_ = String.valueOf(id);
            //Log.d("AAA", ""+planId);
            editarPlan();
        }

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

                if(!relojSeteado)
                    reloj = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build();

                reloj.show(getSupportFragmentManager(), "timePicker");
                /* Si se pulsa el botón de Aceptar una vez en el picker */
                reloj.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        hora = reloj.getHour();
                        minuto = reloj.getMinute();
                        String hMFormato = String.format("%02d : %02d", hora, minuto);
                        binding.campoAlarma.setHint(hMFormato);// Se setea al Hint del campo Alarma

                        reloj = new MaterialTimePicker.Builder()// y al nuevo picker por si cambia
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(hora)
                                .setMinute(minuto)
                                .build();
                        relojSeteado = true;
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

        binding.btnPonerAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                establecerAlarma("Alarma puesta", hora, minuto);
            }
        });


    }// Método onCreate

    public void establecerAlarma(String mensaje, int hora, int minuto){

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, mensaje)
                .putExtra(AlarmClock.EXTRA_HOUR, hora)
                .putExtra(AlarmClock.EXTRA_MINUTES, minuto)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }

    }
    // Método para editar Plan: Carga los datos del plan y después lo elimina
    private void editarPlan() {

        ref = FirebaseDatabase.getInstance().getReference("notas");
        ref.child(planId_)
            .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("ACTA", "" + dataSnapshot.child("actividad").getValue());
                String actividad = "" + dataSnapshot.child("actividad").getValue();
                String descripcion = "" + dataSnapshot.child("descripcion").getValue();
                hora_ = "" + dataSnapshot.child("hora").getValue();
                //Log.d("AAA", "" + dataSnapshot.child("hora").getValue());
                hora = Integer.parseInt(hora_);// Aqui se parsean
                minuto_ = "" + dataSnapshot.child("minuto").getValue();
                minuto = Integer.parseInt(minuto_);

                binding.txtActividad.setText(actividad);
                binding.txtDesc.setText(descripcion);
                String hMFormato = String.format("%2s : %2s", hora_, minuto_);
                binding.campoAlarma.setHint(hMFormato);// Se setea al Hint del campo Alarma

                reloj = new MaterialTimePicker.Builder()// Se meten los viejos valores al picker
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(hora)
                        .setMinute(minuto)
                        .build();
                relojSeteado = true;
                binding.btnPonerAlarma.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private String actividad="", descripcion="";
    //private int hora=0, minuto=1;// Para cuando se ponga a las 00:01 no se muestre después
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
                        // Una vez asignamos los datos a los campos... Se elimina el id pasado
                        ref.child(String.valueOf(planId_)) //En caso de ser una nota editada, CLARO
                                .removeValue(); // Sin comprobación de si se elimina BIEN

                        Toast.makeText(PlanEdit.this, "Nota modificada con éxito", Toast.LENGTH_SHORT).show();
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