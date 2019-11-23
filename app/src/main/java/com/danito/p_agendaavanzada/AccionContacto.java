package com.danito.p_agendaavanzada;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AccionContacto extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    TextInputEditText editNombre, editApellido, editTelefono, editCorreo;
    TextInputLayout editNombreContenedor;
    Button aceptarButton;
    Dato datoRecibido;
    private final int EDITAR = 0;
    private final int ADD = 1;
    private int proposito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accion_contacto);
        Intent intentRecibido = getIntent();
        datoRecibido = intentRecibido.getParcelableExtra("contacto");
        resetTint();
        proposito = datoRecibido == null ? ADD : EDITAR;

        editNombre = findViewById(R.id.editNombre);
        editApellido = findViewById(R.id.editApellido);
        editTelefono = findViewById(R.id.editTelefono);
        editCorreo = findViewById(R.id.editCorreo);
        editNombreContenedor = findViewById(R.id.editNombreContenedor);
        aceptarButton = findViewById(R.id.aceptar);
        aceptarButton.setOnClickListener(this);
        editNombre.setOnFocusChangeListener(this);
        editApellido.setOnFocusChangeListener(this);
        editTelefono.setOnFocusChangeListener(this);
        editCorreo.setOnFocusChangeListener(this);

        if (proposito == EDITAR) {
            editNombre.setText(datoRecibido.getNombre());
            editApellido.setText(datoRecibido.getApellido());
            editTelefono.setText(datoRecibido.getTelefono());
            editCorreo.setText(datoRecibido.getCorreo());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Drawable d = null;
        ImageView icono = null;
        switch (v.getId()) {
            case R.id.editNombre:
                icono = findViewById(R.id.imagenNombre);
                d = icono.getDrawable();
                break;
            case R.id.editApellido:
                icono = findViewById(R.id.imagenApellido);
                d = icono.getDrawable();
                break;
            case R.id.editTelefono:
                icono = findViewById(R.id.imagenTelefono);
                d = icono.getDrawable();
                break;
            case R.id.editCorreo:
                icono = findViewById(R.id.imagenCorreo);
                d = icono.getDrawable();
                break;
        }
        if (d != null) {
            DrawableCompat.wrap(d);
            if (hasFocus) {
                DrawableCompat.setTint(d, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            } else {
                DrawableCompat.setTint(d, ContextCompat.getColor(getApplicationContext(), R.color.colorBaseIconos));
            }
        }
    }

    public Dato generarNuevoContacto() {
        Dato d = new Dato();
        d.setNombre(editNombre.getText().toString());
        d.setApellido(editApellido.getText().toString());
        d.setTelefono(editTelefono.getText().toString());
        d.setCorreo(editCorreo.getText().toString());
        return d;
    }

    public void editarContacto() {
        datoRecibido.setNombre(editNombre.getText().toString());
        datoRecibido.setApellido(editApellido.getText().toString());
        datoRecibido.setTelefono(editTelefono.getText().toString());
        datoRecibido.setCorreo(editCorreo.getText().toString());
    }

    @Override
    public void onClick(View v) {
        if (nombreCorrecto(editNombre.getText().toString())) {
            Intent intent = new Intent();
            if (proposito == EDITAR) {
                editarContacto();
                intent.putExtra("contacto", datoRecibido);
            } else {
                Dato nuevoDato = generarNuevoContacto();
                intent.putExtra("contacto", nuevoDato);
            }
            setResult(RESULT_OK, intent);
            resetTint();
            finish();
        }
    }

    private boolean nombreCorrecto(String nombre) {
        if (nombre.isEmpty()) {
            editNombreContenedor.setError("El nombre no debe estar vacío.");
            return false;
        }
        return true;
    }

    private void resetTint() {
        ImageView iconoNombre = findViewById(R.id.imagenNombre);
        ImageView iconoApellido = findViewById(R.id.imagenApellido);
        ImageView iconoTelefono = findViewById(R.id.imagenTelefono);
        ImageView iconoCorreo = findViewById(R.id.imagenCorreo);
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.add(iconoNombre.getDrawable());
        drawables.add(iconoApellido.getDrawable());
        drawables.add(iconoTelefono.getDrawable());
        drawables.add(iconoCorreo.getDrawable());
        for (Drawable d : drawables) {
            DrawableCompat.wrap(d);
            DrawableCompat.setTint(d, ContextCompat.getColor(getApplicationContext(), R.color.colorBaseIconos));
        }
    }


}
