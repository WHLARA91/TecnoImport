package com.ti.tecnoimport.comprador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseUser;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.CompradorProductAdapter;
import com.ti.tecnoimport.auth.LoginActivity;
import com.ti.tecnoimport.models.Producto;
import com.ti.tecnoimport.repositories.AuthRepository;
import com.ti.tecnoimport.repositories.CarritoOrdenRepository;
import com.ti.tecnoimport.repositories.ProductoRepository;
import com.ti.tecnoimport.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla principal del rol Comprador.
 * Muestra el catálogo de productos tecnológicos en tiempo real desde Cloud Firestore
 * y permite agregar artículos al carrito, consultar el historial de compras y cerrar sesión.
 */
public class DashboardCompradorActivity extends AppCompatActivity {

    private RecyclerView rvProductos;
    private Button btnVerCarrito, btnHistorial;
    private View btnCerrarSesion;

    private ProductoRepository productoRepository;
    private CarritoOrdenRepository carritoRepository;
    private AuthRepository authRepository;
    private CompradorProductAdapter adapter;
    private final List<Producto> listaProductos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_comprador);

        productoRepository = new ProductoRepository();
        carritoRepository = new CarritoOrdenRepository();
        authRepository = new AuthRepository();

        // Vincular los IDs exactos definidos en activity_dashboard_comprador.xml
        rvProductos = findViewById(R.id.recyclerProductos);
        btnVerCarrito = findViewById(R.id.btnVerCarrito);
        btnHistorial = findViewById(R.id.btnMisCompras);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionComprador);

        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CompradorProductAdapter(this, listaProductos, this::agregarProductoAlCarrito);
        rvProductos.setAdapter(adapter);

        btnVerCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        btnHistorial.setOnClickListener(v ->
                startActivity(new Intent(this, PurchaseHistoryActivity.class)));

        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        }

        cargarCatalogo();
    }

    /**
     * Consulta los productos disponibles en Cloud Firestore y actualiza el catálogo.
     */
    private void cargarCatalogo() {
        productoRepository.listarTodosLosProductos(new ProductoRepository.ListaProductosCallback() {
            @Override
            public void onResultado(List<Producto> productos) {
                listaProductos.clear();
                listaProductos.addAll(productos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(DashboardCompradorActivity.this,
                        "Error cargando catálogo: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Agrega una unidad del producto seleccionado al carrito del comprador en Firestore.
     *
     * @param producto Producto seleccionado desde el catálogo.
     */
    private void agregarProductoAlCarrito(Producto producto) {
        FirebaseUser user = authRepository.getUsuarioActual();
        if (user == null) return;

        carritoRepository.agregarAlCarrito(user.getUid(), producto, 1, new CarritoOrdenRepository.OperacionCallback() {
            @Override
            public void onExito() {
                Toast.makeText(DashboardCompradorActivity.this,
                        producto.getNombre() + " agregado al carrito", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(DashboardCompradorActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cierra la sesión en Firebase Auth y redirige a LoginActivity limpiando el historial.
     */
    private void cerrarSesion() {
        authRepository.cerrarSesion();
        new SessionManager(this).cerrarSesionActiva();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}