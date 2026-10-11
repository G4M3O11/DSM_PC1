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

Aplicación móvil Android desarrollada en **Kotlin** utilizando **Jetpack Compose** para la Práctica Calificada 01 del curso Desarrollo de Soluciones Móviles.

## 🛠️ Tecnologías y Requisitos

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
| `VistaInicioSesion` | Formulario ilustrativo y acceso al prototipo sin credenciales. |
| `VistaRegistroCuenta` | Formulario ilustrativo, volver al inicio y continuar a la jugada. |
| `VistaJugada` | Panel de números del **1 al 45**, jugada de ejemplo y acceso al sorteo. |
| `VistaSorteo` | Bolillas y progreso de ejemplo; regreso a la jugada o salida al inicio. |

**Alcance actual:** solo interfaces y navegación. No hay autenticación, creación de cuentas, selección funcional, registro de jugadas, persistencia ni conexión a Firebase. Los formularios no envían datos y permiten continuar vacíos. Las bolillas y el progreso son ejemplos estáticos, identificados como tales en pantalla.

Las cuatro interfaces tienen vistas previas en `presentacion/VistasPrevias.kt`, visibles en el panel **Design / Split** de Android Studio.

### Recorrido navegable

```text
Inicio de sesión ── Crear cuenta ── Registro de cuenta
       │                                   │
       └── Ingresar al prototipo           └── Continuar al prototipo
                    │                                  │
                    └────────── Mi jugada ──────────────┘
                                    │
                                Ver sorteo
                                    │
                                  Sorteo
```

**Volver** desde registro regresa al inicio. Desde sorteo se vuelve a la jugada, también mediante el botón Atrás de Android. **Salir** desde jugada o sorteo regresa al inicio y limpia la pila de navegación. Al entrar al prototipo se retiran las vistas de acceso de la pila.

### Arquitectura adaptada de `script_base.md`

Se mantienen una sola Activity, Compose Material 3, catálogo de versiones y Navigation Compose. No se incorporan capas de datos, ViewModel ni servicios hasta que el MVP los requiera. El archivo `app/google-services.json` existente se conserva, pero no se utiliza ni se necesita configurar Firebase para ejecutar este MVP.

```text
app/src/main/java/dsm/davidalamo/pc1/
├── ActividadPrincipal.kt
├── presentacion/
│   ├── VistasPrevias.kt
│   ├── acceso/       # VistaInicioSesion, VistaRegistroCuenta
│   ├── jugada/       # VistaJugada
│   ├── sorteo/       # VistaSorteo
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

Con un emulador o dispositivo conectado, las pruebas de navegación recorren las cuatro vistas, los accesos sin credenciales, el regreso y la salida:

```bash
bash ./gradlew :app:connectedDebugAndroidTest
```

El enunciado completo (Firebase Auth, jugadas y sorteo compartido en tiempo real) queda fuera del alcance del MVP 1.
