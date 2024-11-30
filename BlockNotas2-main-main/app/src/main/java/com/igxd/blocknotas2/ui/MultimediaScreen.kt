package com.igxd.blocknotas2.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igxd.blocknotas2.viewmodel.MultimediaItem
import com.igxd.blocknotas2.viewmodel.MultimediaViewModel
import com.igxd.blocknotas2.viewmodel.ThemeViewModel
import saveImageToDCIM
import java.io.File
import java.io.FileOutputStream


val LightBackground = Color(0xFFF5F5F5)
val DarkBackground = Color(0xFF121212)
val LightOnPrimary = Color.Black
val DarkOnPrimary = Color.White

@Composable
fun MultimediaScreen(navController: NavController, themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val isDarkTheme = themeViewModel.isDarkTheme
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkOnPrimary else LightOnPrimary

    val multimediaViewModel: MultimediaViewModel = viewModel()

    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioPath by remember { mutableStateOf<String?>(null) }

    // Lanzadores para capturar fotos y videos
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val imageFile = saveImageToDCIM(context, bitmap)
            multimediaViewModel.multimediaItems.add(MultimediaItem.Image(imageFile)) // Guardar la ruta
            Toast.makeText(context, "Foto tomada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            val videoUri = result.data?.data
            if (videoUri != null) {
                multimediaViewModel.multimediaItems.add(MultimediaItem.Video(videoUri))
                Toast.makeText(context, "Video grabado con éxito", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No se pudo grabar el video", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para permisos
    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[android.Manifest.permission.CAMERA] ?: false
        val audioPermissionGranted = permissions[android.Manifest.permission.RECORD_AUDIO] ?: false
        val storagePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false

        if (cameraPermissionGranted && audioPermissionGranted && storagePermissionGranted) {
            Toast.makeText(context, "Permisos concedidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Faltan permisos necesarios", Toast.LENGTH_SHORT).show()
        }
    }

    // Verificar permisos al iniciar la pantalla
    LaunchedEffect(true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            // Solicitar permisos
            requestPermissionsLauncher.launch(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    // BoxWithConstraints para manejar el tamaño de pantalla dinámico
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isWideScreen = screenWidth > 600.dp // Detectar pantallas más grandes

        // Usar LazyColumn para permitir desplazamiento en toda la pantalla, incluyendo botones
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Pantalla de Multimedia", style = MaterialTheme.typography.h6.copy(color = textColor))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Botón para regresar a la pantalla de notas
                Button(onClick = { navController.popBackStack() }) {
                    Text("Regresar a Notas")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Botón para tomar fotos
                Button(
                    onClick = { cameraLauncher.launch() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWideScreen) 32.dp else 16.dp) // Adaptar al tamaño de pantalla
                ) {
                    Icon(imageVector = Icons.Filled.Camera, contentDescription = "Tomar Foto")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar Foto")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Botón para grabar videos
                Button(
                    onClick = {
                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        videoLauncher.launch(intent) // Lanzar el intent correctamente
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWideScreen) 32.dp else 16.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Videocam, contentDescription = "Grabar Video")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Grabar Video")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Botón para grabar audio
                Button(
                    onClick = {
                        if (isRecording) {
                            recorder?.apply {
                                stop()
                                release()
                            }
                            recorder = null
                            isRecording = false
                            audioPath?.let {
                                multimediaViewModel.multimediaItems.add(MultimediaItem.Audio(it))
                            }
                            Toast.makeText(context, "Grabación de audio detenida", Toast.LENGTH_SHORT).show()
                        } else {
                            val fileName = "${context.externalCacheDir?.absolutePath}/audio_${System.currentTimeMillis()}.3gp"
                            audioPath = fileName
                            recorder = MediaRecorder().apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                setOutputFile(fileName)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                prepare()
                                start()
                            }
                            isRecording = true
                            Toast.makeText(context, "Grabando audio...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWideScreen) 32.dp else 16.dp)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.MicOff else Icons.Filled.Mic,
                        contentDescription = if (isRecording) "Detener grabación" else "Iniciar grabación"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRecording) "Detener Audio" else "Grabar Audio")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Lista de elementos multimedia
                Text("Elementos guardados", style = MaterialTheme.typography.h6.copy(color = textColor))
            }

            // Aquí usamos LazyColumn que ya es desplazable por naturaleza
            items(multimediaViewModel.multimediaItems) { item ->
                MultimediaItemView(item)
            }
        }
    }
}

// Función para guardar la imagen


@Composable
fun MultimediaItemView(item: MultimediaItem) {
    when (item) {
        is MultimediaItem.Image -> {
            // Usamos 'file' para las imágenes
            val imageBitmap = BitmapFactory.decodeFile(item.file.absolutePath).asImageBitmap()
            Image(bitmap = imageBitmap, contentDescription = "Imagen multimedia", modifier = Modifier.fillMaxWidth())
            // Aquí puedes añadir un botón para eliminar o manipular la imagen
        }
        is MultimediaItem.Video -> {
            // Usamos 'uri' para los videos
            Text(text = "Video guardado en: ${item.uri}")
            // Aquí podrías implementar un reproductor de video o algo relacionado
        }
        is MultimediaItem.Audio -> {
            // Usamos 'uri' para los audios
            val context = LocalContext.current
            val mediaPlayer = remember { MediaPlayer() }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Audio guardado en: ${item.uri}")
                IconButton(onClick = {
                    mediaPlayer.setDataSource(item.uri) // Reproducimos el audio usando la 'uri'
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    Toast.makeText(context, "Reproduciendo audio", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(imageVector = Icons.Filled.Mic, contentDescription = "Reproducir audio")
                }
            }
        }
    }
}
