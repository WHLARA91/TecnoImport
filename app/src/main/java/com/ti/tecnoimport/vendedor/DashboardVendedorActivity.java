package com.ti.tecnoimport.vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.auth.LoginActivity;
import com.ti.tecnoimport.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DashboardVendedorActivity extends AppCompatActivity {

    private Button btnAgregarProducto;
    private RecyclerView recyclerVendedor;
    private List<Product> productList;
    private FirebaseFirestore db;

    // 💡 Nota: Quitamos temporalmente el CompradorProductAdapter para evitar el crash cruzado.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_vendedor);

        // Validación de seguridad de sesión
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // BOTON
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);

        // RECYCLER
        recyclerVendedor = findViewById(R.id.recyclerVendedor);
        recyclerVendedor.setLayoutManager(new LinearLayoutManager(this));

        // LISTA
        productList = new ArrayList<>();

        // FIRESTORE
        db = FirebaseFirestore.getInstance();

        // Ejecutamos la carga segura
        cargarProductos();

        // CLICK BOTON
        if (btnAgregarProducto != null) {
            btnAgregarProducto.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardVendedorActivity.this, AddProductActivity.class);
                startActivity(intent);
            });
        }
    }

    private void cargarProductos() {
        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    try {
                        productList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                Product product = document.toObject(Product.class);
                                productList.add(product);
                            } catch (Exception e) {
                                // Evita que un producto corrupto tumbe el dashboard
                            }
                        }

                        // 💡 NOTA: Cuando crees tu "VendedorProductAdapter", lo enlazas aquí de forma segura.
                        // Por ahora, la lista se llena en memoria sin tumbar la app.

                    } catch (Exception e) {
                        Toast.makeText(DashboardVendedorActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DashboardVendedorActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                });
    }
}
