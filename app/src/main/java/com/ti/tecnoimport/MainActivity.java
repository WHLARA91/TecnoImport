package com.ti.tecnoimport;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.ti.tecnoimport.admin.DashboardAdminActivity;
import com.ti.tecnoimport.auth.LoginActivity;
import com.ti.tecnoimport.comprador.DashboardCompradorActivity;
import com.ti.tecnoimport.models.Usuario;
import com.ti.tecnoimport.repositories.AuthRepository;

/**
 * Actividad de inicio (Splash / Launcher) de TecnoImport.
 * Verifica si existe una sesión activa en Firebase Authentication y redirige
 * al Dashboard correspondiente según el rol almacenado en Cloud Firestore.
 */
public class MainActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authRepository = new AuthRepository();
        new Handler(Looper.getMainLooper()).postDelayed(this::verificarSesion, 1200);
    }

    /**
     * Comprueba si hay un usuario autenticado y consulta su perfil en Firestore.
     */
    private void verificarSesion() {
        FirebaseUser currentUser = authRepository.getUsuarioActual();
        if (currentUser == null) {
            irALogin();
            return;
        }

        authRepository.obtenerPerfilUsuario(currentUser.getUid(), new AuthRepository.AuthCallback() {
            @Override
            public void onExito(Usuario usuario) {
                redirigirSegunRol(usuario);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                irALogin();
            }
        });
    }

    /**
     * Enruta al usuario hacia DashboardAdminActivity, DashboardVendedorActivity o DashboardCompradorActivity.
     *
     * @param usuario Objeto Usuario con el rol recuperado de Firestore.
     */
    public void redirigirSegunRol(Usuario usuario) {
        Intent intent;
        switch (usuario.getRol()) {
            case Usuario.ROL_ADMIN:
                intent = new Intent(this, DashboardAdminActivity.class);
                break;
            case Usuario.ROL_VENDEDOR:
                // Cambia DashboardVendedorActivity por el nombre exacto de tu clase dentro de .vendedor
                intent = new Intent(this, com.ti.tecnoimport.vendedor.DashboardVendedorActivity.class);
                break;
            case Usuario.ROL_COMPRADOR:
            default:
                intent = new Intent(this, DashboardCompradorActivity.class);
                break;
        }
        intent.putExtra("USUARIO_ACTUAL", usuario);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Navega hacia la pantalla de inicio de sesión limpiando la pila de actividades.
     */
    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}