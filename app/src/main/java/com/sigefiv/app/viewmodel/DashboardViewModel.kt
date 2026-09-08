package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.PeriodoDashboard
import com.sigefiv.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        AuthRepository(
            ApiClient.create(context)
        )

    private val _periodo =
        MutableStateFlow<PeriodoDashboard?>(null)

    val periodo: StateFlow<PeriodoDashboard?> =
        _periodo

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje


    fun cargarDashboard() {

        viewModelScope.launch {

            _cargando.value = true

            _mensaje.value = null

            try {

                val respuesta =
                    repository.obtenerDashboard()

                if (respuesta.success) {

                    _periodo.value =
                        respuesta.periodo

                } else {

                    _mensaje.value =
                        "No se pudo cargar el Dashboard."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    "No se pudo cargar la información financiera."

            } finally {

                _cargando.value = false
            }
        }
    }
}