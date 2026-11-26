# 📝 QuickFox Notes - Aplicación de Notas Rápidas para Android

**QuickFox Notes** es una aplicación nativa para Android diseñada para la creación, edición, organización y almacenamiento local de notas personales de forma rápida, intuitiva y fluida. La aplicación implementa las mejores prácticas del desarrollo moderno en Android, aplicando la arquitectura **MVVM (Model-View-ViewModel)** junto con las librerías de **Android Jetpack**.

---

## 🚀 Características Principales

- **Gestión Completa de Notas (CRUD)**: Creación de nuevas notas, edición de títulos/contenidos existentes y eliminación mediante interacción directa.
- **Interfaz Moderna y Adaptable (Material Design 3)**:
  - Tema oscuro en paleta Azul Marino con acentos Naranja y Dorado.
  - Barra de herramientas superior (`MaterialToolbar`) integrada con el icono oficial de la aplicación y el título **QuickFox Notes**.
  - Botón flotante (`FloatingActionButton`) para acceso inmediato a la creación de notas.
  - Adaptación dinámica al teclado virtual (`adjustResize` y `ScrollView`) evitando que el contenido o el botón de guardado se oculten.
- **Pantalla de Bienvenida (Splash Screen)**: Integración nativa de la librería `androidx.core:core-splashscreen` con logotipo y tiempo de espera optimizado.
- **Persistencia Local Reactiva**: Base de datos SQLite local mediante **Room ORM**, asegurando que los datos perduren aun al cerrar o reiniciar la aplicación.

---

## 🛠️ Arquitectura y Tecnologías

La aplicación está construida en **Java 11** siguiendo el patrón de arquitectura **MVVM**, desacoplando la lógica de negocio de la interfaz de usuario:

```
┌────────────────────────────────────────────────────────┐
│                   VISTA (UI Layer)                     │
│  MainActivity / AgregarEditarNotaActivity / Adapter    │
└───────────────────────────┬────────────────────────────┘
                            │ Observa LiveData
┌───────────────────────────▼────────────────────────────┐
│                       VIEWMODEL                        │
│                     NotasViewModel                     │
└───────────────────────────┬────────────────────────────┘
                            │ Solicita datos
┌───────────────────────────▼────────────────────────────┐
│                       REPOSITORY                       │
│                    NotasRepository                     │
└───────────────────────────┬────────────────────────────┘
                            │ Ejecuta en hilo secundario (ExecutorService)
┌───────────────────────────▼────────────────────────────┐
│                    BASE DE DATOS                       │
│              AppDatabase & NotaDao (Room)              │
└────────────────────────────────────────────────────────┘
```

### 📚 Librerías e Integraciones Clave

| Librería / Componente | Descripción |
| :--- | :--- |
| **AndroidX Room ORM** (`2.6.1+`) | Mapeo objeto-relacional para SQLite. Garantiza transacciones seguras en segundo plano y consultas reactivas con `LiveData`. |
| **Jetpack ViewModel & LiveData** (`2.7.0+`) | Mantiene el estado de la interfaz de usuario ante cambios de configuración (como rotación de pantalla) y notifica cambios a la UI en tiempo real. |
| **AndroidX Core SplashScreen** (`1.2.0`) | Implementación estándar de la pantalla de inicio con soporte para animaciones e imagen de marca. |
| **Material Components for Android** (`1.11.0+`) | Componentes de interfaz de usuario de Material Design (Toolbars, FABs, Cards, Insets). |
| **RecyclerView & CardView** | Renderizado eficiente de listas dinámicas con tarjetas con bordes redondeados y elevación. |

---

## 📁 Estructura del Proyecto

```
QuickFoxNotes/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/itq/zorronotas/
│   │       │   ├── MainActivity.java             # Pantalla principal con la lista de notas
│   │       │   ├── AgregarEditarNotaActivity.java # Formulario para crear o editar una nota
│   │       │   ├── NotaEntity.java                # Tabla 'tabla_notas' en Room ORM
│   │       │   ├── NotaDao.java                   # Interfaz de consultas SQL (Insert, Update, Delete, Query)
│   │       │   ├── AppDatabase.java               # Singleton de la base de datos SQLite
│   │       │   ├── NotasRepository.java           # Gestor de llamadas a la base de datos en hilos secundarios
│   │       │   ├── NotasViewModel.java            # Proveedor de datos reactivos para la interfaz
│   │       │   └── NotaAdapter.java               # Adaptador para renderizar cada nota en el RecyclerView
│   │       ├── res/
│   │       │   ├── layout/                        # Layouts XML (activity_main, activity_agregar_editar_nota, item_nota)
│   │       │   ├── values/                        # Recursos de colores, cadenas (strings.xml) y temas
│   │       │   └── drawable / mipmap              # Iconos de la aplicación y recursos gráficos
│   │       └── AndroidManifest.xml                # Declaración de componentes, actividades y temas
│   └── build.gradle.kts                           # Configuración del módulo de la app y dependencias
├── gradle/                                        # Catálogo de versiones (libs.versions.toml) y wrapper
├── build.gradle.kts                               # Gradle a nivel de proyecto
└── README.md                                      # Documentación del proyecto
```

---

## ⚙️ Requisitos para Compilación

- **Android Studio**: Ladybug (2024.2.1+) o posterior.
- **JDK**: Java 11 o Java 17.
- **Android SDK**:
  - `compileSdk`: 35
  - `minSdk`: 24 (Compatible con Android 7.0 Nougat o superior)
  - `targetSdk`: 35

---

## 🛠️ Cómo Compilar y Ejecutar

### 1. Clonar o Abrir el Proyecto
Abre el directorio del proyecto en **Android Studio**:
```bash
git clone <URL_DEL_REPOSITORIO>
```
O selecciona **File > Open** y navega hasta la carpeta del proyecto.

### 2. Sincronizar Gradle
En Android Studio, ejecuta **File > Sync Project with Gradle Files**.

### 3. Compilar desde la Línea de Comandos
Puedes generar el APK de depuración usando el wrapper de Gradle:

- **En Windows (PowerShell / CMD)**:
  ```cmd
  .\gradlew.bat assembleDebug
  ```
- **En Linux / macOS**:
  ```bash
  ./gradlew assembleDebug
  ```

El archivo APK resultante se generará en:
`app/build/outputs/apk/debug/app-debug.apk`

### 4. Ejecutar Pruebas Unitarias
Para verificar el correcto funcionamiento de las pruebas del proyecto:
```bash
./gradlew test
```

---

## 🎨 Paleta de Colores y Estilos

Los recursos gráficos están centralizados en `res/values/colors.xml` y `res/values/strings.xml` para fácil localización y mantenimiento:
- **Fondo Principal (`bg_main`)**: `#001A33` (Azul Marino Profundo)
- **Fondo de Tarjetas (`card_bg_note`)**: `#002B55` (Azul Noche)
- **Acento Primario (`accent_orange`)**: `#FF6600` (Naranja Vibrante)
- **Texto de Títulos (`text_title_gold`)**: `#FFD700` (Dorado)
- **Texto de Fechas (`text_source_cyan`)**: `#00E5FF` (Cian)

---

## 📝 Licencia y Créditos

Desarrollado para la materia de desarrollo de aplicaciones móviles del Instituto Tecnológico de Querétaro (ITQ). Todos los derechos reservados.
