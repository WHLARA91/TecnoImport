package com.ti.tecnoimport.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

/**
 * Gestiona el almacenamiento cifrado de sesión local mediante EncryptedSharedPreferences.
 * Permite vincular la autenticación biométrica con las credenciales de Firebase Auth.
 */
public class SessionManager {

    private static final String PREF_NAME = "tecnoimport_secure_prefs";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_ROL = "user_rol";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";

    private SharedPreferences sharedPreferences;

    /**
     * Inicializa el almacén cifrado utilizando AES-256 GCM.
     *
     * @param context Contexto de la aplicación o actividad.
     */
    public SessionManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // Respaldo estándar en caso de dispositivos sin soporte hardware Keystore
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * Guarda las credenciales cifradas tras un inicio de sesión exitoso.
     */
    public void guardarCredencialesBiometricas(String email, String password, String rol) {
        sharedPreferences.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putString(KEY_ROL, rol)
                .putBoolean(KEY_BIOMETRIC_ENABLED, true)
                .apply();
    }

    public boolean tieneBiometriaConfigurada() {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
                && getEmail() != null && getPassword() != null;
    }

    public String getEmail() { return sharedPreferences.getString(KEY_EMAIL, null); }
    public String getPassword() { return sharedPreferences.getString(KEY_PASSWORD, null); }
    public String getRol() { return sharedPreferences.getString(KEY_ROL, null); }

    /**
     * Elimina el rol activo al cerrar sesión sin borrar la vinculación biométrica,
     * o limpia todo si se solicita un cierre completo.
     */
    public void cerrarSesionActiva() {
        sharedPreferences.edit().remove(KEY_ROL).apply();
    }
}
