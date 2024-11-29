

# Blocknotas - README

## Descripción del Proyecto

**Blocknotas** es una aplicación de notas que permite a los usuarios crear, editar, eliminar y visualizar notas y tareas, agregar multimedia (fotos, videos, audios) a las notas, gestionar recordatorios y archivos, y recibir notificaciones programadas. La aplicación también maneja permisos en tiempo de ejecución y asegura una experiencia fluida en dispositivos con diferentes versiones de Android.

### Funcionalidades Clave

1. **CRUD Nota/Tarea**  
   Permite crear, leer, actualizar y eliminar notas y tareas. Cada nota puede tener título, contenido, y multimedia asociada.
   
2. **CRUD Multimedia**  
   Los usuarios pueden agregar fotos, videos y audios a las notas. Se incluye la opción de seleccionar archivos desde la galería del sistema o capturarlos directamente desde la cámara del dispositivo.

3. **CRUD Recordatorio**  
   La aplicación permite establecer recordatorios para las notas, los cuales son gestionados con el sistema de alarmas de Android. Los recordatorios se activan en momentos específicos y pueden generar notificaciones.

4. **Gestión de Archivos**  
   Los archivos multimedia (imágenes, videos, audios) se guardan en el almacenamiento interno del dispositivo y se vinculan a las notas.

5. **Informe Técnico Final**  
   El proyecto incluye un sistema para generar informes técnicos que resumen las notas, tareas, y multimedia asociada, ayudando a los usuarios a llevar un control más eficiente.

6. **Multimedia**  
   - **Fotos**: Los usuarios pueden capturar imágenes directamente desde la cámara o seleccionar fotos desde la galería del dispositivo.
   - **Audio**: La app permite grabar audios y asociarlos a las notas.
   - **Video**: Los usuarios pueden grabar videos directamente desde la cámara o seleccionar videos desde la galería.
   - **Recurso del Sistema (System)**: Acceso a la galería, cámara y otros recursos del sistema para gestionar archivos multimedia.
   
7. **Notificaciones y Reprogramación**  
   El sistema de alarmas de Android permite que los usuarios configuren recordatorios, los cuales generan notificaciones incluso cuando la app no está en primer plano.

8. **Permisos en Tiempo de Ejecución**  
   La aplicación gestiona permisos de acceso a la cámara, almacenamiento y micrófono de manera dinámica, solicitándolos cuando sea necesario.

---

## Estructura del Proyecto

El proyecto está estructurado en los siguientes módulos clave:

1. **Vista**: Utiliza Jetpack Compose para crear interfaces reactivas y flexibles.
2. **Modelo**: Define las entidades de la aplicación, incluyendo las notas y los multimedia.
3. **Repositorio**: Maneja el acceso a los datos de las notas, multimedia, y recordatorios.
4. **Base de Datos**: Implementación de un sistema de base de datos local utilizando Room para almacenar las notas, tareas y multimedia.
5. **Notificaciones**: Integración con el sistema de alarmas y notificaciones para la gestión de recordatorios.
6. **Permisos**: Manejo de permisos para acceder a las funcionalidades del sistema (cámara, micrófono, almacenamiento).

---

## Funcionalidades Detalladas

### CRUD Nota/Tarea

La funcionalidad básica de la aplicación es permitir a los usuarios crear, editar, eliminar y visualizar notas. Cada nota tiene los siguientes campos:

- **Título**: El nombre de la nota o tarea.
- **Contenido**: Una descripción o detalle de la tarea o nota.
- **Multimedia**: Fotos, videos, y audios asociados a la nota.

#### Ejemplo de código para agregar una nota:

```kotlin
fun agregarNota(nota: Nota) {
    viewModelScope.launch {
        repository.insertNota(nota)
    }
}
```

### CRUD Multimedia

El usuario puede agregar fotos, audios y videos a cada nota. Esto se puede hacer tanto desde la cámara como desde la galería del dispositivo.

- **Fotos**: Usamos la cámara del dispositivo para capturar una imagen o permitir al usuario seleccionar una imagen desde la galería.
- **Audio**: Permite grabar un archivo de audio que se asocia a la nota.
- **Videos**: Graba un video desde la cámara o selecciona uno desde la galería.

```kotlin
fun agregarMultimedia(uri: Uri, tipo: MultimediaTipo) {
    val multimediaItem = MultimediaItem(uri, tipo)
    nota.multimedia.add(multimediaItem)
}
```

### CRUD Recordatorio

Los usuarios pueden configurar recordatorios para sus notas. Estos recordatorios se gestionan usando el sistema de alarmas de Android.

```kotlin
fun programarRecordatorio(fecha: Long, intent: Intent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, fecha, pendingIntent)
}
```

### Gestión de Archivos

Los archivos multimedia se guardan en el almacenamiento interno o externo del dispositivo. Utilizamos métodos para guardar imágenes, videos y audios en el almacenamiento y asociarlos con las notas.

```kotlin
fun saveImageToDCIM(context: Context, bitmap: Bitmap): File {
    val file = File(context.getExternalFilesDir(null), "BlockNotas/IMG_${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return file
}
```

### Notificaciones y Reprogramación

Las notificaciones se configuran a través del `AlarmManager` de Android. Las alarmas exactas se utilizan para generar recordatorios a una hora exacta. En versiones recientes de Android, se solicita permiso explícito para programar alarmas exactas.

```kotlin
fun scheduleExactAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val timeInMillis = System.currentTimeMillis() + 5000
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    } else {
        requestExactAlarmPermission(context)
    }
}
```

### Permisos en Tiempo de Ejecución

La aplicación gestiona permisos en tiempo de ejecución para acceder a la cámara, el almacenamiento y el micrófono. A partir de Android 6.0 (API 23), se requieren permisos explícitos, los cuales se solicitan en tiempo de ejecución.

```kotlin
fun requestPermissions(context: Context) {
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    ActivityCompat.requestPermissions(context as Activity, permissions, REQUEST_CODE)
}
```

---

## Requisitos

- **Android Studio**: La aplicación está desarrollada usando Android Studio con soporte para Kotlin y Jetpack Compose.
- **Dependencias**:
  - Jetpack Compose
  - Room Database
  - AlarmManager para programación de alarmas
  - Librerías para manejo de multimedia (Imágenes, Audio, Video)

---

## Conclusión

Blocknotas es una aplicación eficiente para gestionar notas y tareas con soporte para multimedia y recordatorios. La integración de notificaciones y permisos en tiempo de ejecución garantiza una experiencia robusta y fluida en dispositivos Android de diferentes versiones. Además, la gestión de archivos y la posibilidad de trabajar con imágenes, audios y videos directamente desde el sistema hace de Blocknotas una herramienta completa para el día a día.

