
package com.sigefiv.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "sigefiv_session"
)

class SessionManager(
    private val context: Context
) {

    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val TOKEN = stringPreferencesKey("token")
        private val EMAIL = stringPreferencesKey("email")
        private val NOMBRE = stringPreferencesKey("nombre")
        private val SEUDONIMO = stringPreferencesKey("seudonimo")
        private val ROL = stringPreferencesKey("rol")
        private val PERMISOS = stringSetPreferencesKey("permisos")
        private val BIENVENIDA_VISTA =
            booleanPreferencesKey("bienvenida_vista")
    }

    val userId: Flow<Int?> =
        context.dataStore.data.map { it[USER_ID] }

    val token: Flow<String?> =
        context.dataStore.data.map { it[TOKEN] }

    val email: Flow<String?> =
        context.dataStore.data.map { it[EMAIL] }

    val nombre: Flow<String?> =
        context.dataStore.data.map { it[NOMBRE] }

    val seudonimo: Flow<String?> =
        context.dataStore.data.map { it[SEUDONIMO] }

    val rol: Flow<String?> =
        context.dataStore.data.map { it[ROL] }

    val permisos: Flow<Set<String>> =
        context.dataStore.data.map { it[PERMISOS] ?: emptySet() }

    val bienvenidaVista: Flow<Boolean> =
        context.dataStore.data.map { it[BIENVENIDA_VISTA] ?: false }

    suspend fun guardarSesion(
        userId: Int,
        token: String,
        email: String,
        nombre: String,
        seudonimo: String? = null,
        rol: String?,
        bienvenidaVista: Boolean = false,
        permisos: List<String> = emptyList()
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[TOKEN] = token
            preferences[EMAIL] = email
            preferences[NOMBRE] = nombre

            if (seudonimo.isNullOrBlank()) {
                preferences.remove(SEUDONIMO)
            } else {
                preferences[SEUDONIMO] = seudonimo
            }

            preferences[ROL] = rol ?: ""
            preferences[PERMISOS] = permisos.toSet()
            preferences[BIENVENIDA_VISTA] = bienvenidaVista
        }
    }

    suspend fun actualizarPermisos(permisos: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PERMISOS] = permisos.toSet()
        }
    }

    suspend fun actualizarSeudonimo(seudonimo: String?) {
        context.dataStore.edit { preferences ->
            if (seudonimo.isNullOrBlank()) {
                preferences.remove(SEUDONIMO)
            } else {
                preferences[SEUDONIMO] = seudonimo
            }
        }
    }

    suspend fun marcarBienvenidaVistaLocal() {
        context.dataStore.edit { preferences ->
            preferences[BIENVENIDA_VISTA] = true
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(TOKEN)
            preferences.remove(EMAIL)
            preferences.remove(NOMBRE)
            preferences.remove(SEUDONIMO)
            preferences.remove(ROL)
            preferences.remove(PERMISOS)
            preferences.remove(BIENVENIDA_VISTA)
        }
    }
}
