package com.han0n.planid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD;

public class PlanEdit extends AppCompatActivity implements View.OnClickListener {

    private ActivityPlanBinding binding;
    private MaterialTimePicker reloj;

    private FirebaseAuth firebaseAuth;

    // Si viene del OnClick para editar el Plan:
    private String planId_;// Este es el que se pasa
    private DatabaseReference ref;
    private String hora_="", minuto_="";//**NO SETEA LA HORA**(Ya sí, era problema del formato)
    private int hora=25, minuto=60;//**CORREGIDO PARA QUE LAS NOTAS CON 0 HORAS Y 1 MINUTO LOS
    //CONSIDERE COMO HECHOS Y NO COMO NUEVOS AL EDITAR: Serán visible el btnSetAlarma y chkDias**

    // Para que guarde la hora y pase de construir un selector de hora de 0
    boolean relojSeteado;

    // Para setear los días que se pondrá la alarma
    public ArrayList<Integer> dias;

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
                        //Se hace visible el selector de días
                        binding.chkDias.setVisibility((View.VISIBLE));
                        dias = new ArrayList<>();//Se inicializa, ya está instanciado para toda la clase

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

                validarDatos();// Primero valida que tenga actividad
                if (!TextUtils.isEmpty(actividad)) {// Más potente que el ==
                    establecerAlarma(actividad, hora, minuto);
                    Intent intent = new Intent(PlanEdit.this, Listado.class);
                    finish();
                }

            }
        });

        //ACCIÓN de enviar por Whatsapp
        binding.btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = "*" + binding.txtActividad.getText().toString() + "*" +
                        "\n_" + binding.txtDesc.getText().toString() + "_";
                enviarWhats(mensaje);
            }
        });

        binding.chkLunes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        findViewById(R.id.chkLunes).setOnClickListener(this);
        findViewById(R.id.chkMartes).setOnClickListener(this);
        findViewById(R.id.chkLunes).setOnClickListener(this);
        findViewById(R.id.chkLunes).setOnClickListener(this);
        findViewById(R.id.chkViernes).setOnClickListener(this);
        findViewById(R.id.chkSabado).setOnClickListener(this);
        findViewById(R.id.chkDomingo).setOnClickListener(this);

    }// Método onCreate

    /* Método que comprueba si está instalado WhatsApp y envia mensaje*/
    private void enviarWhats(String mensaje) {
        //Log.d("AAA", mensaje);
        PackageManager pm=getPackageManager();

        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            // Comprueba si el paquete existe o no. Si no existe, salta el bloque de la excepción
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
            startActivity(Intent.createChooser(waIntent, getResources().getText(R.string.compartir_con)));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, R.string.sin_whatsapp, Toast.LENGTH_SHORT)
                    .show();
        }
    }


    public void establecerAlarma(String mensaje, int hora, int minuto){

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, mensaje)
                .putExtra(AlarmClock.EXTRA_HOUR, hora)
                .putExtra(AlarmClock.EXTRA_MINUTES, minuto)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        //Aquí ahora se comprueban los días que se ha seleccionado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(AlarmClock.EXTRA_DAYS, dias);
        }

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
                /* Corregido que no se seteasen bien, se deben usar de tipo int + el Locale */
                String hMFormato = String.format(Locale.getDefault(),"%02d : %02d", hora, minuto);

                if(hora!=25 && minuto!=60) {// Valores que se guardan por defecto cuando NO se selecciona
                    binding.campoAlarma.setHint(hMFormato);// Se setea al Hint del campo Alarma

                    reloj = new MaterialTimePicker.Builder()// Se meten los viejos valores al picker
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(hora)
                            .setMinute(minuto)
                            .build();
                    relojSeteado = true;
                    binding.btnPonerAlarma.setVisibility(View.VISIBLE);
                    // Se pondrán visibles los chk de los días de la semana si es una Nota editada
                    //binding.chkDias.setVisibility(View.VISIBLE);//**CRASHEA LA APP, REVISAR**

                }else{
                    relojSeteado = false;
                    binding.campoAlarma.setHint("00 : 00");
                }

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
            Toast.makeText(this, R.string.actividad_null, Toast.LENGTH_SHORT).show();
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

                        if (!TextUtils.isEmpty(planId_))
                            Toast.makeText(PlanEdit.this, R.string.nota_modificada, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(PlanEdit.this, R.string.nota_creada, Toast.LENGTH_SHORT).show();

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

    /* Controla los CHECKBOX de los DÍAS DE LA SEMANA para setearlos en la Alarma*/
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.chkLunes:
                if (!dias.contains(Calendar.MONDAY))
                    dias.add(Calendar.MONDAY);
                else {
                    int i = dias.indexOf(Calendar.MONDAY);
                    dias.remove(i);
                }
                break;
            case R.id.chkMartes:
                if (!dias.contains(Calendar.TUESDAY))
                    dias.add(Calendar.TUESDAY);
                else {
                    int i = dias.indexOf(Calendar.TUESDAY);
                    dias.remove(i);
                }
                break;
            case R.id.chkMiercoles:
                if (!dias.contains(Calendar.WEDNESDAY))
                    dias.add(Calendar.WEDNESDAY);
                else {
                    int i = dias.indexOf(Calendar.WEDNESDAY);
                    dias.remove(i);
                }
                break;
            case R.id.chkJueves:
                if (!dias.contains(Calendar.THURSDAY))
                    dias.add(Calendar.THURSDAY);
                else {
                    int i = dias.indexOf(Calendar.THURSDAY);
                    dias.remove(i);
                }
                break;
            case R.id.chkViernes:
                if (!dias.contains(Calendar.FRIDAY))
                    dias.add(Calendar.FRIDAY);
                else {
                    int i = dias.indexOf(Calendar.FRIDAY);
                    dias.remove(i);
                }
                break;
            case R.id.chkSabado:
                if (!dias.contains(Calendar.SATURDAY))
                    dias.add(Calendar.SATURDAY);
                else {
                    int i = dias.indexOf(Calendar.SATURDAY);
                    dias.remove(i);
                }
                break;
            case R.id.chkDomingo:
                if (!dias.contains(Calendar.SUNDAY))
                    dias.add(Calendar.SUNDAY);
                else {
                    int i = dias.indexOf(Calendar.SUNDAY);
                    dias.remove(i);
                }
                break;
        }
        /* Si no hay seleccionado ninguno, se setea la alarma para el día ACTUAL */

    }// Método onClick de los CheckBox de los días de la semana

}