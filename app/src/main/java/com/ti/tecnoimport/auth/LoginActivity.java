package com.ti.tecnoimport.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.admin.DashboardAdminActivity;
import com.ti.tecnoimport.comprador.DashboardCompradorActivity;
import com.ti.tecnoimport.models.Usuario;
import com.ti.tecnoimport.repositories.AuthRepository;
import com.ti.tecnoimport.utils.BiometricHelper;
import com.ti.tecnoimport.utils.SessionManager;

/**
 * Pantalla de inicio de sesión de TecnoImport.
 * Soporta autenticación tradicional mediante Firebase Auth, recuperación de contraseña
 * por correo electrónico y acceso rápido mediante sensor biométrico (huella dactilar).
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etPassword;
    private Button btnIniciarSesion;
    private ImageButton btnHuella;
    private TextView tvOlvidePassword, tvIrARegistro;

    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();
        sessionManager = new SessionManager(this);

        // Vincular vistas del layout activity_login.xml (ajusta los IDs si difieren)
        etCorreo = findViewById(R.id.etCorreoLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnHuella = findViewById(R.id.btnHuella);
        tvOlvidePassword = findViewById(R.id.tvOlvidePassword);
        tvIrARegistro = findViewById(R.id.tvIrARegistro);

        btnIniciarSesion.setOnClickListener(v -> ejecutarLoginManual());
        btnHuella.setOnClickListener(v -> ejecutarLoginBiometrico());
        tvOlvidePassword.setOnClickListener(v -> mostrarDialogoRecuperacion());
        tvIrARegistro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    /**
     * Valida las credenciales ingresadas y solicita autenticación a Firebase Auth.
     */
    private void ejecutarLoginManual() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        btnIniciarSesion.setEnabled(false);
        authRepository.iniciarSesion(correo, password, new AuthRepository.AuthCallback() {
            @Override
            public void onExito(Usuario usuario) {
                btnIniciarSesion.setEnabled(true);
                // Guarda credenciales cifradas para habilitar futuros ingresos con huella
                sessionManager.guardarCredencialesBiometricas(correo, password, usuario.getRol());
                navegarAlDashboard(usuario);
            }

            @Override
            public void onError(String mensaje) {
                btnIniciarSesion.setEnabled(true);
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Verifica disponibilidad del sensor físico de huella y autentica con credenciales cifradas.
     */
    private void ejecutarLoginBiometrico() {
        if (!BiometricHelper.esBiometriaDisponible(this)) {
            Toast.makeText(this, "Este dispositivo no tiene huella dactilar configurada", Toast.LENGTH_LONG).show();
            return;
        }
        if (!sessionManager.tieneBiometriaConfigurada()) {
            Toast.makeText(this, "Inicia sesión una vez con tu contraseña para vincular tu huella", Toast.LENGTH_LONG).show();
            return;
        }

        BiometricHelper.autenticarConHuella(this, new BiometricHelper.BiometricCallback() {
            @Override
            public void onAutenticacionExitosa() {
                String emailGuardado = sessionManager.getEmail();
                String passGuardada = sessionManager.getPassword();
                authRepository.iniciarSesion(emailGuardado, passGuardada, new AuthRepository.AuthCallback() {
                    @Override
                    public void onExito(Usuario usuario) {
                        navegarAlDashboard(usuario);
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onAutenticacionError(String mensaje) {
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Despliega un cuadro de diálogo para enviar el enlace de restablecimiento de contraseña.
     */
    private void mostrarDialogoRecuperacion() {
        EditText inputCorreo = new EditText(this);
        inputCorreo.setHint("Correo electrónico registrado");
        inputCorreo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(this)
                .setTitle("Recuperar contraseña")
                .setMessage("Te enviaremos un enlace a tu correo para restablecer tu clave.")
                .setView(inputCorreo)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String correo = inputCorreo.getText().toString().trim();
                    if (TextUtils.isEmpty(correo)) {
                        Toast.makeText(this, "Escribe un correo válido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    authRepository.enviarRecuperacionPassword(correo, new AuthRepository.SimpleCallback() {
                        @Override
                        public void onExito() {
                            Toast.makeText(LoginActivity.this,
                                    "Correo de recuperación enviado. Revisa tu bandeja.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(String mensaje) {
                            Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Redirige a la pantalla principal según el rol del usuario autenticado.
     */
    private void navegarAlDashboard(Usuario usuario) {
        Intent intent;
        switch (usuario.getRol()) {
            case Usuario.ROL_ADMIN:
                intent = new Intent(this, DashboardAdminActivity.class);
                break;
            case Usuario.ROL_VENDEDOR:
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
}