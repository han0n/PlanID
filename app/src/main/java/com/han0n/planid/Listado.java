package com.han0n.planid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.han0n.planid.databinding.ActivityListadoBinding;

public class Listado extends AppCompatActivity {

    private ActivityListadoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getSupportActionBar().hide(); // Oculta la toolbar_actionbar
    }
}