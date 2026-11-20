package com.ti.tecnoimport.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.Usuario;
import com.ti.tecnoimport.repositories.AuthRepository;

/**
 * Pantalla de registro de nuevos usuarios en TecnoImport.
 * Conecta la interfaz personalizada con Firebase Authentication y persiste
 * el perfil con el rol seleccionado en Cloud Firestore.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etPassword;
    private RadioGroup radioGroupRoles;
    private Button btnRegistrar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();

        // Vincular los IDs exactos definidos en activity_register.xml
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        radioGroupRoles = findViewById(R.id.radioGroupRoles);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarCuenta());
    }

    /**
     * Valida los campos del formulario, obtiene el rol marcado en el RadioGroup
     * y registra el usuario en Firebase Authentication y Cloud Firestore.
     */
    private void registrarCuenta() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String rolSeleccionado = obtenerRolSeleccionado();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Ingresa tu nombre completo y correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rolSeleccionado == null) {
            Toast.makeText(this, "Selecciona un rol para la cuenta", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrar.setEnabled(false);

        authRepository.registrarUsuario(nombre, correo, password, "", rolSeleccionado, new AuthRepository.AuthCallback() {
            @Override
            public void onExito(Usuario usuario) {
                Toast.makeText(RegisterActivity.this,
                        "Registro exitoso como " + usuario.getRol(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String mensaje) {
                btnRegistrar.setEnabled(true);
                Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Identifica qué RadioButton fue seleccionado dentro de radioGroupRoles
     * y devuelve la constante de rol correspondiente.
     *
     * @return Rol en mayúsculas (ADMIN, VENDEDOR, COMPRADOR) o null si no hay selección.
     */
    private String obtenerRolSeleccionado() {
        int selectedId = radioGroupRoles.getCheckedRadioButtonId();
        if (selectedId == R.id.radioAdmin) {
            return Usuario.ROL_ADMIN;
        } else if (selectedId == R.id.radioVendedor) {
            return Usuario.ROL_VENDEDOR;
        } else if (selectedId == R.id.radioComprador) {
            return Usuario.ROL_COMPRADOR;
        }
        return null;
    }
}