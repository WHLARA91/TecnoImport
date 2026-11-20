package com.ti.tecnoimport.models;

import java.io.Serializable;
import java.util.Locale;

/**
 * Modelo de datos que representa a un usuario del sistema TecnoImport.
 * Soporta los tres roles definidos en la arquitectura: ADMIN, VENDEDOR y COMPRADOR.
 */
public class Usuario implements Serializable {

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_VENDEDOR = "VENDEDOR";
    public static final String ROL_COMPRADOR = "COMPRADOR";

    private String uid;
    private String nombre;
    private String correo;
    private String telefono;
    private String rol;
    private String direccion;
    private double latitud;
    private double longitud;
    private boolean activo;

    /**
     * Constructor vacío requerido por Cloud Firestore.
     * Inicializa 'activo' en true por defecto para mantener compatibilidad con
     * documentos existentes que aún no tienen este campo en la base de datos.
     */
    public Usuario() {
        this.activo = true;
    }

    /**
     * Constructor completo para inicializar un nuevo usuario.
     *
     * @param uid       Identificador único generado por Firebase Authentication.
     * @param nombre    Nombre completo del usuario.
     * @param correo    Correo electrónico institucional o personal.
     * @param telefono  Número de contacto.
     * @param rol       Rol asignado (ADMIN, VENDEDOR o COMPRADOR).
     * @param direccion Dirección física de entrega o despacho.
     * @param latitud   Coordenada de latitud GPS.
     * @param longitud  Coordenada de longitud GPS.
     * @param activo    Estado de la cuenta en la plataforma.
     */
    public Usuario(String uid, String nombre, String correo, String telefono,
                   String rol, String direccion, double latitud, double longitud, boolean activo) {
        this.uid = uid;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activo = activo;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * Normaliza el rol a mayúsculas para garantizar compatibilidad tanto con
     * registros previos en minúsculas ("admin") como nuevos ("ADMIN").
     *
     * @return Rol normalizado en mayúsculas (ADMIN, VENDEDOR o COMPRADOR).
     */
    public String getRol() {
        if (rol == null || rol.trim().isEmpty()) {
            return ROL_COMPRADOR;
        }
        return rol.trim().toUpperCase(Locale.ROOT);
    }

    public void setRol(String rol) { this.rol = rol; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
