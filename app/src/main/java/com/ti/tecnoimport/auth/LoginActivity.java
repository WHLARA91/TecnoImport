package com.ti.tecnoimport.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.admin.DashboardAdminActivity;
import com.ti.tecnoimport.comprador.DashboardCompradorActivity;
import com.ti.tecnoimport.vendedor.DashboardVendedorActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etPassword;
    private Button btnLogin;
    private TextView txtCrearCuenta;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etCorreo = findViewById(R.id.etCorreoLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        txtCrearCuenta = findViewById(R.id.txtCrearCuenta);

        btnLogin.setOnClickListener(v -> loginUsuario());
        txtCrearCuenta.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);

        });
    }

    private void loginUsuario() {

        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            etCorreo.setError("Ingrese correo");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese contraseña");
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        db.collection("usuarios")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    if (documentSnapshot.exists()) {

                                        String rol = documentSnapshot.getString("rol");

                                        if (rol.equals("admin")) {

                                            startActivity(new Intent(
                                                    LoginActivity.this,
                                                    DashboardAdminActivity.class
                                            ));

                                        } else if (rol.equals("vendedor")) {

                                            startActivity(new Intent(
                                                    LoginActivity.this,
                                                    DashboardVendedorActivity.class
                                            ));

                                        } else {

                                            startActivity(new Intent(
                                                    LoginActivity.this,
                                                    DashboardCompradorActivity.class
                                            ));
                                        }

                                        finish();
                                    }

                                });

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Error login",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}