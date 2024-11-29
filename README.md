Aquí tienes un reporte completo para el README que puedes agregar a tu proyecto, detallando cómo manejar las alarmas exactas y su integración en una aplicación para Android:

---

# **Blocknotas - Manejo de Alarmas Exactas**

## **Descripción**

Este proyecto es una aplicación de notas, en la cual los usuarios pueden crear, editar y gestionar notas, tareas y multimedia. Una de las funcionalidades que se ha integrado es la programación de alarmas exactas, que permite a la aplicación programar notificaciones para recordatorios de tareas de manera precisa. Las alarmas exactas son fundamentales cuando es necesario ejecutar acciones específicas a un momento exacto, incluso si el dispositivo está en modo de suspensión (Doze Mode).

## **Funcionalidad de Alarmas Exactas**

En Android 12 (API nivel 31) y versiones superiores, las aplicaciones requieren permisos especiales para poder programar alarmas exactas. Este proyecto incluye una implementación para gestionar las alarmas exactas, garantizando la compatibilidad con versiones anteriores y nuevas del sistema operativo.

### **Flujo de la Funcionalidad**
1. **Verificación de Permisos**:
   - La aplicación verifica si tiene permisos para programar alarmas exactas utilizando el método `canScheduleExactAlarms()` de `AlarmManager`.
   - Si la aplicación tiene el permiso, la alarma exacta se configura con el método `setExactAndAllowWhileIdle()`.
   - Si la aplicación no tiene el permiso, solicita al usuario que habilite el permiso desde la configuración del sistema.

2. **Programación de Alarmas**:
   - La alarma se configura para que se active en un momento específico (por ejemplo, 5 segundos después de la configuración).
   - La alarma está configurada para ejecutarse incluso si el dispositivo está en modo de suspensión (Doze Mode).

3. **Manejo de Excepciones**:
   - En caso de que la aplicación no tenga el permiso adecuado, se maneja la excepción `SecurityException` y se notifica al usuario.

### **Permiso Necesario**
Para programar alarmas exactas en Android 12 y versiones superiores, la aplicación necesita el siguiente permiso:
```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
```

Este permiso debe ser solicitado explícitamente en dispositivos con Android 12 o superior.

### **Código Implementado**

A continuación se muestra el código utilizado para manejar la programación de alarmas exactas en el proyecto:

```kotlin
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat

fun scheduleExactAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            setExactAlarm(context, alarmManager)
        } else {
            requestExactAlarmPermission(context)
        }
    } else {
        setExactAlarm(context, alarmManager)
    }
}

fun setExactAlarm(context: Context, alarmManager: AlarmManager) {
    try {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val timeInMillis = System.currentTimeMillis() + 5000 // 5 segundos para demostración

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        Toast.makeText(context, "Alarma programada exitosamente", Toast.LENGTH_SHORT).show()
    } catch (e: SecurityException) {
        Toast.makeText(context, "Error de permiso: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!context.packageManager.canRequestPackageInstalls()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "La aplicación no tiene permiso para programar alarmas exactas", Toast.LENGTH_SHORT).show()
        }
    }
}
```

### **Explicación del Código**

1. **`scheduleExactAlarm`**:
   - Verifica si el sistema operativo es Android 12 o superior. Si es así, verifica si la aplicación tiene permisos para programar alarmas exactas.
   - Si tiene permisos, se configura la alarma exacta. Si no, se solicita al usuario que habilite el permiso.
  
2. **`setExactAlarm`**:
   - Aquí se programa la alarma exacta utilizando `setExactAndAllowWhileIdle` de `AlarmManager`.
   - Si ocurre un error relacionado con los permisos, se captura la excepción `SecurityException` y se muestra un mensaje al usuario.

3. **`requestExactAlarmPermission`**:
   - Si la versión de Android es 12 o superior y la app no tiene permisos para alarmas exactas, esta función abre la pantalla de configuración donde el usuario puede habilitar el permiso.

4. **Manejo de Excepciones**:
   - La excepción `SecurityException` se captura si la app no tiene permisos para establecer alarmas exactas.

### **Compatibilidad con Dispositivos con Android 12+**

En Android 12 (API nivel 31) y versiones superiores, las aplicaciones deben solicitar permisos explícitos para programar alarmas exactas. El código anterior verifica si la app tiene estos permisos antes de intentar programar la alarma y maneja los casos en que los permisos no están disponibles.

### **BroadcastReceiver para Recordatorios**

Para manejar las alarmas, se utiliza un `BroadcastReceiver` que se activa cuando la alarma se dispara:

```kotlin
class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Aquí puedes manejar lo que debe hacer la app cuando suene la alarma.
        Toast.makeText(context, "¡Es hora del recordatorio!", Toast.LENGTH_SHORT).show()
    }
}
```

### **Conclusión**

Este sistema de alarmas exactas en la aplicación Blocknotas permite a los usuarios recibir recordatorios precisos en momentos específicos, independientemente de si el dispositivo está en modo de suspensión. Al utilizar el permiso `SCHEDULE_EXACT_ALARM` y gestionar correctamente los permisos y excepciones, la aplicación asegura una experiencia de usuario fluida y compatible con las versiones más recientes de Android.

