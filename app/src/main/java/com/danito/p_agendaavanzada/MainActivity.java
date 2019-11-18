package com.danito.p_agendaavanzada;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public ArrayList<Dato> datos;
    private int indiceListaPulsado;
    private RecyclerView recyclerView;
    private Adaptador adaptador;
    private SwipeDetector swipeDetector;
    private Dato datoTemp;

    private final int COD_ACTIVITY_EDITAR = 1;
    private final int COD_ACTIVITY_ADD = 2;
    private final int COD_ELEGIR_IMAGEN = 3;
    private final int COD_TOMAR_FOTO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datos = new ArrayList<>();
        cargarDatos();
        swipeDetector = new SwipeDetector();
        adaptador = new Adaptador(this);
        adaptador.setOnClickListener(this);
        adaptador.setOnLongClickListener(this);
        adaptador.setOnTouchListener(swipeDetector);
        adaptador.setImageClickListener(new OnImageClickListener() {
            @Override
            public void onImageClick(final Dato dato) {
                mostrarPerfil(dato);
            }
        });
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContacto();
            }
        });
    }

    private void mostrarPerfil(final Dato dato) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View vista = inflater.inflate(R.layout.perfil_contacto, null);
        TextView nombrePerfil = vista.findViewById(R.id.nombrePerfil);
        ImageView imagenPerfil = vista.findViewById(R.id.imagenPerfil);
        imagenPerfil.setImageURI(dato.getImagen());
        nombrePerfil.setText(dato.getNombre());
        builder.setView(vista);
        final AlertDialog dialog = builder.create();
        ImageView imagenCamara = vista.findViewById(R.id.botonCamara);
        imagenCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto(dato, dialog);
            }
        });
        ImageView imagenGaleria = vista.findViewById(R.id.botonGaleria);
        imagenGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirImagen(dato, dialog);
            }
        });
        dialog.show();
    }

    private void elegirImagen(Dato dato, AlertDialog dialog) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, COD_ELEGIR_IMAGEN);
            datoTemp = dato;
        }
        dialog.cancel();
    }

    private void tomarFoto(Dato dato, AlertDialog dialog) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, COD_TOMAR_FOTO);
            datoTemp = dato;
        }
        dialog.cancel();
    }

    private void llamarContacto(final Dato d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Llamar a " + d.getNombre() + "?");
        builder.setPositiveButton("LLAMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (d.getTelefono().isEmpty()) {
                    Toast.makeText(MainActivity.this, "El contacto no tiene teléfono", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + d.getTelefono()));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void enviarMensaje(final Dato d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Enviar mensaje a " + d.getNombre() + "?");
        builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (d.getCorreo().isEmpty()) {
                    Toast.makeText(MainActivity.this, "El contacto no tiene correo electrónico", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, d.getCorreo());
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        indiceListaPulsado = recyclerView.getChildAdapterPosition(v);
        Dato d = datos.get(recyclerView.getChildAdapterPosition(v));
        if (swipeDetector.swipeDetected()) {
            switch (swipeDetector.getAction()) {
                case LR:
                    llamarContacto(d);
                    break;
                case RL:
                    enviarMensaje(d);
                    break;
            }
        } else {
            editarDatos(d);
        }
    }

    private void addContacto() {
        Intent i = new Intent(this, AccionContacto.class);
        startActivityForResult(i, COD_ACTIVITY_ADD);
    }

    private void editarDatos(Dato d) {
        Intent i = new Intent(this, AccionContacto.class);
        i.putExtra("contacto", d);
        startActivityForResult(i, COD_ACTIVITY_EDITAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ACTIVITY_EDITAR &&
                resultCode == RESULT_OK && data != null) {
            Dato d = data.getParcelableExtra("contacto");
            datos.set(indiceListaPulsado, d);
        } else if (requestCode == COD_ACTIVITY_ADD &&
                resultCode == RESULT_OK && data != null) {
            Dato d = data.getParcelableExtra("contacto");
            datos.add(d);
        } else if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK && data != null) {
            Uri rutaImagen = data.getData();
            datoTemp.setImagen(rutaImagen);
        } else if (requestCode == COD_TOMAR_FOTO && resultCode == RESULT_OK && data != null) {

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Se ha cancelado la operación", Toast.LENGTH_LONG).show();
        }
        recyclerView.setAdapter(adaptador);
        guardarDatos();
    }

    @Override
    public boolean onLongClick(final View v) {
        Dato d = datos.get(recyclerView.getChildAdapterPosition(v));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Quieres eliminar el contacto de " + d.getNombre() + "?");
        builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                datos.remove(datos.get(recyclerView.getChildAdapterPosition(v)));
                recyclerView.setAdapter(adaptador);
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
        return false;
    }

    private void cargarDatos() {

        XmlPullParser parser = Xml.newPullParser();
        FileInputStream stream = null;
        Dato nuevoDato = null;
        try {
            stream = openFileInput("datos.xml");
            parser.setInput(stream, null);
            int evento = parser.getEventType();
            while (evento != XmlPullParser.END_DOCUMENT) {
                switch (evento) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            case "contacto":
                                nuevoDato = new Dato();
                                break;
                            case "nombre":
                                nuevoDato.setNombre(parser.nextText());
                                break;
                            case "apellido":
                                nuevoDato.setApellido(parser.nextText());
                                break;
                            case "telefono":
                                nuevoDato.setTelefono(parser.nextText());
                                break;
                            case "correo":
                                nuevoDato.setCorreo(parser.nextText());
                                break;
                            case "imagen":
                                nuevoDato.setImagen(Uri.parse(parser.nextText()));
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("contacto")) {
                            datos.add(nuevoDato);
                        }
                        break;
                }
                evento = parser.next();
            }
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        datos.add(new Dato("Iván", "Gallego", "601245789", "yo@yo.com"));
        datos.add(new Dato("Gallego", "Iván", "658984512", "yo@yo.com"));
        datos.add(new Dato("Daniel", "Acabado", "", ""));
        datos.add(new Dato("Estodorne", "Ideas", "658497415", "yo@yo.com"));
        datos.add(new Dato("Carlos", "Apellido", "684974523", ""));
        datos.add(new Dato("Juan", "Mastodonte", "", "yo@yo.com"));
        datos.add(new Dato("Marcos", "Calatraba", "784569815", ""));
        datos.add(new Dato("David", "Muñoz", "745123698", "yo@yo.com"));
        datos.add(new Dato("Sandra", "López", "696952356", "yo@yo.com"));
        datos.add(new Dato("Andrea", "García", "787878787", "yo@yo.com"));
        datos.add(new Dato("Ainhoa", "García", "", "yo@yo.com"));*/
    }

    private void guardarDatos() {
        XmlSerializer serializer = Xml.newSerializer();
        FileOutputStream stream = null;
        try {
            stream = openFileOutput("datos.xml", MODE_PRIVATE);
            serializer.setOutput(stream, null);
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "contactos");
            for (Dato d : datos) {
                serializer.startTag(null, "contacto");
                serializer.startTag(null, "nombre");
                serializer.text(d.getNombre());
                serializer.endTag(null, "nombre");
                serializer.startTag(null, "apellido");
                serializer.text(d.getApellido());
                serializer.endTag(null, "apellido");
                serializer.startTag(null, "telefono");
                serializer.text(d.getTelefono());
                serializer.endTag(null, "telefono");
                serializer.startTag(null, "correo");
                serializer.text(d.getCorreo());
                serializer.endTag(null, "correo");
                serializer.startTag(null, "imagen");
                serializer.text(d.getImagen().toString());
                serializer.endTag(null, "imagen");
                serializer.endTag(null, "contacto");
            }
            serializer.endTag(null, "contactos");
            serializer.endDocument();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
