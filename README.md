# 📱 TecnoImport — Tienda Virtual Android Multirrol

![Android](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Language-Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/Backend-Firebase%20(Auth%20%7C%20Firestore%20%7C%20Storage)-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![ePayco](https://img.shields.io/badge/Payments-ePayco%20Checkout-009EE3?style=for-the-badge)

Aplicación móvil nativa para Android orientada al comercio electrónico de dispositivos y accesorios tecnológicos de alta demanda (celulares, laptops, audífonos, relojes inteligentes y periféricos gamer), desarrollada bajo una arquitectura modular con gestión independiente por roles de usuario.

---

## 📋 Información Académica

* **Institución:** Fundación Universitaria Compensar (UCOMPENSAR)
* **Programa:** Ingeniería de Software
* **Asignatura:** Desarrollo de Aplicaciones Móviles Nativas
* **Autor:** Wilson Herrera (`@WHLARA91`)
* **Ubicación:** Gachancipá, Cundinamarca, Colombia

---

## 🚀 Funcionalidades Principales por Rol

### 🔐 Módulo Transversal (Seguridad y Acceso)
* **Autenticación híbrida:** Registro e inicio de sesión mediante **Firebase Authentication** (correo y contraseña) con validación de roles en tiempo real.
* **Acceso biométrico:** Inicio de sesión rápido mediante huella dactilar (`androidx.biometric.BiometricPrompt`) respaldado por almacenamiento cifrado local (`EncryptedSharedPreferences`).
* **Recuperación de contraseña:** Envío automatizado de enlaces de restablecimiento de clave al correo registrado.
* **Cierre de sesión seguro:** Limpieza de estado de sesión activa y retorno controlado al login.

### 🛡️ Rol Administrador
* **Gestión de Usuarios (CRUD):** Visualización en tiempo real de los usuarios registrados en Cloud Firestore, creación de nuevas cuentas con asignación de rol, edición de datos/perfiles y eliminación de registros.

### 🏪 Rol Vendedor
* **Gestión de Catálogo (CRUD):** Publicación, edición y eliminación de productos tecnológicos propios.
* **Multimedia con Cámara y Galería:** Captura de fotografías en tiempo real mediante `FileProvider` (con permisos en tiempo de ejecución) o selección desde galería, almacenadas en **Firebase Storage**.
* **Resumen de Ventas:** Visualización de métricas de pedidos realizados e ingresos netos calculados en tiempo real.

### 🛒 Rol Comprador
* **Catálogo Interactivo:** Exploración de productos disponibles con precios formateados en moneda local (COP), stock e imágenes cargadas dinámicamente con **Glide**.
* **Carrito de Compras Persistente:** Adición, cálculo automático de subtotales/totales y eliminación de artículos sincronizados con Cloud Firestore.
* **Geolocalización GPS:** Captura automática de coordenadas y dirección legible de entrega mediante `FusedLocationProviderClient` y `Geocoder`.
* **Pasarela de Pagos ePayco:** Integración de checkout en modo pruebas (`test='true'`) con generación de referencia de transacción y registro automático en el historial de compras.

---

## 🏗️ Arquitectura y Estructura del Proyecto

El código fuente está organizado por capas funcionales y módulos de rol dentro del paquete `com.ti.tecnoimport`:

```text
app/src/main/java/com/ti/tecnoimport/
├── adapters/         # Adaptadores de RecyclerView (Catálogo, Carrito, Órdenes)
├── admin/            # DashboardAdminActivity (CRUD de usuarios)
├── auth/             # LoginActivity y RegisterActivity
├── comprador/        # DashboardCompradorActivity, CartActivity, PurchaseHistoryActivity
├── models/           # Entidades POJO (Usuario, Producto, ItemCarrito, Orden)
├── repositories/     # Capa de acceso a datos (Auth, Usuario, Producto, CarritoOrden, ePayco)
├── utils/            # Helpers de hardware y seguridad (BiometricHelper, LocationHelper, SessionManager)
├── vendedor/         # DashboardVendedorActivity y AddProductActivity
└── MainActivity.java # Splash Screen y enrutamiento inicial por rol
```

### Colecciones en Cloud Firestore
* `usuarios`: Perfiles de usuario indexados por `UID` con atributo de `rol` (`ADMIN`, `VENDEDOR`, `COMPRADOR`).
* `productos`: Catálogo tecnológico con título, descripción, precio, stock, categoría, `vendedorId` e `imagenUrl`.
* `carrito`: Ítems activos seleccionados por cada comprador.
* `compras`: Órdenes confirmadas con detalle de artículos, total pagado, coordenadas GPS, dirección de entrega y referencia de pago ePayco.

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 17
* **SDK Android:** `minSdk 26` (Android 8.0 Oreo) / `targetSdk 34` (Android 14)
* **Sistema de Construcción:** Gradle con Kotlin DSL (`build.gradle.kts`) y `ViewBinding`
* **Servicios en la Nube:** Firebase Authentication, Cloud Firestore y Firebase Storage
* **Librerías Principales:**
    * `androidx.biometric:biometric` (Autenticación por huella)
    * `androidx.security:security-crypto` (Cifrado de preferencias locales)
    * `com.google.android.gms:play-services-location` (Servicios de ubicación GPS)
    * `com.github.bumptech.glide:glide` (Carga y caché de imágenes)
    * `com.google.android.material:material` (Componentes Material Design)

---

## ⚖️ Marco Normativo Aplicado (Colombia)

* **Ley 1581 de 2012 (Protección de Datos Personales):** Cifrado local de credenciales sensibles y reglas de acceso por propietario en Firebase.
* **Ley 527 de 1999 (Comercio Electrónico):** Trazabilidad de órdenes digitales y confirmación de transacciones mediante pasarela certificada.
* **Ley 1480 de 2011 (Estatuto del Consumidor):** Transparencia en precios, descripción de productos, stock disponible e historial de compras accesible para el comprador.

---

## ⚙️ Instalación y Ejecución

1. Clonar este repositorio:
   ```bash
   git clone [https://github.com/WHLARA91/TecnoImport.git](https://github.com/WHLARA91/TecnoImport.git)
   ```
2. Abrir la carpeta raíz en **Android Studio** (Iguana o superior).
3. Verificar que el archivo `google-services.json` esté ubicado dentro de `app/`.
4. Sincronizar dependencias de Gradle y ejecutar en un dispositivo físico Android 8.0+ (o generar el instalador mediante `./gradlew assembleDebug`).