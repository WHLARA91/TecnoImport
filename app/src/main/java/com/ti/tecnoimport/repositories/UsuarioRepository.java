package com.ti.tecnoimport.repositories;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ti.tecnoimport.models.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio CRUD para la administración integral de usuarios desde el rol Administrador.
 */
public class UsuarioRepository {

    public interface ListaUsuariosCallback {
        void onResultado(List<Usuario> usuarios);
        void onError(String mensaje);
    }

    public interface OperacionCallback {
        void onExito();
        void onError(String mensaje);
    }

    private final FirebaseFirestore db;

    public UsuarioRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Obtiene el listado completo de usuarios registrados en Firestore.
     */
    public void listarUsuarios(ListaUsuariosCallback callback) {
        db.collection("usuarios").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Usuario> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Usuario u = doc.toObject(Usuario.class);
                        if (u != null) {
                            u.setUid(doc.getId());
                            lista.add(u);
                        }
                    }
                    callback.onResultado(lista);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Crea o actualiza los datos, rol o estado activo de un usuario en Firestore.
     */
    public void guardarOActualizarUsuario(Usuario usuario, OperacionCallback callback) {
        String docId = (usuario.getUid() == null || usuario.getUid().isEmpty())
                ? db.collection("usuarios").document().getId()
                : usuario.getUid();
        usuario.setUid(docId);

        db.collection("usuarios").document(docId)
                .set(usuario)
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Elimina el registro de un usuario de la colección 'usuarios'.
     */
    public void eliminarUsuario(String uid, OperacionCallback callback) {
        db.collection("usuarios").document(uid)
                .delete()
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
