package com.igxd.BlockNotas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igxd.BlockNotas.data.models.Nota
import com.igxd.blocknotas2.viewmodel.MultimediaItem

class NotaViewModel : ViewModel() {
    private val _notas = MutableLiveData<List<Nota>>()
    val notas: LiveData<List<Nota>> get() = _notas

    // Método para agregar multimedia a una nota
    fun agregarMultimedia(nota: Nota, multimediaItems: List<MultimediaItem>) {
        val updatedNota = nota.copy(multimedia = multimediaItems)
        // Actualiza la nota en el repositorio y vuelve a cargar la lista de notas
        actualizarNota(updatedNota)
    }

    fun agregarNota(nota: Nota) {
        val currentNotas = _notas.value ?: emptyList()
        _notas.value = currentNotas + nota
    }

    fun actualizarNota(nota: Nota) {
        val currentNotas = _notas.value?.map { if (it.id == nota.id) nota else it } ?: emptyList()
        _notas.value = currentNotas
    }

    fun eliminarNota(nota: Nota) {
        _notas.value = _notas.value?.filter { it.id != nota.id }
    }
}
