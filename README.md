# `<24200433>-<alamo>-pc01-dsm202602-loteria`
# `<21200159>-<saravia>-pc01-dsm202602-loteria`

## Práctica Calificada 1 - Desarrollo de Soluciones Móviles (DSM 2026-02)

### Información del Proyecto

- **Curso:** Desarrollo de Soluciones Móviles (2026-02)
- **Evaluación:** Práctica Calificada 01 (PC01)
- **Tema:** Lotería

### Integrantes

- **Álamo Guizado, David Eduardo** (Código: `24200433`)
- **Saravia, Cesar** (Código: `21200159`)

---

## 📱 Descripción

**Seisora** es una aplicación móvil Android desarrollada en **Kotlin** utilizando **Jetpack Compose** para la Práctica Calificada 01 del curso Desarrollo de Soluciones Móviles. Su nombre combina “seis” y “ahora”, evocando las seis bolillas y el seguimiento en tiempo real.

## 🛠️ Tecnologías y Requisitos

La identidad visual de Seisora combina azul medianoche, turquesa y dorado, con fondos marfil en modo claro y azul profundo en modo oscuro. Todas las vistas comparten una cabecera con logo vectorial original, tarjetas redondeadas y bolillas con aciertos destacados en dorado. El icono del lanzador utiliza el mismo monograma. Los avisos de prototipo se eliminaron; los errores y estados de conexión reales permanecen visibles.

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Min SDK:** 34 (Android 14)
- **Target / Compile SDK:** 37
- **Build System:** Gradle 9.6.0 + AGP 9.4.0
- **JDK:** Java 11 / Foojay JDK 25

## 📂 Estructura del Proyecto

```
DSM_PC1/
├── app/                  # Módulo principal de la aplicación
│   ├── src/              # Código fuente en Kotlin y recursos
│   └── build.gradle.kts  # Gradle del módulo app
├── gradle/               # Wrapper y versiones (libs.versions.toml)
├── .gitignore            # Archivo de ignorados para Git
├── build.gradle.kts      # Gradle del proyecto raíz
├── settings.gradle.kts   # Configuración de repositorios
└── README.md             # Documentación del proyecto
```

## 🚀 Instalación y Ejecución

1. Clonar el repositorio.
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar las dependencias con Gradle (*Sync Project with Gradle Files*).
4. Ejecutar la aplicación en un dispositivo físico o emulador con Android 14+ (API Level 34+).

## MVP 1: vistas y navegación

| Pantalla | Función |
|---|---|
| `VistaInicioSesion` | Inicio de sesión real con correo y contraseña en Firebase Auth. |
| `VistaRegistroCuenta` | Creación de cuenta real y acceso con el UID devuelto por Firebase Auth. |
| `VistaJugada` | Selección de seis números del **1 al 45** y registro real en Firestore. |
| `VistaSorteo` | Bolillas en tiempo real, jugada almacenada, aciertos y resultado final. |

**Alcance actual:** autenticación con correo y contraseña, registro de jugadas y seguimiento real en Firestore. El formulario llama a Firebase Auth y navega solo si la autenticación tiene éxito. Mi jugada utiliza `FirebaseAuth.currentUser.uid`, correspondiente al usuario que retorna Auth. No se inicia autenticación anónima ni se fija un UID manualmente.

Las cuatro interfaces tienen vistas previas en `presentacion/VistasPrevias.kt`, visibles en el panel **Design / Split** de Android Studio.

### Recorrido navegable

```text
Inicio de sesión ── Crear cuenta ── Registro de cuenta
       │                                   │
       └── Iniciar sesión                  └── Registrar cuenta
                    │                                  │
                    └────────── Mi jugada ──────────────┘
                                    │
                                Ver sorteo
                                    │
                                  Sorteo
```

**Volver** desde registro regresa al inicio. Desde sorteo se vuelve a la jugada, también mediante el botón Atrás de Android. **Salir** cierra Firebase Auth, regresa al inicio y limpia la pila de navegación. Al autenticar se retiran las vistas de acceso de la pila.

