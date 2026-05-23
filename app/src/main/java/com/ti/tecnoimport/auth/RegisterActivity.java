package com.ti.tecnoimport.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ti.tecnoimport.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etPassword;
    private Button btnRegistrar;

    private RadioGroup radioGroupRoles;
    private RadioButton radioAdmin, radioVendedor, radioComprador;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        radioGroupRoles = findViewById(R.id.radioGroupRoles);
        radioAdmin = findViewById(R.id.radioAdmin);
        radioVendedor = findViewById(R.id.radioVendedor);
        radioComprador = findViewById(R.id.radioComprador);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese nombre");
            return;
        }

        if (TextUtils.isEmpty(correo)) {
            etCorreo.setError("Ingrese correo");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese contraseña");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            return;
        }

        int selectedId = radioGroupRoles.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show();
            return;
        }

        String rol = "";

        if (selectedId == R.id.radioAdmin) {
            rol = "admin";
        } else if (selectedId == R.id.radioVendedor) {
            rol = "vendedor";
        } else if (selectedId == R.id.radioComprador) {
            rol = "comprador";
        }

        String finalRol = rol;

        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> usuario = new HashMap<>();
                        usuario.put("nombre", nombre);
                        usuario.put("correo", correo);
                        usuario.put("rol", finalRol);
                        usuario.put("uid", uid);

                        db.collection("usuarios")
                                .document(uid)
                                .set(usuario)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Usuario registrado",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    startActivity(new Intent(
                                            RegisterActivity.this,
                                            LoginActivity.class
                                    ));

                                    finish();

                                }).addOnFailureListener(e -> {

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Error Firestore",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });

                    } else {

                        Toast.makeText(
                                RegisterActivity.this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}