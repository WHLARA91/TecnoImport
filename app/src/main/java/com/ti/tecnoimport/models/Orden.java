package com.ti.tecnoimport.models;

import java.io.Serializable;
import java.util.List;

/**
 * Representa una orden de compra confirmada y persistida en Cloud Firestore.
 */
public class Orden implements Serializable {

    private String ordenId;
    private String compradorId;
    private String compradorNombre;
    private List<ItemCarrito> items;
    private double total;
    private String direccionEntrega;
    private double latitud;
    private double longitud;
    private String estado;
    private String referenciaPago;
    private long fecha;

    /**
     * Constructor vacío requerido por Cloud Firestore.
     */
    public Orden() {
    }

    /**
     * Constructor completo para registrar una transacción aprobada.
     */
    public Orden(String ordenId, String compradorId, String compradorNombre, List<ItemCarrito> items,
                 double total, String direccionEntrega, double latitud, double longitud,
                 String estado, String referenciaPago, long fecha) {
        this.ordenId = ordenId;
        this.compradorId = compradorId;
        this.compradorNombre = compradorNombre;
        this.items = items;
        this.total = total;
        this.direccionEntrega = direccionEntrega;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.referenciaPago = referenciaPago;
        this.fecha = fecha;
    }

    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }

    public String getCompradorId() { return compradorId; }
    public void setCompradorId(String compradorId) { this.compradorId = compradorId; }

    public String getCompradorNombre() { return compradorNombre; }
    public void setCompradorNombre(String compradorNombre) { this.compradorNombre = compradorNombre; }

    public List<ItemCarrito> getItems() { return items; }
    public void setItems(List<ItemCarrito> items) { this.items = items; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }

    public long getFecha() { return fecha; }
    public void setFecha(long fecha) { this.fecha = fecha; }
}