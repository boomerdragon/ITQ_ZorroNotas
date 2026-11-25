package com.itq.zorronotas;

import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidad de persitencia que representa la tabla 'tabla_notas' en SQLite mediante Room ORM.
 */
@Keep
@Entity(tableName = "tabla_notas")
public class NotaEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titulo;
    private String contenido;
    private String fechaCreacion;

    // Constructor completo
    public NotaEntity(String titulo, String contenido, String fechaCreacion) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}