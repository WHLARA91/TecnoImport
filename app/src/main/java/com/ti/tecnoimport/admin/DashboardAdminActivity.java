package com.ti.tecnoimport.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.auth.LoginActivity;
import com.ti.tecnoimport.models.Usuario;
import com.ti.tecnoimport.repositories.AuthRepository;
import com.ti.tecnoimport.repositories.UsuarioRepository;
import com.ti.tecnoimport.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel principal del rol Administrador.
 * Implementa el CRUD completo (Listar, Crear, Editar rol/estado y Eliminar)
 * sobre la colección 'usuarios' de Cloud Firestore.
 */
public class DashboardAdminActivity extends AppCompatActivity {

    private ListView lvUsuarios;
    private Button btnNuevoUsuario, btnCerrarSesion;

    private UsuarioRepository usuarioRepository;
    private AuthRepository authRepository;
    private final List<Usuario> listaUsuarios = new ArrayList<>();
    private final List<String> resumenUsuarios = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_admin);

        usuarioRepository = new UsuarioRepository();
        authRepository = new AuthRepository();

        lvUsuarios = findViewById(R.id.lvUsuariosAdmin);
        btnNuevoUsuario = findViewById(R.id.btnCrearUsuarioAdmin);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resumenUsuarios);
        lvUsuarios.setAdapter(listAdapter);

        // Pulsación corta: editar usuario; pulsación larga: eliminar usuario
        lvUsuarios.setOnItemClickListener((parent, view, position, id) ->
                mostrarDialogoFormularioUsuario(listaUsuarios.get(position)));

        lvUsuarios.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmarEliminacionUsuario(listaUsuarios.get(position));
            return true;
        });

        btnNuevoUsuario.setOnClickListener(v -> mostrarDialogoFormularioUsuario(null));
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        cargarUsuarios();
    }

    /**
     * Consulta todos los usuarios registrados en Firestore y refresca la lista.
     */
    private void cargarUsuarios() {
        usuarioRepository.listarUsuarios(new UsuarioRepository.ListaUsuariosCallback() {
            @Override
            public void onResultado(List<Usuario> usuarios) {
                listaUsuarios.clear();
                resumenUsuarios.clear();
                listaUsuarios.addAll(usuarios);
                for (Usuario u : usuarios) {
                    String estado = u.isActivo() ? "Activo" : "Inactivo";
                    resumenUsuarios.add(u.getNombre() + " (" + u.getRol() + ") - " + u.getCorreo() + " [" + estado + "]");
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(DashboardAdminActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Despliega un formulario modal para crear o actualizar un usuario en Firestore.
     *
     * @param usuarioEditar Usuario existente a modificar, o null si es creación.
     */
    private void mostrarDialogoFormularioUsuario(Usuario usuarioEditar) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 12);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre completo");
        EditText etCorreo = new EditText(this);
        etCorreo.setHint("Correo electrónico");
        EditText etTelefono = new EditText(this);
        etTelefono.setHint("Teléfono");

        Spinner spinnerRol = new Spinner(this);
        String[] roles = {Usuario.ROL_COMPRADOR, Usuario.ROL_VENDEDOR, Usuario.ROL_ADMIN};
        spinnerRol.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles));

        Switch switchActivo = new Switch(this);
        switchActivo.setText("Usuario Activo");
        switchActivo.setChecked(true);

        if (usuarioEditar != null) {
            etNombre.setText(usuarioEditar.getNombre());
            etCorreo.setText(usuarioEditar.getCorreo());
            etTelefono.setText(usuarioEditar.getTelefono());
            switchActivo.setChecked(usuarioEditar.isActivo());
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].equals(usuarioEditar.getRol())) {
                    spinnerRol.setSelection(i);
                }
            }
        }

        layout.addView(etNombre);
        layout.addView(etCorreo);
        layout.addView(etTelefono);
        layout.addView(spinnerRol);
        layout.addView(switchActivo);

        new AlertDialog.Builder(this)
                .setTitle(usuarioEditar == null ? "Nuevo Usuario" : "Editar Usuario")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    Usuario u = (usuarioEditar != null) ? usuarioEditar : new Usuario();
                    u.setNombre(etNombre.getText().toString().trim());
                    u.setCorreo(etCorreo.getText().toString().trim());
                    u.setTelefono(etTelefono.getText().toString().trim());
                    u.setRol(spinnerRol.getSelectedItem().toString());
                    u.setActivo(switchActivo.isChecked());

                    usuarioRepository.guardarOActualizarUsuario(u, new UsuarioRepository.OperacionCallback() {
                        @Override
                        public void onExito() {
                            Toast.makeText(DashboardAdminActivity.this, "Usuario guardado en Firestore", Toast.LENGTH_SHORT).show();
                            cargarUsuarios();
                        }

                        @Override
                        public void onError(String mensaje) {
                            Toast.makeText(DashboardAdminActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Solicita confirmación antes de eliminar un documento de usuario en Firestore.
     */
    private void confirmarEliminacionUsuario(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Deseas eliminar permanentemente a " + usuario.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        usuarioRepository.eliminarUsuario(usuario.getUid(), new UsuarioRepository.OperacionCallback() {
                            @Override
                            public void onExito() {
                                Toast.makeText(DashboardAdminActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                cargarUsuarios();
                            }

                            @Override
                            public void onError(String mensaje) {
                                Toast.makeText(DashboardAdminActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        authRepository.cerrarSesion();
        new SessionManager(this).cerrarSesionActiva();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}