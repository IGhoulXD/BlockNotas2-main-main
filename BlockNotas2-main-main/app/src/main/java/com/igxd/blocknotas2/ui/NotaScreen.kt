import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE_SECURE
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igxd.blocknotas2.viewmodel.ThemeViewModel
import com.igxd.BlockNotas.data.models.Nota
import androidx.navigation.NavController
import com.igxd.BlockNotas.viewmodel.NotaViewModel
import com.igxd.blocknotas2.ReminderBroadcastReceiver
import com.igxd.blocknotas2.ui.*
import com.igxd.blocknotas2.viewmodel.MultimediaItem
import java.io.File
import java.io.FileOutputStream

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

    val isDarkTheme = themeViewModel.isDarkTheme
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkOnPrimary else LightOnPrimary

    val context = LocalContext.current

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

    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Implementa la lógica para grabar audio y almacenar la URI
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val imageFile = saveUriToFile(context, uri)
            multimediaItems = multimediaItems + MultimediaItem.Image(imageFile)
        }
    }

    // Lógica para Recordatorio
    @SuppressLint("ScheduleExactAlarm")
    fun setRecordatorio() {
        if (recordatorioActivado) {
            // Aquí se puede usar un calendario o timepicker para elegir el momento
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
            val timeInMillis = System.currentTimeMillis() + 5000 // 5 segundos de retraso como ejemplo
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }

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
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar nota") },
                textStyle = TextStyle(color = textColor),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        item {
            Column {
                Button(onClick = { cameraLauncher.launch() }) {
                    Text("Tomar Foto")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = { videoLauncher.launch(Intent(MediaStore.ACTION_VIDEO_CAPTURE)) }) {
                    Text("Grabar Video")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = { audioLauncher.launch(Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)) }) {
                    Text("Grabar Audio")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Seleccionar Imagen")
                }
            }
        }

        item {
            // Sección de recordatorio
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Agregar Recordatorio", style = MaterialTheme.typography.h6.copy(color = textColor))
                Switch(
                    checked = recordatorioActivado,
                    onCheckedChange = { recordatorioActivado = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = LightOnPrimary)
                )
            }
            if (recordatorioActivado) {
                setRecordatorio()
            }
        }

        item {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                        if (notaParaEditar == null) {
                            viewModel.agregarNota(
                                Nota(
                                    titulo = titulo,
                                    contenido = contenido,
                                    multimedia = multimediaItems
                                )
                            )
                        } else {
                            viewModel.actualizarNota(
                                notaParaEditar!!.copy(
                                    titulo = titulo,
                                    contenido = contenido,
                                    multimedia = multimediaItems
                                )
                            )
                            notaParaEditar = null
                        }
                        titulo = ""
                        contenido = ""
                        multimediaItems = emptyList()
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    if (notaParaEditar == null) "Agregar Nota" else "Actualizar Nota",
                    color = textColor
                )
            }
        }

        items(notas.filter {
            it.titulo.contains(searchQuery, ignoreCase = true) ||
                    it.contenido.contains(searchQuery, ignoreCase = true)
        }) { nota ->
            Card(
                backgroundColor = if (isDarkTheme) DarkBackground else LightBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = nota.titulo,
                        style = MaterialTheme.typography.h6.copy(color = textColor)
                    )
                    Text(
                        text = nota.contenido,
                        style = MaterialTheme.typography.body1.copy(color = textColor)
                    )
                    nota.multimedia.forEach {
                        when (it) {
                            is MultimediaItem.Image -> {
                                val bitmap = BitmapFactory.decodeFile(it.file.absolutePath)
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Imagen"
                                )
                            }

                            is MultimediaItem.Video -> {
                                Text("Video: ${it.uri}")
                            }

                            is MultimediaItem.Audio -> {
                                Text("Audio: ${it.uri}")
                            }
                        }
                    }
                    Row {
                        Button(
                            onClick = {
                                titulo = nota.titulo
                                contenido = nota.contenido
                                notaParaEditar = nota
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Editar", color = textColor)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.eliminarNota(nota) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Eliminar", color = textColor)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("multimedia") }) {
                Text("Ir a Multimedia", color = textColor)
            }
        }
    }
}

// Función para guardar imágenes
fun saveImageToDCIM(context: Context, bitmap: Bitmap): File {
    val storageDir = File(context.getExternalFilesDir(null), "BlockNotas")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    val file = File(storageDir, "IMG_${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    return file
}

// Función para guardar URI de imágenes
fun saveUriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val storageDir = File(context.getExternalFilesDir(null), "BlockNotas")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    val file = File(storageDir, "IMG_${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()

    return file
}
