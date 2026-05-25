package com.ti.tecnoimport.models;

public class CartItem {

    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String usuarioId;

    public CartItem() {
    }

    public CartItem(
            String nombre,
            String descripcion,
            double precio,
            String usuarioId
    ) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.usuarioId = usuarioId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}