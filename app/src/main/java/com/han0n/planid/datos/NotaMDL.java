package com.han0n.planid.datos;

public class NotaMDL {

    long timestamp, id;
    String  uid, actividad, descripcion;
    int hora, minuto;

    /* el vacio lo requiere la BD de Firebase */
    public NotaMDL(){}

    /* parametrizado */
    public NotaMDL(long timestamp, long id, String uid, String actividad, String descripcion,
                   int hora, int minuto) {

        this.timestamp = timestamp;
        this.id = id;
        this.uid = uid;
        this.actividad = actividad;
        this.descripcion = descripcion;
        this.hora = hora;
        this.minuto = minuto;

    }

    /* getters y setters */
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}
