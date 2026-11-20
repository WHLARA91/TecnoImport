package com.ti.tecnoimport.repositories;

import android.net.Uri;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ti.tecnoimport.models.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio CRUD de productos con integración a Firebase Storage para fotografías
 * capturadas desde cámara o seleccionadas desde galería.
 */
public class ProductoRepository {

    public interface ListaProductosCallback {
        void onResultado(List<Producto> productos);
        void onError(String mensaje);
    }

    public interface OperacionCallback {
        void onExito();
        void onError(String mensaje);
    }

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public ProductoRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Lista todos los productos disponibles (rol Comprador / Administrador).
     */
    public void listarTodosLosProductos(ListaProductosCallback callback) {
        db.collection("productos").get()
                .addOnSuccessListener(query -> {
                    List<Producto> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Producto p = doc.toObject(Producto.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            lista.add(p);
                        }
                    }
                    callback.onResultado(lista);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Lista únicamente los productos creados por un vendedor específico.
     */
    public void listarProductosPorVendedor(String vendedorId, ListaProductosCallback callback) {
        db.collection("productos")
                .whereEqualTo("vendedorId", vendedorId)
                .get()
                .addOnSuccessListener(query -> {
                    List<Producto> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Producto p = doc.toObject(Producto.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            lista.add(p);
                        }
                    }
                    callback.onResultado(lista);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Sube la imagen del producto a Firebase Storage (si se seleccionó una nueva)
     * y guarda o actualiza el documento en Cloud Firestore.
     */
    public void guardarProductoConImagen(Producto producto, Uri imagenLocalUri, OperacionCallback callback) {
        String idProducto = (producto.getId() == null || producto.getId().isEmpty())
                ? db.collection("productos").document().getId()
                : producto.getId();
        producto.setId(idProducto);

        if (imagenLocalUri != null) {
            StorageReference ref = storage.getReference()
                    .child("productos/" + idProducto + "_" + UUID.randomUUID() + ".jpg");

            ref.putFile(imagenLocalUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful() && task.getException() != null) {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    })
                    .addOnSuccessListener(downloadUri -> {
                        producto.setImagenUrl(downloadUri.toString());
                        persistirDocumentoProducto(producto, callback);
                    })
                    .addOnFailureListener(e -> callback.onError("Error subiendo imagen: " + e.getMessage()));
        } else {
            persistirDocumentoProducto(producto, callback);
        }
    }

    private void persistirDocumentoProducto(Producto producto, OperacionCallback callback) {
        db.collection("productos").document(producto.getId())
                .set(producto)
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError("Error guardando producto: " + e.getMessage()));
    }

    /**
     * Elimina un producto del catálogo en Firestore.
     */
    public void eliminarProducto(String productoId, OperacionCallback callback) {
        db.collection("productos").document(productoId)
                .delete()
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
