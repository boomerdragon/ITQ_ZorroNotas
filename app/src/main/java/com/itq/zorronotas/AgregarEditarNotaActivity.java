package com.itq.zorronotas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itq.zorronotas.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity para captura y edición de datos de una nota.
 */
public class AgregarEditarNotaActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.itq.zorronotas.EXTRA_ID";
    public static final String EXTRA_TITULO = "com.itq.zorronotas.EXTRA_TITULO";
    public static final String EXTRA_CONTENIDO = "com.itq.zorronotas.EXTRA_CONTENIDO";
    public static final String EXTRA_FECHA = "com.itq.zorronotas.EXTRA_FECHA";

    private EditText etTitulo;
    private EditText etContenido;
    private int idNota = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_nota);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitulo = findViewById(R.id.etTituloNota);
        etContenido = findViewById(R.id.etContenidoNota);
        Button btnGuardar = findViewById(R.id.btnGuardarNota);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ID)) {
            toolbar.setTitle("Editar Nota");
            idNota = intent.getIntExtra(EXTRA_ID, -1);
            etTitulo.setText(intent.getStringExtra(EXTRA_TITULO));
            etContenido.setText(intent.getStringExtra(EXTRA_CONTENIDO));
        } else {
            toolbar.setTitle("Nueva Nota");
        }

        btnGuardar.setOnClickListener(v -> guardarNota());
    }

    private void guardarNota() {
        String titulo = etTitulo.getText().toString().trim();
        String contenido = etContenido.getText().toString().trim();

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(contenido)) {
            Toast.makeText(this, "Por favor escribe un título y contenido", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaActual = sdf.format(new Date());

        Intent data = new Intent();
        data.putExtra(EXTRA_TITULO, titulo);
        data.putExtra(EXTRA_CONTENIDO, contenido);
        data.putExtra(EXTRA_FECHA, fechaActual);

        if (idNota != -1) {
            data.putExtra(EXTRA_ID, idNota);
        }

        setResult(RESULT_OK, data);
        finish();
    }
}