### Arquitectura adaptada de `script_base.md`

Se mantienen una sola Activity, Compose Material 3, catálogo de versiones y Navigation Compose. El registro incorpora `datos/modelos/Jugada.kt`, `datos/remoto/RepositorioJugadas.kt` y `presentacion/jugada/ModeloVistaJugada.kt`. Se utiliza el archivo existente `app/google-services.json`.

```text
app/src/main/java/dsm/davidalamo/pc1/
├── ActividadPrincipal.kt
├── datos/
│   ├── modelos/     # Jugada, Sorteo, Bolilla, ResumenSorteo y validaciones
│   └── remoto/      # RepositorioJugadas, RepositorioSeguimiento, LecturaDocumentos
├── presentacion/
│   ├── VistasPrevias.kt
│   ├── acceso/       # VistaInicioSesion, VistaRegistroCuenta
│   ├── jugada/       # VistaJugada, ModeloVistaJugada
│   ├── sorteo/       # VistaSorteo, ModeloVistaSorteo
│   ├── componentes/ # ContenedorVista, CampoContrasena, FilaBolillas
│   └── navegacion/  # NavegacionAplicacion, Rutas
└── ui/tema/          # Colores, Tipografia, TemaBolilla
```

### Convención de nombres

Los identificadores propios se escriben en español, sin tildes ni `ñ`: `PascalCase` para tipos y vistas, `camelCase` para funciones, parámetros y variables; `MAYUSCULAS_CON_GUION_BAJO` para constantes y `minusculas_con_guion_bajo` para rutas. Los paquetes usan nombres en español en minúsculas.

Se conservan los nombres de APIs de Android, Compose, Gradle y otras dependencias porque son contratos externos, por ejemplo `onCreate`, `Modifier`, `NavHost` y `onClick`. La Activity es `ActividadPrincipal` y el tema es `TemaBolilla`.

### Compilación y pruebas

```bash
bash ./gradlew :app:assembleDebug :app:testDebugUnitTest
```

Con un emulador o dispositivo conectado, las pruebas recorren las cuatro vistas, el acceso, el regreso y la salida. Los tests de interfaz usan operaciones de autenticación inyectadas, sin credenciales reales ni creación de cuentas remotas:

```bash
bash ./gradlew :app:connectedDebugAndroidTest
```

## Registro de jugadas en Firestore

### Preparación en la consola

1. Verifica que `app/google-services.json` corresponda al proyecto Firebase de la app `dsm.davidalamo.pc1`.
2. En **Authentication → Sign-in method**, habilita **Email/Password**. Ingresa con una cuenta existente o créala desde Registrar cuenta. La autenticación anónima ya no es necesaria.
3. Crea Cloud Firestore en la base de datos predeterminada.
4. Publica el contenido de `firestore.rules` en **Firestore → Reglas**.
5. Crea la cabecera **`sorteos/actual`**:

| Campo | Tipo | Valor |
|---|---|---|
| `identificador` | string | `actual` |
| `nombre` | string | `Sorteo Seisora` |
| `fecha` | timestamp | Fecha y hora del sorteo |
| `estado` | string | `abierto` |

Estados de la cabecera: `abierto`, `en_curso`, `finalizado`. La app no crea una cabecera inventada ni permite participar si falta o tiene campos inválidos.

### Organización de datos

```text
sorteos/actual
  identificador: "actual"
  nombre: "Sorteo Seisora"
  fecha: timestamp
  estado: "abierto"
  jugadas/{uid}
    identificadorSorteo: "actual"
    uidUsuario: UID de Firebase Auth
    numeros: [1, 7, 14, 22, 35, 45]
    fechaRegistro: timestamp del servidor
  bolillas/{documento}
    numero: 1
    ordenSalida: 1
```

