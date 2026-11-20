package com.ti.tecnoimport.comprador;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseUser;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.adapters.PurchaseAdapter;
import com.ti.tecnoimport.models.Orden;
import com.ti.tecnoimport.repositories.AuthRepository;
import com.ti.tecnoimport.repositories.CarritoOrdenRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla que muestra el historial de compras confirmadas del usuario autenticado.
 * Consulta la colección 'compras' en Cloud Firestore filtrando por el UID del comprador.
 */
public class PurchaseHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistorial;
    private PurchaseAdapter adapter;
    private final List<Orden> listaOrdenes = new ArrayList<>();
    private CarritoOrdenRepository ordenRepository;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        ordenRepository = new CarritoOrdenRepository();
        authRepository = new AuthRepository();

        // Vincular el RecyclerView definido en activity_purchase_history.xml
        rvHistorial = findViewById(R.id.recyclerCompras);
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PurchaseAdapter(this, listaOrdenes);
        rvHistorial.setAdapter(adapter);

        cargarHistorial();
    }

    /**
     * Consulta en Cloud Firestore las órdenes registradas a nombre del comprador actual.
     */
    private void cargarHistorial() {
        FirebaseUser user = authRepository.getUsuarioActual();
        if (user == null) return;

        ordenRepository.listarOrdenes(user.getUid(), new CarritoOrdenRepository.OrdenesCallback() {
            @Override
            public void onResultado(List<Orden> ordenes) {
                listaOrdenes.clear();
                listaOrdenes.addAll(ordenes);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(PurchaseHistoryActivity.this,
                        "Error cargando historial: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }
}