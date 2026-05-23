package com.ti.tecnoimport.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ti.tecnoimport.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreoLogin, etPasswordLogin;
    private Button btnLogin;
    private TextView txtCrearCuenta;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias XML
        etCorreoLogin = findViewById(R.id.etCorreoLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        txtCrearCuenta = findViewById(R.id.txtCrearCuenta);

        // Ir a registro
        txtCrearCuenta.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        // Login Firebase
        btnLogin.setOnClickListener(v -> loginUsuario());
    }

    private void loginUsuario() {

        String correo = etCorreoLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(correo)) {
            etCorreoLogin.setError("Ingrese su correo");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPasswordLogin.setError("Ingrese su contraseña");
            return;
        }

        // Login Firebase
        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Inicio de sesión exitoso",
                                Toast.LENGTH_LONG
                        ).show();

                        // Aquí luego enviaremos al Dashboard

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Credenciales incorrectas",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}