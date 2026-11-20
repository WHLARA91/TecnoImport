package com.ti.tecnoimport.models;

import java.io.Serializable;

/**
 * Modelo que representa un ítem dentro de la colección 'carrito' en Cloud Firestore.
 */
public class ItemCarrito implements Serializable {

    private String id;
    private String usuarioId;
    private String productoId;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private String imagenUrl;
    private String vendedorId;

    /**
     * Constructor vacío obligatorio para la deserialización de Firestore.
     */
    public ItemCarrito() {
        this.cantidad = 1; // Valor por defecto para documentos previos sin campo cantidad
    }

    /**
     * Constructor completo para agregar un producto al carrito.
     */
    public ItemCarrito(String id, String usuarioId, String productoId, String nombre,
                       String descripcion, double precio, int cantidad,
                       String imagenUrl, String vendedorId) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.productoId = productoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
        this.vendedorId = vendedorId;
    }

    /**
     * Calcula el subtotal multiplicando el precio por la cantidad de unidades.
     */
    public double calcularSubtotal() {
        int cant = (cantidad <= 0) ? 1 : cantidad;
        return precio * cant;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getProductoId() { return productoId; }
    public void setProductoId(String productoId) { this.productoId = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidad() { return (cantidad <= 0) ? 1 : cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getVendedorId() { return vendedorId; }
    public void setVendedorId(String vendedorId) { this.vendedorId = vendedorId; }
}