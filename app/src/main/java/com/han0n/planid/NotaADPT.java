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

    private RecycleViewClickInterface recycleViewClickInterface;


    public NotaADPT(Context context, ArrayList<NotaMDL> notaArrayList, RecycleViewClickInterface recycleViewClickInterface) {
        this.context = context;
        this.notaArrayList = notaArrayList;
        this.recycleViewClickInterface = recycleViewClickInterface;
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

        if (actividad.length()>32) {
            actividad = modelo.getActividad().substring(0, 31);
            int ultimoEspacio = actividad.lastIndexOf(" ");
            actividad = modelo.getActividad().substring(0, ultimoEspacio+1) + "...";
        }

        String descripcionFull = modelo.getDescripcion();
        String descripcion = "";
        String descripcion_ = "";

        if (descripcionFull.length()>=44){
            //Buscamos el último espacio...
            descripcion = modelo.getDescripcion().substring(0, 43);
            int ultimoEspacio = descripcion.lastIndexOf(" ");

            descripcion = modelo.getDescripcion().substring(0, ultimoEspacio+1);

            if(descripcionFull.length()<88) {// Si la segunda parte de texto no tiene más de 88-4 chars
                descripcion_ = modelo.getDescripcion().substring(ultimoEspacio, descripcion.length());

            }else{// Los puntos suspensivos y el espacio último también cuentan... 88-4 chars
                //el doble de 44 = a este 88

                descripcion_ = modelo.getDescripcion().substring(ultimoEspacio+1, 82);

                int ultimoEspacio_ = descripcion_.lastIndexOf(" ");
                descripcion_ = modelo.getDescripcion().substring(ultimoEspacio+1 , ultimoEspacio_+1+ultimoEspacio+1) + "...";
            }

        }// Para adaptar textos largos en las cardviews

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
        holder.vistaDesc_.setText(descripcion_);
        holder.vistaHora.setText(hMFormato);


    }

    @Override
    public int getItemCount() {
        return notaArrayList.size();
    }

    /* La clase ViewHolder para hold UI views para cardview_nota.xml */
    class HolderNota extends RecyclerView.ViewHolder {

        //Las vistas de la UI para la cardview_nota.xml
        TextView vistaActividad;
        TextView vistaDescripcion;
        TextView vistaDesc_;
        TextView vistaHora;

        public HolderNota(@NonNull View itemView) {
            super(itemView);
            // Inicialización de las Vistas de la UI
            vistaActividad = binding.vistaActividad;
            vistaDescripcion = binding.vistaDescripcion;
            vistaDesc_ = binding.vistaDesc;
            vistaHora = binding.vistaHora;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recycleViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

        }
    }
}
