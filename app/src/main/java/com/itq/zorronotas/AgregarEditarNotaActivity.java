package com.itq.zorronotas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity para captura y edición de datos de una nota y selección opcional de recordatorio.
 */
public class AgregarEditarNotaActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.itq.zorronotas.EXTRA_ID";
    public static final String EXTRA_TITULO = "com.itq.zorronotas.EXTRA_TITULO";
    public static final String EXTRA_CONTENIDO = "com.itq.zorronotas.EXTRA_CONTENIDO";
    public static final String EXTRA_FECHA = "com.itq.zorronotas.EXTRA_FECHA";
    public static final String EXTRA_RECORDATORIO = "com.itq.zorronotas.EXTRA_RECORDATORIO";

    private EditText etTitulo;
    private EditText etContenido;
    private CheckBox cbRecordatorio;
    private TextView tvFechaHoraRecordatorio;

    private int idNota = -1;
    private Long selectedTimestamp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_nota);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitulo = findViewById(R.id.etTituloNota);
        etContenido = findViewById(R.id.etContenidoNota);
        cbRecordatorio = findViewById(R.id.cbRecordatorio);
        tvFechaHoraRecordatorio = findViewById(R.id.tvFechaHoraRecordatorio);
        Button btnGuardar = findViewById(R.id.btnGuardarNota);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ID)) {
            toolbar.setTitle("Editar Nota");
            idNota = intent.getIntExtra(EXTRA_ID, -1);
            etTitulo.setText(intent.getStringExtra(EXTRA_TITULO));
            etContenido.setText(intent.getStringExtra(EXTRA_CONTENIDO));

            long recordatorio = intent.getLongExtra(EXTRA_RECORDATORIO, -1L);
            boolean desdeNotificacion = intent.getBooleanExtra("DESDE_NOTIFICACION", false);

            if (recordatorio > 0) {
                if (recordatorio <= System.currentTimeMillis() || desdeNotificacion) {
                    // Si ya expiró la fecha del recordatorio o se abrió desde notificación, se resetea a null
                    selectedTimestamp = null;
                    cbRecordatorio.setChecked(false);
                    tvFechaHoraRecordatorio.setVisibility(View.GONE);
                } else {
                    selectedTimestamp = recordatorio;
                    cbRecordatorio.setChecked(true);
                    actualizarTextoFechaHora(selectedTimestamp);
                    tvFechaHoraRecordatorio.setVisibility(View.VISIBLE);
                }
            }
        } else {
            toolbar.setTitle("Nueva Nota");
        }

        cbRecordatorio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedTimestamp == null) {
                    mostrarPickerDialogs();
                } else {
                    tvFechaHoraRecordatorio.setVisibility(View.VISIBLE);
                }
            } else {
                selectedTimestamp = null;
                tvFechaHoraRecordatorio.setVisibility(View.GONE);
            }
        });

        tvFechaHoraRecordatorio.setOnClickListener(v -> mostrarPickerDialogs());
        btnGuardar.setOnClickListener(v -> guardarNota());
    }

    private void mostrarPickerDialogs() {
        final Calendar c = Calendar.getInstance();
        if (selectedTimestamp != null && selectedTimestamp > System.currentTimeMillis()) {
            c.setTimeInMillis(selectedTimestamp);
        }

        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            int startHour = c.get(Calendar.HOUR_OF_DAY);
            int startMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                Calendar selCal = Calendar.getInstance();
                selCal.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                selCal.set(Calendar.MILLISECOND, 0);

                long timeInMillis = selCal.getTimeInMillis();
                if (timeInMillis <= System.currentTimeMillis()) {
                    Toast.makeText(this, "La fecha y hora del recordatorio deben ser futuras", Toast.LENGTH_SHORT).show();
                    if (selectedTimestamp == null) {
                        cbRecordatorio.setChecked(false);
                        tvFechaHoraRecordatorio.setVisibility(View.GONE);
                    }
                } else {
                    selectedTimestamp = timeInMillis;
                    actualizarTextoFechaHora(selectedTimestamp);
                    tvFechaHoraRecordatorio.setVisibility(View.VISIBLE);
                }
            }, startHour, startMinute, true);

            timePickerDialog.setOnCancelListener(dialog -> {
                if (selectedTimestamp == null) {
                    cbRecordatorio.setChecked(false);
                    tvFechaHoraRecordatorio.setVisibility(View.GONE);
                }
            });
            timePickerDialog.show();

        }, startYear, startMonth, startDay);

        datePickerDialog.setOnCancelListener(dialog -> {
            if (selectedTimestamp == null) {
                cbRecordatorio.setChecked(false);
                tvFechaHoraRecordatorio.setVisibility(View.GONE);
            }
        });
        datePickerDialog.show();
    }

    private void actualizarTextoFechaHora(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaTexto = "⏰ Recordatorio: " + sdf.format(new Date(timestamp));
        tvFechaHoraRecordatorio.setText(fechaTexto);
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

        if (cbRecordatorio.isChecked() && selectedTimestamp != null && selectedTimestamp > System.currentTimeMillis()) {
            data.putExtra(EXTRA_RECORDATORIO, selectedTimestamp);
        } else {
            data.putExtra(EXTRA_RECORDATORIO, -1L);
        }

        if (idNota != -1) {
            data.putExtra(EXTRA_ID, idNota);
        }

        setResult(RESULT_OK, data);
        finish();
    }
}
