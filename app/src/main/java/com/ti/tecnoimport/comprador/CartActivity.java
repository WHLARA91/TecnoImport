package com.ti.tecnoimport.comprador;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.CartAdapter;
import com.ti.tecnoimport.models.CartItem;
import com.ti.tecnoimport.models.Purchase;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrito;

    private TextView txtTotal;

    private MaterialButton btnComprar;

    private List<CartItem> listaCarrito;

    private CartAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cart);

        recyclerCarrito =
                findViewById(R.id.recyclerCarrito);

        txtTotal =
                findViewById(R.id.txtTotal);

        btnComprar =
                findViewById(R.id.btnComprar);

        recyclerCarrito.setLayoutManager(
                new LinearLayoutManager(this)
        );

        listaCarrito = new ArrayList<>();

        adapter = new CartAdapter(
                listaCarrito,
                this
        );

        recyclerCarrito.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarCarrito();

        btnComprar.setOnClickListener(v -> comprar());
    }

    private void cargarCarrito() {

        String usuarioId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        db.collection("carrito")
                .document(usuarioId)
                .collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    listaCarrito.clear();

                    double total = 0;

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        CartItem item =
                                document.toObject(
                                        CartItem.class
                                );

                        item.setId(document.getId());

                        listaCarrito.add(item);

                        total += item.getPrecio();
                    }

                    adapter.notifyDataSetChanged();

                    txtTotal.setText(
                            "Total: $" + total
                    );

                });
    }

    private void comprar() {

        String usuarioId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        for (CartItem item : listaCarrito) {

            Purchase purchase =
                    new Purchase(
                            item.getNombre(),
                            item.getDescripcion(),
                            item.getPrecio(),
                            usuarioId,
                            System.currentTimeMillis()
                    );

            db.collection("compras")
                    .add(purchase);
        }

        db.collection("carrito")
                .document(usuarioId)
                .collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        document.getReference().delete();
                    }

                    listaCarrito.clear();

                    adapter.notifyDataSetChanged();

                    txtTotal.setText("Total: $0");

                    Toast.makeText(
                            CartActivity.this,
                            "Compra realizada",
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    public void actualizarTotal() {

        double total = 0;

        for (CartItem item : listaCarrito) {

            total += item.getPrecio();
        }

        txtTotal.setText("Total: $" + total);
    }
}