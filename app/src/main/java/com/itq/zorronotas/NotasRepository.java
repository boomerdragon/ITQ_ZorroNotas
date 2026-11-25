package com.itq.zorronotas;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio desacoplado que gestiona la ejecución de operaciones CRUD en segundo plano.
 */
public class NotasRepository {

    private final NotaDao notaDao;
    private final LiveData<List<NotaEntity>> todasLasNotas;
    private final ExecutorService executorService;

    public NotasRepository(Application application) {
        AppDatabase db = AppDatabase.getInstancia(application);
        notaDao = db.notaDao();
        todasLasNotas = notaDao.getTodasLasNotas();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<NotaEntity>> getTodasLasNotas() {
        return todasLasNotas;
    }

    public void insertar(NotaEntity nota) {
        executorService.execute(() -> notaDao.insertar(nota));
    }

    public void actualizar(NotaEntity nota) {
        executorService.execute(() -> notaDao.actualizar(nota));
    }

    public void eliminar(NotaEntity nota) {
        executorService.execute(() -> notaDao.eliminar(nota));
    }
}