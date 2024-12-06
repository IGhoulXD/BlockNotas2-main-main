package com.igxd.blocknotas2.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.igxd.blocknotas2.data.models.Recordatorio
import com.igxd.blocknotas2.repository.RecordatorioRepository
import com.igxd.blocknotas2.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun RecordatorioScreen(recordatorioRepository: RecordatorioRepository, navController: NavController
                       , themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var recordatorios by remember { mutableStateOf(listOf<Recordatorio>()) }
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var fechaHora by remember { mutableStateOf<Long?>(null) }
    var editingRecordatorio by remember { mutableStateOf<Recordatorio?>(null) }
    val calendar = Calendar.getInstance()

    // Obtener todos los recordatorios desde la base de datos
    LaunchedEffect(Unit) {
        recordatorios = recordatorioRepository.obtenerTodos()
    }

    // Función para abrir el TimePicker
    fun showTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                fechaHora = calendar.timeInMillis
            },
            hour, minute, true
        ).show()
    }

    // Función para abrir el DatePicker
    fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                showTimePicker()  // Llamar a TimePicker después de seleccionar la fecha
            },
            year, month, day
        ).show()
    }

    // Interfaz de usuario para mostrar los recordatorios
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(recordatorios) { recordatorio ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Título: ${recordatorio.titulo}", style = MaterialTheme.typography.h6)
                Text("Contenido: ${recordatorio.contenido}", style = MaterialTheme.typography.body1)
                Text("Fecha y Hora: ${Date(recordatorio.fechaHora)}", style = MaterialTheme.typography.body2)

                // Botón de editar
                Button(onClick = {
                    editingRecordatorio = recordatorio
                    titulo = recordatorio.titulo
                    contenido = recordatorio.contenido
                    fechaHora = recordatorio.fechaHora
                }) {
                    Text("Editar")
                }

                // Botón de eliminar
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        recordatorioRepository.eliminar(recordatorio)
                        recordatorios = recordatorioRepository.obtenerTodos()
                    }
                }) {
                    Text("Eliminar")
                }
            }
        }
    }

    // Formulario de agregar/editar recordatorio
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título del recordatorio") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = contenido,
            onValueChange = { contenido = it },
            label = { Text("Contenido del recordatorio") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar la fecha y hora seleccionadas
        Text("Fecha y Hora: ${fechaHora?.let { Date(it) }}")

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para seleccionar fecha y hora
        Button(onClick = {
            showDatePicker()
        }) {
            Text("Seleccionar Fecha y Hora")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val recordatorio = Recordatorio(
                id = editingRecordatorio?.id ?: 0,
                titulo = titulo,
                contenido = contenido,
                fechaHora = fechaHora ?: System.currentTimeMillis(),
                mensaje = ""
            )
            coroutineScope.launch {
                if (editingRecordatorio == null) {
                    recordatorioRepository.insertar(recordatorio)
                } else {
                    recordatorioRepository.actualizar(recordatorio)
                }
                recordatorios = recordatorioRepository.obtenerTodos()
            }
            titulo = ""
            contenido = ""
            fechaHora = null
            editingRecordatorio = null
        }) {
            Text(if (editingRecordatorio == null) "Agregar Recordatorio" else "Actualizar Recordatorio")
        }
    }
}
