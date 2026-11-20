package com.ti.tecnoimport.comprador;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseUser;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.CartAdapter;
import com.ti.tecnoimport.models.ItemCarrito;
import com.ti.tecnoimport.models.Orden;
import com.ti.tecnoimport.repositories.AuthRepository;
import com.ti.tecnoimport.repositories.CarritoOrdenRepository;
import com.ti.tecnoimport.repositories.EpaycoRepository;
import com.ti.tecnoimport.utils.LocationHelper;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla de carrito de compras y checkout del rol Comprador.
 * Gestiona el listado de productos agregados en Cloud Firestore, el cálculo del total,
 * la captura de coordenadas GPS mediante LocationHelper y el pago mediante ePayco.
 */
public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCarrito;
    private TextView tvTotalCarrito;
    private EditText etDireccionEntrega;
    private Button btnObtenerUbicacion, btnPagarEpayco;

    private CarritoOrdenRepository carritoRepository;
    private AuthRepository authRepository;
    private LocationHelper locationHelper;
    private CartAdapter adapter;

    private final List<ItemCarrito> itemsCarrito = new ArrayList<>();
    private double totalCompra = 0.0;
    private double latitudEntrega = 0.0;
    private double longitudEntrega = 0.0;

    private final ActivityResultLauncher<String> permisoUbicacionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (concedido) {
                    capturarUbicacionGps();
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        carritoRepository = new CarritoOrdenRepository();
        authRepository = new AuthRepository();
        locationHelper = new LocationHelper(this);

        // Vincular los IDs exactos de activity_cart.xml
        rvCarrito = findViewById(R.id.recyclerCarrito);
        tvTotalCarrito = findViewById(R.id.txtTotal);
        btnPagarEpayco = findViewById(R.id.btnComprar);
        etDireccionEntrega = findViewById(R.id.etDireccionEntrega);
        btnObtenerUbicacion = findViewById(R.id.btnObtenerUbicacionGps);

        rvCarrito.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, itemsCarrito, this::eliminarItem);
        rvCarrito.setAdapter(adapter);

        btnObtenerUbicacion.setOnClickListener(v -> verificarPermisoYSolicitarGps());
        btnPagarEpayco.setOnClickListener(v -> iniciarPagoEpayco());

        cargarItemsCarrito();
    }

    /**
     * Consulta los ítems del carrito del usuario autenticado en Firestore y calcula el total.
     */
    private void cargarItemsCarrito() {
        FirebaseUser user = authRepository.getUsuarioActual();
        if (user == null) return;

        carritoRepository.obtenerCarrito(user.getUid(), new CarritoOrdenRepository.CarritoCallback() {
            @Override
            public void onResultado(List<ItemCarrito> items) {
                itemsCarrito.clear();
                itemsCarrito.addAll(items);
                adapter.notifyDataSetChanged();
                recalcularTotal();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(CartActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Suma los subtotales de cada línea del carrito y actualiza el texto en moneda COP.
     */
    private void recalcularTotal() {
        totalCompra = 0.0;
        for (ItemCarrito item : itemsCarrito) {
            totalCompra += item.calcularSubtotal();
        }
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        tvTotalCarrito.setText("Total: " + formato.format(totalCompra));
    }

    /**
     * Elimina un documento específico de la colección 'carrito' en Cloud Firestore.
     *
     * @param item Ítem del carrito seleccionado para eliminar.
     */
    private void eliminarItem(ItemCarrito item) {
        FirebaseUser user = authRepository.getUsuarioActual();
        if (user == null) return;

        carritoRepository.eliminarItemCarrito(item.getId(), new CarritoOrdenRepository.OperacionCallback() {
            @Override
            public void onExito() {
                cargarItemsCarrito();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(CartActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Verifica el permiso ACCESS_FINE_LOCATION en tiempo de ejecución antes de leer el GPS.
     */
    private void verificarPermisoYSolicitarGps() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            capturarUbicacionGps();
        } else {
            permisoUbicacionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Obtiene las coordenadas actuales del dispositivo físico y autocompleta la dirección.
     */
    private void capturarUbicacionGps() {
        Toast.makeText(this, "Obteniendo coordenadas GPS...", Toast.LENGTH_SHORT).show();
        locationHelper.obtenerUbicacionActual(new LocationHelper.LocationResultCallback() {
            @Override
            public void onUbicacionObtenida(double latitud, double longitud, String direccionLegible) {
                latitudEntrega = latitud;
                longitudEntrega = longitud;
                etDireccionEntrega.setText(direccionLegible);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(CartActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Abre el Checkout de ePayco en modo pruebas dentro de un diálogo WebView
     * y captura la URL de respuesta para registrar la compra en Firestore.
     */
    private void iniciarPagoEpayco() {
        FirebaseUser user = authRepository.getUsuarioActual();
        String direccion = etDireccionEntrega.getText().toString().trim();

        if (user == null || itemsCarrito.isEmpty()) {
            Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(direccion)) {
            Toast.makeText(this, "Ingresa o captura tu dirección de entrega con GPS", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialogPago = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        WebView webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith(EpaycoRepository.URL_RESPUESTA)) {
                    String refPago = EpaycoRepository.extraerReferenciaPago(url);
                    dialogPago.dismiss();
                    registrarOrdenConfirmada(user, direccion, refPago);
                    return true;
                }
                return false;
            }
        });

        String html = EpaycoRepository.generarHtmlCheckoutEpayco(
                "Pedido TecnoImport (" + itemsCarrito.size() + " productos)",
                totalCompra,
                user.getEmail() != null ? user.getEmail() : "cliente@tecnoimport.co"
        );
        webView.loadDataWithBaseURL("https://checkout.epayco.co", html, "text/html", "UTF-8", null);
        dialogPago.setContentView(webView);
        dialogPago.show();
    }

    /**
     * Guarda la transacción aprobada en la colección 'compras' y vacía el carrito.
     */
    private void registrarOrdenConfirmada(FirebaseUser user, String direccion, String refPago) {
        Orden orden = new Orden(
                null,
                user.getUid(),
                user.getEmail(),
                new ArrayList<>(itemsCarrito),
                totalCompra,
                direccion,
                latitudEntrega,
                longitudEntrega,
                "PAGADA",
                refPago,
                System.currentTimeMillis()
        );

        carritoRepository.confirmarOrdenYVaciarCarrito(orden, new CarritoOrdenRepository.OperacionCallback() {
            @Override
            public void onExito() {
                Toast.makeText(CartActivity.this,
                        "¡Pago confirmado! Referencia: " + refPago, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(CartActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}