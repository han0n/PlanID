package com.han0n.planid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.han0n.planid.databinding.CardviewNotaBinding;

import java.util.ArrayList;
import java.util.Locale;

public class NotaADPT extends RecyclerView.Adapter<NotaADPT.HolderNota>{

    private CardviewNotaBinding binding;

    private Context context;
    private ArrayList<NotaMDL> notaArrayList;


    public NotaADPT(Context context, ArrayList<NotaMDL> notaArrayList) {
        this.context = context;
        this.notaArrayList = notaArrayList;
    }

    /* Auto-implementados */
    @NonNull
    @Override
    public HolderNota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CardviewNotaBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderNota(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull NotaADPT.HolderNota holder, int position) {
        // Toma los valores/datos
        NotaMDL modelo = notaArrayList.get(position);
        //String id = modelo.getId();
        //String uid = modelo.getUid();
        //long timestamp = modelo.getTimestamp();
        String actividad = modelo.getActividad();
        String descripcion = modelo.getDescripcion();
        int hora = modelo.getHora();
        int minuto = modelo.getMinuto();

        String hMFormato;
        if(hora == 0 && minuto == 1)
            hMFormato = "";
        else
            hMFormato = String.format("%02d : %02d", hora, minuto);


        // Los setea
        holder.vistaActividad.setText(actividad);
        holder.vistaDescripcion.setText(descripcion);
        holder.vistaHora.setText(hMFormato);


    }

    @Override
    public int getItemCount() {
        return notaArrayList.size();
    }

    /* La clase ViewHolder para hold UI views para cardview_nota.xml */
    class HolderNota extends RecyclerView.ViewHolder{

        //Las vistas de la UI para la cardview_nota.xml
        TextView vistaActividad;
        TextView vistaDescripcion;
        TextView vistaHora;

        public HolderNota(@NonNull View itemView) {
            super(itemView);
            // Inicialización de las Vistas de la UI
            vistaActividad = binding.vistaActividad;
            vistaDescripcion = binding.vistaDescripcion;
            vistaHora = binding.vistaHora;

        }
    }
}
