package com.ti.tecnoimport.repositories;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.ti.tecnoimport.models.ItemCarrito;
import com.ti.tecnoimport.models.Orden;
import com.ti.tecnoimport.models.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio CRUD conectado a las colecciones 'carrito' y 'compras' de Cloud Firestore.
 */
public class CarritoOrdenRepository {

    public interface CarritoCallback {
        void onResultado(List<ItemCarrito> items);
        void onError(String mensaje);
    }

    public interface OrdenesCallback {
        void onResultado(List<Orden> ordenes);
        void onError(String mensaje);
    }

    public interface OperacionCallback {
        void onExito();
        void onError(String mensaje);
    }

    private final FirebaseFirestore db;

    public CarritoOrdenRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Agrega un producto a la colección 'carrito' vinculándolo al 'usuarioId'
     * y asignando el ID real del documento para evitar campos nulos.
     */
    public void agregarAlCarrito(String uidComprador, Producto producto, int cantidad, OperacionCallback callback) {
        DocumentReference docRef = db.collection("carrito").document();
        ItemCarrito nuevoItem = new ItemCarrito(
                docRef.getId(),
                uidComprador,
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                cantidad,
                producto.getImagenUrl(),
                producto.getVendedorId()
        );

        docRef.set(nuevoItem)
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError("Error agregando al carrito: " + e.getMessage()));
    }

    /**
     * Consulta en la colección 'carrito' todos los ítems pertenecientes al usuario actual.
     */
    public void obtenerCarrito(String uidComprador, CarritoCallback callback) {
        db.collection("carrito")
                .whereEqualTo("usuarioId", uidComprador)
                .get()
                .addOnSuccessListener(query -> {
                    List<ItemCarrito> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        ItemCarrito item = doc.toObject(ItemCarrito.class);
                        if (item != null) {
                            // Asegura tener siempre el ID del documento aunque estuviera en null
                            item.setId(doc.getId());
                            lista.add(item);
                        }
                    }
                    callback.onResultado(lista);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Elimina un documento específico de la colección 'carrito' usando su ID de documento.
     */
    public void eliminarItemCarrito(String documentId, OperacionCallback callback) {
        db.collection("carrito").document(documentId)
                .delete()
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Registra la transacción aprobada en la colección 'compras' y elimina los ítems
     * comprados de la colección 'carrito' mediante un lote atómico (WriteBatch).
     */
    public void confirmarOrdenYVaciarCarrito(Orden orden, OperacionCallback callback) {
        WriteBatch batch = db.batch();

        DocumentReference compraRef = db.collection("compras").document();
        orden.setOrdenId(compraRef.getId());
        batch.set(compraRef, orden);

        for (ItemCarrito item : orden.getItems()) {
            if (item.getId() != null && !item.getId().isEmpty()) {
                DocumentReference itemRef = db.collection("carrito").document(item.getId());
                batch.delete(itemRef);
            }
        }

        batch.commit()
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError("Error registrando compra: " + e.getMessage()));
    }

    /**
     * Lista el historial de transacciones desde la colección 'compras'.
     */
    public void listarOrdenes(String compradorIdOpcional, OrdenesCallback callback) {
        if (compradorIdOpcional != null && !compradorIdOpcional.isEmpty()) {
            db.collection("compras")
                    .whereEqualTo("compradorId", compradorIdOpcional)
                    .get()
                    .addOnSuccessListener(query -> mapearCompras(query.getDocuments(), callback))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            db.collection("compras")
                    .get()
                    .addOnSuccessListener(query -> mapearCompras(query.getDocuments(), callback))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }
    }

    private void mapearCompras(List<DocumentSnapshot> docs, OrdenesCallback callback) {
        List<Orden> lista = new ArrayList<>();
        for (DocumentSnapshot doc : docs) {
            Orden o = doc.toObject(Orden.class);
            if (o != null) {
                o.setOrdenId(doc.getId());
                lista.add(o);
            }
        }
        callback.onResultado(lista);
    }
}
