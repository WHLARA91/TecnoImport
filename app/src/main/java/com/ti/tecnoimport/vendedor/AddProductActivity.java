package com.ti.tecnoimport.vendedor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.Producto;
import com.ti.tecnoimport.repositories.AuthRepository;
import com.ti.tecnoimport.repositories.ProductoRepository;
import java.io.File;
import java.io.IOException;

/**
 * Actividad para registrar y editar productos tecnológicos del rol Vendedor.
 * Integra permisos en tiempo de ejecución para cámara, selección desde galería,
 * subida de imágenes a Firebase Storage y persistencia en Cloud Firestore.
 */
public class AddProductActivity extends AppCompatActivity {

    private TextView tvTituloForm;
    private EditText etNombre, etDescripcion, etPrecio, etStock, etUrlImagen;
    private Spinner spinnerCategoria;
    private ImageView ivPreviewProducto;
    private Button btnTomarFoto, btnSeleccionarGaleria, btnGuardarProducto;

    private ProductoRepository productoRepository;
    private AuthRepository authRepository;

    private Producto productoEnEdicion;
    private Uri imagenSeleccionadaUri;
    private Uri fotoCamaraTempUri;

    private final ActivityResultLauncher<String> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imagenSeleccionadaUri = uri;
                    ivPreviewProducto.setImageURI(uri);
                }
            });

    private final ActivityResultLauncher<Uri> camaraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), exito -> {
                if (exito && fotoCamaraTempUri != null) {
                    imagenSeleccionadaUri = fotoCamaraTempUri;
                    ivPreviewProducto.setImageURI(fotoCamaraTempUri);
                }
            });

    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (concedido) {
                    abrirCamara();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productoRepository = new ProductoRepository();
        authRepository = new AuthRepository();

        // Vincular componentes de activity_add_product.xml
        tvTituloForm = findViewById(R.id.tvTituloFormProducto);
        etNombre = findViewById(R.id.etNombreProducto);
        etDescripcion = findViewById(R.id.etDescripcionProducto);
        etPrecio = findViewById(R.id.etPrecioProducto);
        etStock = findViewById(R.id.etStockProducto);
        etUrlImagen = findViewById(R.id.etUrlImagenProducto);
        spinnerCategoria = findViewById(R.id.spinnerCategoriaProducto);
        ivPreviewProducto = findViewById(R.id.ivPreviewProducto);
        btnTomarFoto = findViewById(R.id.btnTomarFotoProducto);
        btnSeleccionarGaleria = findViewById(R.id.btnGaleriaProducto);
        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);

        String[] categorias = {"Celulares", "Laptops", "Audífonos", "Relojes Inteligentes", "Accesorios Gamer"};
        spinnerCategoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias));

        if (getIntent().hasExtra("PRODUCTO_EDITAR")) {
            productoEnEdicion = (Producto) getIntent().getSerializableExtra("PRODUCTO_EDITAR");
            cargarDatosEdicion(categorias);
        }

        btnTomarFoto.setOnClickListener(v -> verificarPermisoYAbrirCamara());
        btnSeleccionarGaleria.setOnClickListener(v -> galeriaLauncher.launch("image/*"));
        btnGuardarProducto.setOnClickListener(v -> guardarProducto());
    }

    /**
     * Carga los valores actuales del producto cuando la pantalla se abre en modo edición.
     */
    private void cargarDatosEdicion(String[] categorias) {
        if (productoEnEdicion == null) return;
        tvTituloForm.setText("Editar Producto");
        btnGuardarProducto.setText("Actualizar Producto");
        etNombre.setText(productoEnEdicion.getNombre());
        etDescripcion.setText(productoEnEdicion.getDescripcion());
        etPrecio.setText(String.valueOf(productoEnEdicion.getPrecio()));
        etStock.setText(String.valueOf(productoEnEdicion.getStock()));
        etUrlImagen.setText(productoEnEdicion.getImagenUrl());

        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(productoEnEdicion.getCategoria())) {
                spinnerCategoria.setSelection(i);
            }
        }

        if (productoEnEdicion.getImagenUrl() != null && !productoEnEdicion.getImagenUrl().isEmpty()) {
            Glide.with(this).load(productoEnEdicion.getImagenUrl()).into(ivPreviewProducto);
        }
    }

    /**
     * Solicita el permiso CAMERA en tiempo de ejecución si aún no ha sido otorgado.
     */
    private void verificarPermisoYAbrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Crea un archivo temporal en caché externa y abre la cámara nativa mediante FileProvider.
     */
    private void abrirCamara() {
        try {
            File archivoFoto = File.createTempFile("producto_", ".jpg", getExternalCacheDir());
            fotoCamaraTempUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    archivoFoto
            );
            camaraLauncher.launch(fotoCamaraTempUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error preparando cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Valida los datos ingresados, sube la fotografía a Firebase Storage si se capturó/seleccionó
     * una imagen local y guarda el producto en Cloud Firestore.
     */
    private void guardarProducto() {
        FirebaseUser vendedor = authRepository.getUsuarioActual();
        if (vendedor == null) return;

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String urlManual = etUrlImagen.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(precioStr)) {
            Toast.makeText(this, "Completa al menos el nombre y el precio", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        int stock = 10;
        try {
            precio = Double.parseDouble(precioStr);
            if (!TextUtils.isEmpty(stockStr)) {
                stock = Integer.parseInt(stockStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        Producto producto = (productoEnEdicion != null) ? productoEnEdicion : new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        producto.setVendedorId(vendedor.getUid());

        if (imagenSeleccionadaUri == null && !TextUtils.isEmpty(urlManual)) {
            producto.setImagenUrl(urlManual);
        }
        if (productoEnEdicion == null) {
            producto.setFechaCreacion(System.currentTimeMillis());
        }

        btnGuardarProducto.setEnabled(false);
        Toast.makeText(this, "Guardando producto...", Toast.LENGTH_SHORT).show();

        productoRepository.guardarProductoConImagen(producto, imagenSeleccionadaUri, new ProductoRepository.OperacionCallback() {
            @Override
            public void onExito() {
                Toast.makeText(AddProductActivity.this, "Producto guardado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String mensaje) {
                btnGuardarProducto.setEnabled(true);
                Toast.makeText(AddProductActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}