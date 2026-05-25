package com.ti.tecnoimport.comprador;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.PurchaseAdapter;
import com.ti.tecnoimport.models.Purchase;

import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryActivity
        extends AppCompatActivity {

    private RecyclerView recyclerCompras;

    private List<Purchase> listaCompras;

    private PurchaseAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_purchase_history
        );

        recyclerCompras =
                findViewById(R.id.recyclerCompras);

        recyclerCompras.setLayoutManager(
                new LinearLayoutManager(this)
        );

        listaCompras = new ArrayList<>();

        adapter =
                new PurchaseAdapter(listaCompras);

        recyclerCompras.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarCompras();
    }

    private void cargarCompras() {

        String usuarioId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        db.collection("compras")
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    listaCompras.clear();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        Purchase purchase =
                                document.toObject(
                                        Purchase.class
                                );

                        listaCompras.add(purchase);
                    }

                    adapter.notifyDataSetChanged();

                });
    }
}
