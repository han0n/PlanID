package com.han0n.planid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.han0n.planid.databinding.ActivityPlanBinding;

import java.util.ArrayList;

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



    }

    private String actividad="", descripcion="";
    private int hora=0, minuto=0;
    private void validarDatos() {

    }
}