package com.ti.tecnoimport.models;

public class Purchase {

    private String nombre;
    private String descripcion;
    private double precio;
    private String usuarioId;
    private long fecha;

    public Purchase() {
    }

    public Purchase(
            String nombre,
            String descripcion,
            double precio,
            String usuarioId,
            long fecha
    ) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