Las bolillas quedan vinculadas por su ubicación bajo el sorteo. Se publican desde la consola seis documentos con `numero` entero del 1 al 45, sin repetir, y `ordenSalida` entero del 1 al 6, único y consecutivo. La app escucha estas publicaciones, pero no las crea ni modifica. Nunca se almacenan `GANADOR` ni `SIGUE INTENTANDO`.

Los números de la jugada se almacenan y muestran **en el orden de selección**, sin ordenarlos. Si deseleccionas y vuelves a elegir un número, queda al final. Las jugadas antiguas que se guardaron ordenadas se mostrarán en el orden que ya tienen en Firestore; su orden de selección original no se puede reconstruir.

### Uso e integración

En **Mi jugada**, espera la carga de la cabecera y el UID. Toca números para seleccionar o deseleccionar; solo puedes elegir seis. Pulsa **Registrar jugada**, revisa los números y confirma. El mensaje de registro exitoso solo aparece cuando Firestore confirma la transacción. La jugada existente se recupera al volver o reiniciar.

La selección se conserva durante rotaciones mediante el ViewModel; no se persiste antes de confirmar. La transacción vuelve a verificar en el servidor que el sorteo esté abierto y que no exista `jugadas/{uid}`. Las reglas validan UID, relación, seis enteros únicos, rango y fecha del servidor, y rechazan modificaciones y eliminaciones desde la app.

**Salir** cierra la sesión de Firebase Auth. Para demostrar dos participantes usa dos cuentas distintas en dos dispositivos/emuladores. Al ingresar de nuevo con la misma cuenta se recupera el mismo UID y su jugada. Las jugadas creadas anteriormente con UID anónimo no se trasladan automáticamente a una cuenta con correo; cada documento permanece asociado a su UID original.

Si no hay conexión, falta la cabecera o las reglas deniegan acceso, se muestra el error y un botón de reintento. Las credenciales incorrectas se muestran como error en el formulario y no permiten avanzar. No se presentan datos de caché como confirmación de registro. Publicar reglas y configurar Firebase es un paso manual; el código no modifica el proyecto remoto.

## Seguimiento en tiempo real y demostración

La pantalla escucha la cabecera `sorteos/actual`, la colección completa `sorteos/actual/bolillas` y la jugada `sorteos/actual/jugadas/{uid}` mediante `addSnapshotListener`. No usa consultas periódicas ni animaciones locales. Todos los documentos de bolillas se leen para detectar incluso registros sin `ordenSalida`; después se validan y ordenan en la app.

La escucha está vinculada al estado **RESUMED** de la pantalla de navegación. Al salir, pasar a otra pantalla o poner la app en segundo plano, la colección se cancela inmediatamente y `awaitClose` elimina los listeners de Firestore y Auth. Al volver se crea una sola colección activa, sin reutilizar resultados obsoletos. Si cambia el usuario autenticado, se cancela la escucha anterior y se observa la jugada del nuevo UID.

La cabecera debe tener identificador válido, nombre, fecha timestamp y estado válido. Se rechazan más de seis bolillas, números no enteros o fuera del rango, repetidos, órdenes fuera del 1 al 6, duplicados o no consecutivos. También se validan los seis números, el UID y la relación de la jugada almacenada. Los errores se muestran y bloquean el resultado; corregir el documento en la consola actualiza la vista automáticamente.

**Mensajes y cálculo:** en `abierto` se espera el inicio; en `en_curso` se muestra “Sorteo en curso”. Los aciertos se calculan por coincidencias de números distintos, independientemente del orden. No se muestran porcentajes. Solo con `finalizado`, seis bolillas válidas y datos confirmados por el servidor se muestra **GANADOR** (6 aciertos) o **SIGUE INTENTANDO** (menos de 6). Si falta alguna bolilla se mantiene “Esperando resultados”. La caché se identifica y no produce un resultado definitivo sin confirmación.

### Demostración obligatoria con dos participantes

