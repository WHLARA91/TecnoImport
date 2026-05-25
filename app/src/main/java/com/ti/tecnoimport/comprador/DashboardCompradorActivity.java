package com.ti.tecnoimport.comprador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.CompradorProductAdapter;
import com.ti.tecnoimport.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DashboardCompradorActivity extends AppCompatActivity {

    private RecyclerView recyclerProductos;
    private MaterialButton btnVerCarrito;
    private MaterialButton btnMisCompras;
    private List<Product> listaProductos;
    private CompradorProductAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_comprador);

        // Enlace de vistas de forma segura
        recyclerProductos = findViewById(R.id.recyclerProductos);
        btnVerCarrito = findViewById(R.id.btnVerCarrito);
        btnMisCompras = findViewById(R.id.btnMisCompras);

        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        listaProductos = new ArrayList<>();
        adapter = new CompradorProductAdapter(listaProductos);
        recyclerProductos.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Cargar los datos desde Firestore de manera controlada
        cargarProductos();

        btnVerCarrito.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardCompradorActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnMisCompras.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardCompradorActivity.this, PurchaseHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void cargarProductos() {
        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    try {
                        listaProductos.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                // Mapeo seguro de Firebase al modelo Product
                                Product product = document.toObject(Product.class);
                                listaProductos.add(product);
                            } catch (Exception e) {
                                // Si un producto individual de la BD está corrupto, avisa pero no tumba la app
                                Toast.makeText(DashboardCompradorActivity.this,
                                        "Error en formato de producto: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(DashboardCompradorActivity.this,
                                "Error al actualizar la lista: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DashboardCompradorActivity.this,
                            "Error de conexión con Firestore: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}