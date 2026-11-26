# 📝 QuickFox Notes - Aplicación de Notas Rápidas para Android

**QuickFox Notes** es una aplicación nativa para Android diseñada para la creación, edición, organización y almacenamiento local de notas personales de forma rápida, intuitiva y fluida. La aplicación implementa las mejores prácticas del desarrollo moderno en Android, aplicando la arquitectura **MVVM (Model-View-ViewModel)** junto con las librerías de **Android Jetpack**.

---

## 🚀 Características Principales

- **Gestión Completa de Notas (CRUD)**: Creación de notas, edición de títulos/contenidos y eliminación mediante diálogos de confirmación.
- **Sistema de Recordatorios y Notificaciones ⏰**:
  - Casilla de verificación (**Check-box**) en el formulario de la nota para activar un recordatorio personalizado.
  - Selector interactivo de fecha y hora (`DatePickerDialog` y `TimePickerDialog`) con validación de fechas futuras.
  - Alertas push automáticas mediante `AlarmManager` y `BroadcastReceiver` cuando se alcanza la fecha y hora del recordatorio.
  - Al reabrir la nota (ya sea desde la notificación o desde la lista), la fecha y hora del recordatorio se restablecen automáticamente a `null` para evitar notificaciones repetidas.
- **Interfaz Moderna y Adaptable (Material Design 3)**:
  - Tema oscuro en paleta Azul Marino con acentos Naranja, Dorado y Cian.
  - Barra de herramientas superior (`MaterialToolbar`) integrada con el icono de la aplicación y el título **QuickFox Notes**.
  - Botón flotante (`FloatingActionButton`) para acceso inmediato a la creación de notas.
  - Adaptación dinámica al teclado virtual (`adjustResize` y `ScrollView`).
- **Pantalla de Bienvenida (Splash Screen)**: Integración nativa de la librería `androidx.core:core-splashscreen`.
- **Persistencia Local Reactiva**: Base de datos SQLite local mediante **Room ORM** (versión 2).

---

## 🛠️ Arquitectura y Tecnologías

La aplicación está construida en **Java 11** siguiendo el patrón de arquitectura **MVVM**, desacoplando la lógica de negocio de la interfaz de usuario:

```
┌────────────────────────────────────────────────────────┐
│                   VISTA (UI Layer)                     │
│  MainActivity / AgregarEditarNotaActivity / Adapter    │
└───────────────────────────┬────────────────────────────┘
            │ Observa LiveData          │ Inicia Recordatorio
┌───────────▼───────────────▼────────────┬───────────────┐
│                       VIEWMODEL        │  AlarmManager │
│                     NotasViewModel     │  & Receiver   │
└───────────────────────────┬────────────┴───────────────┘
                            │ Solicita datos
┌───────────────────────────▼────────────────────────────┐
│                       REPOSITORY                       │
│                    NotasRepository                     │
└───────────────────────────┬────────────────────────────┘
                            │ Ejecuta en hilo secundario (ExecutorService)
┌───────────────────────────▼────────────────────────────┐
│                    BASE DE DATOS                       │
│              AppDatabase & NotaDao (Room)              │
└────────────────────────────────------------------------┘
```

### 📚 Librerías e Integraciones Clave

| Librería / Componente | Descripción |
| :--- | :--- |
| **AndroidX Room ORM** (`2.6.1+`) | Mapeo objeto-relacional para SQLite (Versión 2). Gestiona la persistencia de notas y marcas de tiempo de recordatorios. |
| **Jetpack ViewModel & LiveData** (`2.7.0+`) | Conserva el estado de la UI y notifica cambios a la interfaz en tiempo real de forma reactiva. |
| **AlarmManager & Notifications** | Programación de alarmas exactas (`setExactAndAllowWhileIdle`) y emisión de notificaciones nativas de alta prioridad. |
| **AndroidX Core SplashScreen** (`1.2.0`) | Pantalla de inicio estándar con soporte para branding. |
| **Material Components for Android** (`1.11.0+`) | Componentes de interfaz de usuario de Material Design. |

---

## 🔔 ¿Cómo Funcionan los Recordatorios?

El sistema de recordatorios de **QuickFox Notes** opera mediante una integración precisa entre **Room**, **AlarmManager**, **BroadcastReceiver** y el sistema de notificaciones de Android:

1. **Creación / Edición con Recordatorio**:
   - Al activar el checkbox **"Establecer recordatorio"** en `AgregarEditarNotaActivity`, se despliegan secuencialmente un `DatePickerDialog` y un `TimePickerDialog`.
   - La fecha y hora seleccionadas se convierten a un timestamp en milisegundos (`Long fechaRecordatorio`) que se almacena en la base de datos Room.