Usa dos emuladores o dispositivos con cuentas de correo Firebase Auth distintas, conectadas al **mismo proyecto** y al documento **`sorteos/actual`**. Verifica los UID en Mi jugada: deben coincidir con Authentication en la consola y ser diferentes entre participantes.

1. Con `estado: abierto` y sin bolillas publicadas, registra y confirma estas jugadas:

| Participante | Jugada en orden de selección | Resultado esperado |
|---|---|---|
| Usuario A | `5, 12, 18, 23, 34, 41` | 6 aciertos · GANADOR |
| Usuario B | `5, 12, 18, 23, 34, 42` | 5 aciertos · SIGUE INTENTANDO |

2. Abre **Ver sorteo** en ambos dispositivos. Cambia la cabecera a `estado: en_curso` (valor técnico para “en curso”) y comprueba que **ambas pantallas** muestren “Sorteo en curso”.
3. Crea los siguientes documentos **uno por uno**, con campos de tipo **number**, no strings ni decimales. Después de guardar cada documento, espera y muestra su aparición en ambas sesiones **antes de crear el siguiente**. No agregues las seis bolillas juntas:

| Documento en `sorteos/actual/bolillas` | `numero` | `ordenSalida` |
|---|---|---|
| `bolilla_1` | 5 | 1 |
| `bolilla_2` | 12 | 2 |
| `bolilla_3` | 18 | 3 |
| `bolilla_4` | 23 | 4 |
| `bolilla_5` | 34 | 5 |
| `bolilla_6` | 41 | 6 |

4. **Después de la quinta bolilla**, muestra que A y B tienen **5 de 6 aciertos**, sin resultado definitivo.
5. Publica la sexta (`41`): A debe tener 6 aciertos y B 5, todavía sin resultado mientras siga `en_curso`. Cambia la cabecera a `estado: finalizado`: A verá **GANADOR** y B **SIGUE INTENTANDO**. No agregues esos textos a ningún documento. Si finalizas antes de recibir la sexta, la app espera resultados.
6. Cierra y vuelve a abrir la app en uno de los dispositivos. Ingresa nuevamente con la misma cuenta y abre **Ver sorteo**. Deben recuperarse el mismo UID, la jugada, las seis bolillas y el resultado recalculado.

La evidencia debe incluir la consola durante **cada publicación individual**, las dos pantallas actualizadas, la comprobación tras la quinta bolilla, el cambio a finalizado y la recuperación tras reiniciar. Las pruebas automatizadas de cálculo y UI no sustituyen esta demostración conectada a Firebase.

### Código para mostrar y explicar

| Archivo | Responsabilidad |
|---|---|
| `datos/remoto/RepositorioSeguimiento.kt` | `addSnapshotListener` recibe cambios de cabecera, bolillas y jugada. `combine` recalcula el resumen con las tres fuentes. `awaitClose` elimina los listeners; un cambio de UID sustituye la escucha de jugada. |
| `presentacion/sorteo/ModeloVistaSorteo.kt` | Comparte el estado con `WhileSubscribed(0)` y deja de escuchar sin suscriptores. No guarda resultados en Firestore. |
| `presentacion/navegacion/NavegacionAplicacion.kt` | Recoge el estado solo cuando Ver sorteo está en `RESUMED`. |
| `datos/modelos/SeguimientoSorteo.kt` | Valida bolillas y órdenes. La intersección de conjuntos calcula los aciertos. `resultadoFinal` exige `finalizado`, seis bolillas y confirmación del servidor. |
| `datos/remoto/RepositorioJugadas.kt` y `LecturaDocumentos.kt` | Recuperan la jugada del UID autenticado y conservan el orden almacenado. |

Los números de esta demostración solo son datos de prueba: no hay ganadores prefijados ni lógica especial para los usuarios A y B.

Para repetir, elimina los documentos de `bolillas` y `jugadas` desde la consola y restablece `estado: abierto`. Borrar solo la cabecera no borra las subcolecciones. Publica las reglas de `firestore.rules`: los clientes solo leen las bolillas; la administración se hace en la consola.
