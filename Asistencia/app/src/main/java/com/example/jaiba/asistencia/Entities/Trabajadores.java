package com.example.jaiba.asistencia.Entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Trabajadores {

    private String name;
    private String email;
    private int entry;
    private String rutaImagen;

    public void Trabajadores(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public int getEntry() { return entry; }

    public void setEntry(int entry) { this.entry = entry; }

    public String getRutaImagen() { return rutaImagen; }

    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
}
