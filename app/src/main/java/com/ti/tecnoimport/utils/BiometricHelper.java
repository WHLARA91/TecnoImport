package com.ti.tecnoimport.utils;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

/**
 * Utilidad encargada de validar el sensor biométrico del dispositivo físico
 * y desplegar el diálogo nativo de huella dactilar (BiometricPrompt).
 */
public class BiometricHelper {

    /**
     * Interfaz de retorno para comunicar el resultado de la lectura biométrica.
     */
    public interface BiometricCallback {
        void onAutenticacionExitosa();
        void onAutenticacionError(String mensaje);
    }

    /**
     * Verifica si el dispositivo físico cuenta con sensor biométrico activo y huellas registradas.
     *
     * @param activity Actividad anfitriona.
     * @return true si el sensor está disponible y listo para usarse.
     */
    public static boolean esBiometriaDisponible(FragmentActivity activity) {
        BiometricManager biometricManager = BiometricManager.from(activity);
        int resultado = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        return resultado == BiometricManager.BIOMETRIC_SUCCESS;
    }

    /**
     * Muestra el prompt del sistema operativo para autenticar mediante huella dactilar.
     *
     * @param activity Actividad anfitriona.
     * @param callback Manejador del resultado de autenticación.
     */
    public static void autenticarConHuella(FragmentActivity activity, BiometricCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(activity);

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onAutenticacionExitosa();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        callback.onAutenticacionError(errString.toString());
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onAutenticacionError("Huella no reconocida. Intenta nuevamente.");
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Ingreso Biométrico - TecnoImport")
                .setSubtitle("Coloca tu huella en el sensor para iniciar sesión")
                .setNegativeButtonText("Usar contraseña")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
