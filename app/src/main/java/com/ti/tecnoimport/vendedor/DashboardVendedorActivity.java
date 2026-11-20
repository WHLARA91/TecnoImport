package com.ti.tecnoimport.vendedor;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseUser;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.VendedorProductAdapter;
import com.ti.tecnoimport.auth.LoginActivity;
import com.ti.tecnoimport.comprador.PurchaseHistoryActivity;
import com.ti.tecnoimport.models.Orden;
import com.ti.tecnoimport.models.Producto;
import com.ti.tecnoimport.repositories.AuthRepository;
import com.ti.tecnoimport.repositories.CarritoOrdenRepository;
import com.ti.tecnoimport.repositories.ProductoRepository;
import com.ti.tecnoimport.utils.SessionManager;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Panel principal del rol Vendedor.
 * Gestiona el CRUD de productos del vendedor en Cloud Firestore, muestra el resumen
 * de ventas y comisiones en tiempo real y permite navegar al historial de pedidos.
 */
public class DashboardVendedorActivity extends AppCompatActivity {

    private RecyclerView rvProductosVendedor;
    private Button btnAgregarProducto, btnPedidos, btnHistorial;
    private TextView txtVentasHoy, txtComision;
    private View btnCerrarSesion;

    private ProductoRepository productoRepository;
    private CarritoOrdenRepository ordenRepository;
    private AuthRepository authRepository;
    private VendedorProductAdapter adapter;
    private final List<Producto> listaProductos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_vendedor);

        productoRepository = new ProductoRepository();
        ordenRepository = new CarritoOrdenRepository();
        authRepository = new AuthRepository();

        // Vincular los IDs exactos de activity_dashboard_vendedor.xml
        rvProductosVendedor = findViewById(R.id.recyclerVendedor);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        btnPedidos = findViewById(R.id.btnPedidos);
        btnHistorial = findViewById(R.id.btnHistorial);
        txtVentasHoy = findViewById(R.id.txtVentasHoy);
        txtComision = findViewById(R.id.txtComision);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionVendedor);

        rvProductosVendedor.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VendedorProductAdapter(this, listaProductos, new VendedorProductAdapter.OnVendedorProductoListener() {
            @Override
            public void onEditarProducto(Producto producto) {
                Intent intent = new Intent(DashboardVendedorActivity.this, AddProductActivity.class);
                intent.putExtra("PRODUCTO_EDITAR", producto);
                startActivity(intent);
            }

            @Override
            public void onEliminarProducto(Producto producto) {
                confirmarEliminacion(producto);
            }
        });
        rvProductosVendedor.setAdapter(adapter);

        btnAgregarProducto.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class)));

        // Navegar al listado de órdenes/pedidos registrados en Firestore
        View.OnClickListener abrirOrdenes = v ->
                startActivity(new Intent(this, PurchaseHistoryActivity.class));
        btnPedidos.setOnClickListener(abrirOrdenes);
        btnHistorial.setOnClickListener(abrirOrdenes);

        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductosDelVendedor();
        actualizarResumenVentas();
    }

    /**
     * Consulta en Cloud Firestore los productos asociados al UID del vendedor autenticado.
     */
    private void cargarProductosDelVendedor() {
        FirebaseUser user = authRepository.getUsuarioActual();
        if (user == null) return;

        productoRepository.listarProductosPorVendedor(user.getUid(), new ProductoRepository.ListaProductosCallback() {
            @Override
            public void onResultado(List<Producto> productos) {
                listaProductos.clear();
                listaProductos.addAll(productos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(DashboardVendedorActivity.this,
                        "Error al cargar productos: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Consulta las compras registradas en Firestore para calcular el conteo de ventas
     * y la ganancia neta estimada del vendedor (90 % tras comisión de plataforma).
     */
    private void actualizarResumenVentas() {
        ordenRepository.listarOrdenes(null, new CarritoOrdenRepository.OrdenesCallback() {
            @Override
            public void onResultado(List<Orden> ordenes) {
                double totalVendido = 0.0;
                for (Orden o : ordenes) {
                    totalVendido += o.getTotal();
                }
                double gananciaVendedor = totalVendido * 0.90;
                NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

                txtVentasHoy.setText("Ventas: " + ordenes.size());
                txtComision.setText("Ingresos netos: " + formato.format(gananciaVendedor));
            }

            @Override
            public void onError(String mensaje) {
                // Mantiene los valores por defecto si aún no existen órdenes
            }
        });
    }

    /**
     * Despliega un cuadro de diálogo de confirmación antes de eliminar un producto.
     *
     * @param producto Producto seleccionado para eliminación en Firestore.
     */
    private void confirmarEliminacion(Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de eliminar '" + producto.getNombre() + "' del catálogo?")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        productoRepository.eliminarProducto(producto.getId(), new ProductoRepository.OperacionCallback() {
                            @Override
                            public void onExito() {
                                Toast.makeText(DashboardVendedorActivity.this,
                                        "Producto eliminado", Toast.LENGTH_SHORT).show();
                                cargarProductosDelVendedor();
                            }

                            @Override
                            public void onError(String mensaje) {
                                Toast.makeText(DashboardVendedorActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Cierra la sesión actual en Firebase Auth y redirige a LoginActivity.
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