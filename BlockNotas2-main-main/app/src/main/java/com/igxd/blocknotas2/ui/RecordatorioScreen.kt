package com.igxd.blocknotas2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.app.AlarmManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igxd.blocknotas2.ReminderBroadcastReceiver
import com.igxd.blocknotas2.data.models.Recordatorio
import com.igxd.blocknotas2.repository.RecordatorioRepository
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun RecordatorioScreen(recordatorioRepository: RecordatorioRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Usamos un CoroutineScope para operaciones suspendidas
    var recordatorios by remember { mutableStateOf(listOf<Recordatorio>()) }
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var fechaHora by remember { mutableStateOf<Long?>(null) }
    var editingRecordatorio by remember { mutableStateOf<Recordatorio?>(null) }

    
    // Obtener todos los recordatorios desde la base de datos
    LaunchedEffect(Unit) {
        recordatorios = recordatorioRepository.obtenerTodos()
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

// Botón de Editar
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
                        // Llamamos a eliminar dentro de un coroutine
                        recordatorioRepository.eliminar(recordatorio)
                        recordatorios = recordatorioRepository.obtenerTodos()
                    }
                }) {
                    Text("Eliminar")
                }
            }
        }
    }
