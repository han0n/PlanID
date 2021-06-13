package com.han0n.planid;
/* INTERFAZ NECESARIA para que me devuelva la posición de cada elemento del RecycleView */
public interface RecycleViewClickInterface {
    void onItemClick(int position);// Eliminará el elemento en Listado
    void onLongItemClick(int position);


}
