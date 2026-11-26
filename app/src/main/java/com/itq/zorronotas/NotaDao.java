package com.itq.zorronotas;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) que define las consultas SQL relacionales para Room ORM.
 */
@Dao
public interface NotaDao {

    // Inserción de una nueva nota
    @Insert
    long insertar(NotaEntity nota);

    // Actualización de una nota existente
    @Update
    void actualizar(NotaEntity nota);

    // Eliminación de una nota
    @Delete
    void eliminar(NotaEntity nota);

    // Consulta reactiva de todas las notas ordenadas por ID descendente
    @Query("SELECT * FROM tabla_notas ORDER BY id DESC")
    LiveData<List<NotaEntity>> getTodasLasNotas();
}