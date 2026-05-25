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
    private EditText etStringDescripcion;
    private EditText etPrecioProducto;
    private EditText etUrlImagenProducto;
    private Button btnGuardarProducto;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etStringDescripcion = findViewById(R.id.etDescripcionProducto);
        etPrecioProducto = findViewById(R.id.etPrecioProducto);

        etUrlImagenProducto = findViewById(R.id.etUrlImagenProducto);

        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);

        btnGuardarProducto.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {
        String nombre = etNombreProducto.getText().toString().trim();
        String descripcion = etStringDescripcion.getText().toString().trim();
        String precioTexto = etPrecioProducto.getText().toString().trim();
        String urlImagen = etUrlImagenProducto.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombreProducto.setError("Ingrese nombre");
            return;
        }

        if (TextUtils.isEmpty(descripcion)) {
            etStringDescripcion.setError("Ingrese descripción");
            return;
        }

        if (TextUtils.isEmpty(precioTexto)) {
            etPrecioProducto.setError("Ingrese precio");
            return;
        }

        if (TextUtils.isEmpty(urlImagen)) {
            etUrlImagenProducto.setError("Ingrese la URL de la imagen");
            return;
        }

        double precio = Double.parseDouble(precioTexto);

        Product product = new Product(
                nombre,
                descripcion,
                precio,
                urlImagen
        );

        db.collection("productos")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddProductActivity.this, "Producto creado con éxito", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa al Dashboard
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}