package com.igxd.blocknotas2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igxd.BlockNotas.data.models.Nota
import com.igxd.blocknotas2.viewmodel.MultimediaItem

class NotaViewModel : ViewModel() {
    private val _notas = MutableLiveData<List<Nota>>().apply { value = emptyList() }
    val notas: LiveData<List<Nota>> get() = _notas

    fun agregarNota(nota: Nota) {
        Log.d("NotaViewModel", "Agregando nueva nota: $nota")  // Agregar un log para verificar
        val currentNotas = _notas.value ?: emptyList()
        val nuevaNota =
            nota.copy(id = System.currentTimeMillis()) // Genera un id único (puedes usar cualquier lógica aquí)
        _notas.value = currentNotas + nuevaNota
    }


    fun actualizarNota(nota: Nota) {
        val currentNotas = _notas.value?.map {
            if (it.id == nota.id) nota else it
        } ?: emptyList()
        _notas.value = currentNotas
    }


    // Eliminar una nota
    fun eliminarNota(nota: Nota) {
        _notas.value = _notas.value?.filter { it.id != nota.id }
    }

    // Función para eliminar multimedia de una nota
    fun eliminarMultimediaDeNota(nota: Nota, multimediaItem: MultimediaItem) {
        val updatedMultimedia = nota.multimedia.filterNot { it == multimediaItem }
        val updatedNota = nota.copy(multimedia = updatedMultimedia)
        actualizarNota(updatedNota)
    }

    fun editarNota(nota: Nota) {
    }
}