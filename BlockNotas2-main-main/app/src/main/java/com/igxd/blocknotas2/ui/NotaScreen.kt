package com.igxd.blocknotas2.ui
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.androidgamesdk.gametextinput.Settings
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime

import com.igxd.BlockNotas.data.models.Nota
import com.igxd.blocknotas2.ReminderBroadcastReceiver
import com.igxd.blocknotas2.data.models.Recordatorio
import com.igxd.blocknotas2.ui.viewmodel.NotaViewModel
import com.igxd.blocknotas2.viewmodel.ThemeViewModel
import com.igxd.blocknotas2.viewmodel.MultimediaItem


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotaScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val viewModel: NotaViewModel = viewModel()
    val notas by viewModel.notas.observeAsState(listOf())
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var notaParaEditar by remember { mutableStateOf<Nota?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var multimediaItems by remember { mutableStateOf<List<MultimediaItem>>(emptyList()) }
    var recordatorioActivado by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var showImageDialog by remember { mutableStateOf<File?>(null) }
    var showVideoDialog by remember { mutableStateOf<Uri?>(null) }

    val isDarkTheme = themeViewModel.isDarkTheme
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkOnPrimary else LightOnPrimary


    val context = LocalContext.current
    // Lanzadores de permisos para cámara, video, galería y audio
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val imageFile = saveImageToDCIM(context, bitmap)
            multimediaItems = multimediaItems + MultimediaItem.Image(imageFile)
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            val videoUri = result.data?.data
            if (videoUri != null) {
                multimediaItems = multimediaItems + MultimediaItem.Video(videoUri)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val imageFile = saveUriToFile(context, uri)
            multimediaItems = multimediaItems + MultimediaItem.Image(imageFile)
        }
    }
    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

    }

    // Función para configurar recordatorio
    fun configurarRecordatorio(nota: Nota, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Verificar si la aplicación tiene permiso para programar alarmas exactas (Android 12 o superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                // Si tiene permiso, configurar la alarma
                val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                    putExtra("titulo", nota.titulo)
                    putExtra("contenido", nota.contenido)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    nota.id.toInt(), // ID único por nota
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                // Si no tiene permiso, solicitarlo
                val intent = Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        } else {
            // Para versiones anteriores a Android 12, no es necesario solicitar el permiso
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("titulo", nota.titulo)
                putExtra("contenido", nota.contenido)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                nota.id.toInt(), // ID único por nota
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }


    // Lógica para mostrar diálogos de fecha y hora
    fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth
        )
        datePickerDialog.show()
    }

    fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedTime = LocalTime.of(hourOfDay, minute)
            },
            selectedTime.hour, selectedTime.minute, true
        )
        timePickerDialog.show()
    }

    // Mostrar diálogos de multimedia (imagen y video)
    if (showImageDialog != null) {
        AlertDialog(
            onDismissRequest = { showImageDialog = null },
            buttons = {},
            text = {
                val bitmap = BitmapFactory.decodeFile(showImageDialog!!.absolutePath)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Imagen Completa",
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    }

    if (showVideoDialog != null) {
        AlertDialog(
            onDismissRequest = { showVideoDialog = null },
            buttons = {},
            text = {
                AndroidView(
                    factory = {
                        VideoView(it).apply {
                            setVideoURI(showVideoDialog)
                            setOnPreparedListener { mediaPlayer ->
                                mediaPlayer.isLooping = true
                                start()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    }

    // Contenido de la pantalla
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Block de Notas  \uD83D\uDCDD",
                style = MaterialTheme.typography.h4.copy(color = textColor),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tema", style = MaterialTheme.typography.h6.copy(color = textColor))
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { themeViewModel.toggleTheme() },
                    colors = SwitchDefaults.colors(checkedThumbColor = LightOnPrimary)
                )
            }
            Text(text = if (isDarkTheme) "Modo Oscuro" else "Modo Claro", color = textColor)
        }

        item {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar nota") },
                textStyle = TextStyle(color = textColor),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                textStyle = TextStyle(color = textColor),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido") },
                textStyle = TextStyle(color = textColor),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }

        item {
            Column {
                Button(onClick = { cameraLauncher.launch() }) { Text("Tomar Foto") }
                Button(onClick = { videoLauncher.launch(Intent(MediaStore.ACTION_VIDEO_CAPTURE)) }) { Text("Grabar Video") }
                Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Seleccionar Imagen") }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = { audioLauncher.launch(Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)) }) {
                    Text("Grabar Audio")
                }
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Agregar Recordatorio", style = MaterialTheme.typography.h6.copy(color = textColor))
                Switch(
                    checked = recordatorioActivado,
                    onCheckedChange = { recordatorioActivado = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = LightOnPrimary)
                )
            }
            if (recordatorioActivado) {
                Column {
                    Button(onClick = { showDatePickerDialog() }) {
                        Text("Seleccionar Fecha: ${selectedDate.toString()}", color = textColor)
                    }
                    Button(onClick = { showTimePickerDialog() }) {
                        Text("Seleccionar Hora: ${selectedTime.toString()}", color = textColor)
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                        val nuevaNota = Nota(
                            id = System.currentTimeMillis(),
                            titulo = titulo,
                            contenido = contenido,
                            multimedia = multimediaItems,
                            recordatorioFecha = if (recordatorioActivado) selectedDate else null,
                            recordatorioHora = if (recordatorioActivado) selectedTime else null,
                            notaParaEditar = null
                        )

                        if (notaParaEditar == null) {
                            viewModel.agregarNota(nuevaNota)
                        } else {
                            viewModel.actualizarNota(
                                notaParaEditar!!.copy(
                                    titulo = titulo,
                                    contenido = contenido,
                                    multimedia = multimediaItems,
                                    recordatorioFecha = if (recordatorioActivado) selectedDate else null,
                                    recordatorioHora = if (recordatorioActivado) selectedTime else null
                                )
                            )
                        }

                        if (recordatorioActivado) {
                            val calendar = Calendar.getInstance().apply {
                                set(
                                    selectedDate.year,
                                    selectedDate.monthValue - 1,
                                    selectedDate.dayOfMonth,
                                    selectedTime.hour,
                                    selectedTime.minute,
                                    0
                                )
                            }
                            configurarRecordatorio(nuevaNota, calendar.timeInMillis)
                        }

                        notaParaEditar = null
                        titulo = ""
                        contenido = ""
                        multimediaItems = emptyList()
                    }
                }
            ) {
                Text(text = if (notaParaEditar == null) "Guardar Nota" else "Actualizar Nota")
            }
        }

        items(notas.filter { it.titulo.contains(searchQuery, ignoreCase = true) }) { nota ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(if (isDarkTheme) DarkBackground else LightBackground)
            ) {
                Text(text = nota.titulo, style = MaterialTheme.typography.h6.copy(color = textColor))
                Text(text = nota.contenido, style = MaterialTheme.typography.body1.copy(color = textColor))
                if (nota.recordatorioFecha != null && nota.recordatorioHora != null) {
                    Text(
                        text = "Recordatorio: ${nota.recordatorioFecha} a las ${nota.recordatorioHora}",
                        style = MaterialTheme.typography.body2.copy(color = textColor)
                    )
                }
                nota.multimedia.forEach { multimediaItem ->
                    when (multimediaItem) {
                        is MultimediaItem.Image -> {
                            val bitmap = BitmapFactory.decodeFile(multimediaItem.file.absolutePath)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Imagen",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable { showImageDialog = multimediaItem.file }
                            )
                        }
                        is MultimediaItem.Video -> {
                            AndroidView(
                                factory = {
                                    VideoView(it).apply {
                                        setVideoURI(multimediaItem.uri)
                                        setOnPreparedListener { mediaPlayer ->
                                            mediaPlayer.isLooping = true
                                            start()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable { showVideoDialog = multimediaItem.uri }
                            )
                        }
                        is MultimediaItem.Audio -> Text("Audio: ${multimediaItem.uri}")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        titulo = nota.titulo
                        contenido = nota.contenido
                        multimediaItems = nota.multimedia
                        notaParaEditar = nota
                    }) { Text("Editar") }
                    Button(onClick = { viewModel.eliminarNota(nota) }) { Text("Eliminar") }
                }
            }
        }
    }
}



fun saveImageToDCIM(context: Context, bitmap: Bitmap): File {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return file
}

fun saveUriToFile(context: Context, uri: Uri): File {
    val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.jpg")
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.flush()
    outputStream.close()
    return file
}


