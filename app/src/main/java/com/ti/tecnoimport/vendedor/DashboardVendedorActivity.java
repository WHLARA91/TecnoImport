package com.ti.tecnoimport.vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.ProductAdapter;
import com.ti.tecnoimport.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DashboardVendedorActivity extends AppCompatActivity {

    private Button btnAgregarProducto;
    private RecyclerView recyclerVendedor;

    private List<Product> productList;
    private ProductAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard_vendedor);

        // BOTON
        btnAgregarProducto =
                findViewById(R.id.btnAgregarProducto);

        // RECYCLER
        recyclerVendedor =
                findViewById(R.id.recyclerVendedor);

        recyclerVendedor.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // LISTA
        productList = new ArrayList<>();

        adapter = new ProductAdapter(productList);

        recyclerVendedor.setAdapter(adapter);

        // FIRESTORE
        db = FirebaseFirestore.getInstance();

        cargarProductos();

        // CLICK BOTON
        btnAgregarProducto.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardVendedorActivity.this,
                    AddProductActivity.class
            );

            startActivity(intent);

        });
    }

    private void cargarProductos() {

        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    productList.clear();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        Product product =
                                document.toObject(Product.class);

                        productList.add(product);
                    }

                    adapter.notifyDataSetChanged();

                });
    }
}
