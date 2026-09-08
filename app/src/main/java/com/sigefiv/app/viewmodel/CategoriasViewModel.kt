package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Categoria
import com.sigefiv.app.data.repository.CategoriasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriasViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        CategoriasRepository(
            ApiClient.create(context)
        )

    private val _categorias =
        MutableStateFlow<List<Categoria>>(emptyList())

    val categorias: StateFlow<List<Categoria>> =
        _categorias

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje


    fun cargarCategorias() {

        viewModelScope.launch {

            _cargando.value = true

            _mensaje.value = null

            try {

                val respuesta =
                    repository.obtenerCategorias()

                if (respuesta.success) {

                    _categorias.value =
                        respuesta.categorias

                } else {

                    _mensaje.value =
                        "No se pudieron cargar las categorías."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudieron cargar las categorías."

            } finally {

                _cargando.value = false
            }
        }
    }


    fun limpiarMensaje() {

        _mensaje.value = null
    }
}