2. **Programación en Segundo Plano**:
   - En `NotasRepository`, al insertar o actualizar una nota con un recordatorio válido en el futuro, se invoca a `ReminderScheduler.programarRecordatorio()`.
   - `ReminderScheduler` utiliza `AlarmManager.setExactAndAllowWhileIdle()` para programar una alarma exacta vinculada al ID único de la nota.

3. **Disparador y Notificación (`ReminderReceiver`)**:
   - Cuando llega la fecha y hora programadas, `AlarmManager` activa el `BroadcastReceiver` (`ReminderReceiver`).
   - Este componente crea un canal de notificaciones de alta importancia (`NotificationChannel`) y emite una notificación con el título y contenido de la nota.
   - La notificación incluye un `PendingIntent` configurado para abrir la actividad principal (`MainActivity`) y cargar directamente la nota asociada.

4. **Reinicio Automático (Anti-repetición)**:
   - Tan pronto como el usuario abre la nota (ya sea pulsando la notificación o seleccionándola desde la lista de la aplicación), el sistema comprueba si el recordatorio ha expirado o proviene de una notificación.
   - `fechaRecordatorio` se restablece automáticamente a `null`, cancelando cualquier alarma pendiente y asegurando que la notificación no vuelva a mostrarse.

---

## 🗄️ Explicación de la Base de Datos Room

La aplicación utiliza **Room ORM** como abstracción sobre SQLite para garantizar un acceso a datos robusto, seguro y libre de bloqueos en el hilo principal:

1. **Entidad (`NotaEntity.java`)**:
   - Representa la tabla `tabla_notas` en SQLite.
   - Contiene los campos: `id` (Primary Key auto-generada), `titulo` (String), `contenido` (String), `fechaCreacion` (String) y `fechaRecordatorio` (`Long`, almacena el timestamp del recordatorio en milisegundos o `null` si no está activo).

2. **DAO (`NotaDao.java`)**:
   - Interfaz con consultas SQL anotadas para operaciones CRUD:
     - `long insertar(NotaEntity nota)`: Inserta una nota y retorna el ID autogenerado.
     - `void actualizar(NotaEntity nota)`: Actualiza una nota existente.
     - `void eliminar(NotaEntity nota)`: Elimina una nota.
     - `LiveData<List<NotaEntity>> getTodasLasNotas()`: Consulta reactiva que emite una lista ordenada por ID descendente cada vez que los datos cambian.

3. **Base de Datos Singleton (`AppDatabase.java`)**:
   - Patrón Singleton con doble comprobación de sincronización (`synchronized`) para evitar la apertura múltiple de conexiones SQLite.
   - Configurada con versión `2` y `.fallbackToDestructiveMigration()` para simplificar actualizaciones de esquema.

4. **Repositorio (`NotasRepository.java`)**:
   - Desacopla el ViewModel de la fuente de datos.
   - Ejecuta todas las operaciones de escritura/actualización/eliminación en un hilo secundario mediante `ExecutorService` (`Executors.newSingleThreadExecutor()`).
   - Gestiona automáticamente la programación y cancelación de alarmas en `AlarmManager` al insertar, actualizar o eliminar notas.

---

## 📁 Estructura del Proyecto

```
QuickFoxNotes/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/itq/zorronotas/
│   │       │   ├── MainActivity.java             # Lista de notas, gestión de permisos e intents de notificación
│   │       │   ├── AgregarEditarNotaActivity.java # Formulario de notas, checkbox y diálogos Date/Time Picker
│   │       │   ├── NotaEntity.java                # Entidad Room con soporte para recordatorios (fechaRecordatorio)
│   │       │   ├── NotaDao.java                   # DAO con consultas SQL reactivas (LiveData)
│   │       │   ├── AppDatabase.java               # Singleton de Room ORM (Versión 2)
│   │       │   ├── NotasRepository.java           # Repositorio CRUD con hilos secundarios y AlarmManager
│   │       │   ├── NotasViewModel.java            # ViewModel MVVM para retención de estado
│   │       │   ├── NotaAdapter.java               # Adaptador RecyclerView con indicador visual de recordatorio (⏰)
│   │       │   ├── ReminderReceiver.java          # BroadcastReceiver para recepción de alarmas y notificaciones
│   │       │   └── ReminderScheduler.java         # Gestor de AlarmManager para alarmas exactas
│   │       ├── res/
│   │       │   ├── layout/                        # Layouts XML (activity_main, activity_agregar_editar_nota, item_nota)
│   │       │   ├── values/                        # Recursos de colores, cadenas y temas
│   │       │   └── drawable / mipmap              # Iconos y recursos gráficos
│   │       └── AndroidManifest.xml                # Permisos (POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM) y componentes
│   └── build.gradle.kts                           # Configuración del módulo de la app y dependencias
├── gradle/                                        # Catálogo de versiones y wrapper
├── build.gradle.kts                               # Gradle a nivel de proyecto
└── README.md                                      # Documentación del proyecto
