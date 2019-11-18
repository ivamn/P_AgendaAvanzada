package com.danito.p_agendaavanzada;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Dato implements Parcelable {
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private Uri imagen;

    public Dato() {
        imagen = Uri.parse("");
    }

    public Dato(String nombre, String apellido, String telefono, String correo, Uri imagen) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Uri getImagen() {
        return imagen;
    }

    public void setImagen(Uri imagen) {
        this.imagen = imagen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.apellido);
        dest.writeString(this.telefono);
        dest.writeString(this.correo);
        dest.writeParcelable(this.imagen, flags);
    }

    protected Dato(Parcel in) {
        this.nombre = in.readString();
        this.apellido = in.readString();
        this.telefono = in.readString();
        this.correo = in.readString();
        this.imagen = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<Dato> CREATOR = new Parcelable.Creator<Dato>() {
        @Override
        public Dato createFromParcel(Parcel source) {
            return new Dato(source);
        }

        @Override
        public Dato[] newArray(int size) {
            return new Dato[size];
        }
    };
}
