package com.sigefiv.app.screens.mascota

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.mascotaDataStore by preferencesDataStore(
    name = "mascota_preferencias"
)

class MascotaPreferencias(
    private val context: Context
) {

    companion object {
        private val POSICION_X = floatPreferencesKey("posicion_x")
        private val POSICION_Y = floatPreferencesKey("posicion_y")

        private val MASCOTA_VISIBLE =
            booleanPreferencesKey("mascota_visible")
    }

    // ============================================================
    // POSICIÓN DE ZOE
    // ============================================================

    val posicion: Flow<Pair<Float, Float>> =
        context.mascotaDataStore.data.map { preferencias ->

            Pair(
                preferencias[POSICION_X] ?: 0f,
                preferencias[POSICION_Y] ?: 0f
            )
        }

    suspend fun guardarPosicion(
        x: Float,
        y: Float
    ) {
        context.mascotaDataStore.edit { preferencias ->

            preferencias[POSICION_X] = x
            preferencias[POSICION_Y] = y
        }
    }

    // ============================================================
    // VISIBILIDAD DE ZOE
    // ============================================================

    val visible: Flow<Boolean> =
        context.mascotaDataStore.data.map { preferencias ->

            preferencias[MASCOTA_VISIBLE] ?: true
        }

    suspend fun guardarVisible(
        visible: Boolean
    ) {
        context.mascotaDataStore.edit { preferencias ->

            preferencias[MASCOTA_VISIBLE] = visible
        }
    }
}