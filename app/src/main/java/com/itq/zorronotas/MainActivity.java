package com.itq.zorronotas;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity Principal que muestra la lista de notas en el RecyclerView y observa el LiveData.
 */
public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_AGREGAR = 1;
    public static final int REQUEST_CODE_EDITAR = 2;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;

    private NotasViewModel notasViewModel;
    private NotaAdapter notaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        long startTime = System.currentTimeMillis();
        long minSplashDurationMs = 1500; // Mantiene el Splash Screen al menos 1.5 segundos
        long maxSplashTimeoutMs = 3000;  // Tiempo límite de seguridad

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pedirPermisoNotificaciones();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotas);
        notaAdapter = new NotaAdapter();
        recyclerView.setAdapter(notaAdapter);

        // Inicializar ViewModel
        notasViewModel = new ViewModelProvider(this).get(NotasViewModel.class);
        notasViewModel.getTodasLasNotas().observe(this, notas -> {
            notaAdapter.setNotas(notas);
        });

        // Mantiene visible el Splash Screen durante la duración deseada o mientras cargan las notas
        splashScreen.setKeepOnScreenCondition(() -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= maxSplashTimeoutMs) {
                return false;
            }
            boolean isMinTimePassed = elapsedTime >= minSplashDurationMs;
            boolean isDataLoaded = notasViewModel.getTodasLasNotas().getValue() != null;
            return !(isMinTimePassed && isDataLoaded);
        });

        // Botón Flotante para agregar nota
        FloatingActionButton fab = findViewById(R.id.fabAgregarNota);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarEditarNotaActivity.class);
            startActivityForResult(intent, REQUEST_CODE_AGREGAR);
        });

        // Clic corto: Editar Nota
        notaAdapter.setOnNotaClickListener(nota -> {
            // Si la fecha del recordatorio ya se cumplió, la reseteamos a null al abrir
            if (nota.getFechaRecordatorio() != null && nota.getFechaRecordatorio() <= System.currentTimeMillis()) {
                nota.setFechaRecordatorio(null);
                notasViewModel.actualizar(nota);
            }

            Intent intent = new Intent(MainActivity.this, AgregarEditarNotaActivity.class);
            intent.putExtra(AgregarEditarNotaActivity.EXTRA_ID, nota.getId());
            intent.putExtra(AgregarEditarNotaActivity.EXTRA_TITULO, nota.getTitulo());
            intent.putExtra(AgregarEditarNotaActivity.EXTRA_CONTENIDO, nota.getContenido());
            intent.putExtra(AgregarEditarNotaActivity.EXTRA_FECHA, nota.getFechaCreacion());
            if (nota.getFechaRecordatorio() != null && nota.getFechaRecordatorio() > System.currentTimeMillis()) {
                intent.putExtra(AgregarEditarNotaActivity.EXTRA_RECORDATORIO, nota.getFechaRecordatorio());
            } else {
                intent.putExtra(AgregarEditarNotaActivity.EXTRA_RECORDATORIO, -1L);
            }
            startActivityForResult(intent, REQUEST_CODE_EDITAR);
        });

        // Clic largo: Eliminar Nota
        notaAdapter.setOnNotaLongClickListener(nota -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Eliminar Nota")
                    .setMessage("¿Deseas eliminar la nota '" + nota.getTitulo() + "'?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        notasViewModel.eliminar(nota);
                        Toast.makeText(MainActivity.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        verificarIntentNotificacion(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        verificarIntentNotificacion(intent);
    }

    private void pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }

    private void verificarIntentNotificacion(Intent intent) {
        if (intent != null && intent.getBooleanExtra("ABRIR_NOTA_RECORDATORIO", false)) {
            int id = intent.getIntExtra(AgregarEditarNotaActivity.EXTRA_ID, -1);
            String titulo = intent.getStringExtra(AgregarEditarNotaActivity.EXTRA_TITULO);
            String contenido = intent.getStringExtra(AgregarEditarNotaActivity.EXTRA_CONTENIDO);

            if (id != -1) {
                Intent editIntent = new Intent(MainActivity.this, AgregarEditarNotaActivity.class);
                editIntent.putExtra(AgregarEditarNotaActivity.EXTRA_ID, id);
                editIntent.putExtra(AgregarEditarNotaActivity.EXTRA_TITULO, titulo);
                editIntent.putExtra(AgregarEditarNotaActivity.EXTRA_CONTENIDO, contenido);
                editIntent.putExtra(AgregarEditarNotaActivity.EXTRA_RECORDATORIO, -1L); // Reset a null
                editIntent.putExtra("DESDE_NOTIFICACION", true);
                startActivityForResult(editIntent, REQUEST_CODE_EDITAR);
            }
            intent.removeExtra("ABRIR_NOTA_RECORDATORIO");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String titulo = data.getStringExtra(AgregarEditarNotaActivity.EXTRA_TITULO);
            String contenido = data.getStringExtra(AgregarEditarNotaActivity.EXTRA_CONTENIDO);
            String fecha = data.getStringExtra(AgregarEditarNotaActivity.EXTRA_FECHA);
            long recordatorio = data.getLongExtra(AgregarEditarNotaActivity.EXTRA_RECORDATORIO, -1L);
            Long fechaRecordatorio = (recordatorio > 0) ? recordatorio : null;

            if (requestCode == REQUEST_CODE_AGREGAR) {
                NotaEntity nuevaNota = new NotaEntity(titulo, contenido, fecha, fechaRecordatorio);
                notasViewModel.insertar(nuevaNota);
                Toast.makeText(this, "Nota guardada con éxito", Toast.LENGTH_SHORT).show();

            } else if (requestCode == REQUEST_CODE_EDITAR) {
                int id = data.getIntExtra(AgregarEditarNotaActivity.EXTRA_ID, -1);
                if (id != -1) {
                    NotaEntity notaEditada = new NotaEntity(titulo, contenido, fecha, fechaRecordatorio);
                    notaEditada.setId(id);
                    notasViewModel.actualizar(notaEditada);
                    Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
