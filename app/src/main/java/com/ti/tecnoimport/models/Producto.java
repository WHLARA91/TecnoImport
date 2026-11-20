package com.ti.tecnoimport.models;

import java.io.Serializable;

/**
 * Modelo de datos que representa un artículo tecnológico publicado en el catálogo.
 */
public class Producto implements Serializable {

    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String categoria;
    private String imagenUrl;
    private String vendedorId;
    private long fechaCreacion;

    /**
     * Constructor vacío requerido por Cloud Firestore.
     */
    public Producto() {
    }

    /**
     * Constructor completo para registrar o actualizar un producto tecnológico.
     *
     * @param id            Identificador del documento en Firestore.
     * @param nombre        Nombre comercial del dispositivo o accesorio.
     * @param descripcion   Especificaciones técnicas del producto.
     * @param precio        Precio unitario en pesos colombianos (COP).
     * @param stock         Unidades disponibles en inventario.
     * @param categoria     Categoría tecnológica (Celulares, Laptops, Audio, Gamer, etc.).
     * @param imagenUrl     URL pública descargable desde Firebase Storage.
     * @param vendedorId    UID del vendedor propietario del producto.
     * @param fechaCreacion Marca de tiempo (timestamp) de registro.
     */
    public Producto(String id, String nombre, String descripcion, double precio,
                    int stock, String categoria, String imagenUrl, String vendedorId, long fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.imagenUrl = imagenUrl;
        this.vendedorId = vendedorId;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getVendedorId() { return vendedorId; }
    public void setVendedorId(String vendedorId) { this.vendedorId = vendedorId; }

    public long getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(long fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}