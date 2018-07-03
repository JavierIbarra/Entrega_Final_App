package com.example.jaiba.asistencia.Entities;

public class Ubicaciones {

    private float latitud;
    private float longitud;
    private String nombre;
    private String hora;

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public String getHora() { return hora; }

    public void setHora(String hora) { this.hora = hora; }
}
