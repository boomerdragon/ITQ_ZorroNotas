package com.itq.zorronotas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * ViewModel que conserva el estado de las notas independientemente del ciclo de vida de la Activity.
 */
public class NotasViewModel extends AndroidViewModel {

    private final NotasRepository repository;
    private final LiveData<List<NotaEntity>> todasLasNotas;

    public NotasViewModel(@NonNull Application application) {
        super(application);
        repository = new NotasRepository(application);
        todasLasNotas = repository.getTodasLasNotas();
    }

    public LiveData<List<NotaEntity>> getTodasLasNotas() {
        return todasLasNotas;
    }

    public void insertar(NotaEntity nota) {
        repository.insertar(nota);
    }

    public void actualizar(NotaEntity nota) {
        repository.actualizar(nota);
    }

    public void eliminar(NotaEntity nota) {
        repository.eliminar(nota);
    }
}