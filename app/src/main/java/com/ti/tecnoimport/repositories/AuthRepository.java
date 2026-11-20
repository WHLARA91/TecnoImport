package com.ti.tecnoimport.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ti.tecnoimport.models.Usuario;

/**
 * Repositorio encargado de la autenticación multirrol con Firebase Authentication
 * y la sincronización del perfil de usuario en Cloud Firestore.
 */
public class AuthRepository {

    public interface AuthCallback {
        void onExito(Usuario usuario);
        void onError(String mensaje);
    }

    public interface SimpleCallback {
        void onExito();
        void onError(String mensaje);
    }

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Registra un nuevo usuario en Firebase Auth y crea su documento de perfil en Firestore.
     */
    public void registrarUsuario(String nombre, String correo, String password,
                                 String telefono, String rol, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fbUser = authResult.getUser();
                    if (fbUser == null) {
                        callback.onError("Error inesperado al obtener el UID.");
                        return;
                    }
                    Usuario nuevoUsuario = new Usuario(
                            fbUser.getUid(), nombre, correo, telefono,
                            rol, "", 0.0, 0.0, true
                    );
                    db.collection("usuarios").document(fbUser.getUid())
                            .set(nuevoUsuario)
                            .addOnSuccessListener(unused -> callback.onExito(nuevoUsuario))
                            .addOnFailureListener(e -> callback.onError("Error guardando perfil: " + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError("Error de registro: " + e.getMessage()));
    }

    /**
     * Inicia sesión con correo y contraseña y recupera el rol asignado desde Firestore.
     */
    public void iniciarSesion(String correo, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(correo, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fbUser = authResult.getUser();
                    if (fbUser != null) {
                        obtenerPerfilUsuario(fbUser.getUid(), callback);
                    }
                })
                .addOnFailureListener(e -> callback.onError("Credenciales inválidas: " + e.getMessage()));
    }

    /**
     * Consulta los datos y rol del usuario autenticado en la colección 'usuarios'.
     */
    public void obtenerPerfilUsuario(String uid, AuthCallback callback) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(snapshot -> {
                    Usuario usuario = snapshot.toObject(Usuario.class);
                    if (usuario != null && usuario.isActivo()) {
                        callback.onExito(usuario);
                    } else if (usuario != null && !usuario.isActivo()) {
                        firebaseAuth.signOut();
                        callback.onError("Tu cuenta se encuentra inactiva. Contacta al administrador.");
                    } else {
                        callback.onError("Perfil de usuario no encontrado en la base de datos.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error consultando rol: " + e.getMessage()));
    }

    /**
     * Envía un correo de restablecimiento de contraseña mediante Firebase Auth.
     */
    public void enviarRecuperacionPassword(String correo, SimpleCallback callback) {
        firebaseAuth.sendPasswordResetEmail(correo)
                .addOnSuccessListener(unused -> callback.onExito())
                .addOnFailureListener(e -> callback.onError("No se pudo enviar el correo: " + e.getMessage()));
    }

    /**
     * Cierra la sesión activa en Firebase Auth.
     */
    public void cerrarSesion() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getUsuarioActual() {
        return firebaseAuth.getCurrentUser();
    }
}
