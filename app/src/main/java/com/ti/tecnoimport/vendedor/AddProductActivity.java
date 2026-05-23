package com.ti.tecnoimport.vendedor;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.Product;

public class AddProductActivity extends AppCompatActivity {

    private EditText etNombreProducto;
    private EditText etDescripcionProducto;
    private EditText etPrecioProducto;
    private Button btnGuardarProducto;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcionProducto = findViewById(R.id.etDescripcionProducto);
        etPrecioProducto = findViewById(R.id.etPrecioProducto);
        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);

        btnGuardarProducto.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {

        String nombre = etNombreProducto.getText().toString().trim();
        String descripcion = etDescripcionProducto.getText().toString().trim();
        String precioTexto = etPrecioProducto.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombreProducto.setError("Ingrese nombre");
            return;
        }

        if (TextUtils.isEmpty(descripcion)) {
            etDescripcionProducto.setError("Ingrese descripción");
            return;
        }

        if (TextUtils.isEmpty(precioTexto)) {
            etPrecioProducto.setError("Ingrese precio");
            return;
        }

        double precio = Double.parseDouble(precioTexto);

        Product product = new Product(
                nombre,
                descripcion,
                precio
        );

        db.collection("productos")
                .add(product)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            AddProductActivity.this,
                            "Producto guardado",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            AddProductActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}