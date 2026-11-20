package com.ti.tecnoimport.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import java.util.List;
import java.util.Locale;

/**
 * Utilidad de geolocalización que obtiene las coordenadas GPS actuales del usuario
 * y las convierte en una dirección física legible para el envío de pedidos.
 */
public class LocationHelper {

    public interface LocationResultCallback {
        void onUbicacionObtenida(double latitud, double longitud, String direccionLegible);
        void onError(String mensaje);
    }

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    public LocationHelper(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * Solicita la ubicación de alta precisión al GPS del dispositivo físico.
     * Requiere que los permisos ACCESS_FINE_LOCATION hayan sido concedidos previamente.
     *
     * @param callback Interfaz que recibe latitud, longitud y dirección geocodificada.
     */
    @SuppressLint("MissingPermission")
    public void obtenerUbicacionActual(LocationResultCallback callback) {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        String direccion = convertirCoordenadasADireccion(lat, lng);
                        callback.onUbicacionObtenida(lat, lng, direccion);
                    } else {
                        callback.onError("No se pudo obtener la ubicación. Verifica que el GPS esté encendido.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error GPS: " + e.getMessage()));
    }

    /**
     * Realiza geocodificación inversa para transformar coordenadas en dirección postal.
     */
    private String convertirCoordenadasADireccion(double latitud, double longitud) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> direcciones = geocoder.getFromLocation(latitud, longitud, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                return direcciones.get(0).getAddressLine(0);
            }
        } catch (Exception ignored) {
        }
        return String.format(Locale.US, "Lat: %.5f, Lng: %.5f", latitud, longitud);
    }
}
