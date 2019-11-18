package com.danito.p_agendaavanzada;

import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imagen;
    TextView nombre, apellido, telefono, correo;
    OnImageClickListener imageClickListener;
    Dato dato;

    public Holder(View v) {
        super(v);
        imagen = v.findViewById(R.id.imageview);
        imagen.setOnClickListener(this);
        nombre = v.findViewById(R.id.nombre);
        apellido = v.findViewById(R.id.apellido);
        telefono = v.findViewById(R.id.telefono);
        correo = v.findViewById(R.id.correo);
    }

    public void bind(Dato d) {
        nombre.setText(d.getNombre());
        apellido.setText(d.getApellido());
        telefono.setText(d.getTelefono());
        correo.setText(d.getCorreo());
        imagen.setImageBitmap(d.getImagen());
        dato = d;
    }

    public void setImageClickListener(OnImageClickListener listener) {
        if (listener != null) {
            imageClickListener = listener;
        }
    }

    @Override
    public void onClick(View v) {
        if (imageClickListener != null) {
            imageClickListener.onImageClick(dato);
        }
    }
}
