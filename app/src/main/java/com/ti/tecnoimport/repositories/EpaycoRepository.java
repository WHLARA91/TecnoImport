package com.ti.tecnoimport.repositories;

import android.net.Uri;
import java.util.Locale;

/**
 * Gestiona la construcción de la URL de pago en ambiente de pruebas (Sandbox) de ePayco
 * y la validación de la respuesta de transacción para su registro en Firestore.
 */
public class EpaycoRepository {

    // Reemplaza con tu PUBLIC_KEY de pruebas desde el panel de ePayco
    public static final String EPAYCO_PUBLIC_KEY = "TU_PUBLIC_KEY_DE_PRUEBAS_EPAYCO";
    public static final String URL_RESPUESTA = "https://tecnoimport.app/epayco-response";

    /**
     * Genera el HTML del formulario Checkout estándar de ePayco en modo prueba (test="true")
     * para ser cargado directamente en un WebView dentro de la pantalla de pago.
     *
     * @param nombreCompra Descripción breve de los artículos del carrito.
     * @param montoTotal   Valor total a cobrar en COP.
     * @param correoCliente Correo del comprador.
     * @return Cadena HTML auto-ejecutable con el botón/widget oficial de ePayco.
     */
    public static String generarHtmlCheckoutEpayco(String nombreCompra, double montoTotal, String correoCliente) {
        String valorFormateado = String.format(Locale.US, "%.0f", montoTotal);

        return "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>"
                + "<body style='display:flex;justify-content:center;align-items:center;height:100vh;margin:0;background:#F5F7FA;'>"
                + "<form>"
                + "<script src='https://checkout.epayco.co/checkout.js' "
                + "class='epayco-button' "
                + "data-epayco-key='" + EPAYCO_PUBLIC_KEY + "' "
                + "data-epayco-amount='" + valorFormateado + "' "
                + "data-epayco-name='Compra TecnoImport' "
                + "data-epayco-description='" + nombreCompra + "' "
                + "data-epayco-currency='cop' "
                + "data-epayco-country='co' "
                + "data-epayco-test='true' "
                + "data-epayco-external='false' "
                + "data-epayco-email-billing='" + correoCliente + "' "
                + "data-epayco-response='" + URL_RESPUESTA + "'>"
                + "</script>"
                + "</form>"
                + "</body></html>";
    }

    /**
     * Extrae la referencia de pago (ref_payco) cuando ePayco redirige a URL_RESPUESTA.
     *
     * @param urlRedireccion URL interceptada por el WebViewClient.
     * @return Referencia de transacción o código generado si el parámetro viene en la URL.
     */
    public static String extraerReferenciaPago(String urlRedireccion) {
        Uri uri = Uri.parse(urlRedireccion);
        String ref = uri.getQueryParameter("ref_payco");
        return (ref != null && !ref.isEmpty()) ? ref : "EPAYCO-" + System.currentTimeMillis();
    }
}