package com.ti.tecnoimport.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.auth.LoginActivity;

public class DashboardAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CORREGIDO: Apunta exactamente al archivo dash_admin.xml de tu carpeta layout
        setContentView(R.layout.activity_dash_admin);

        // Validación preventiva de sesión por seguridad
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Sesión vencida o inválida", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
