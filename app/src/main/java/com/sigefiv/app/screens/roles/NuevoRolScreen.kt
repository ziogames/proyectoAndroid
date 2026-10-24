
package com.sigefiv.app.screens.roles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sigefiv.app.data.model.Permiso

@Composable
fun NuevoRolScreen(
    permisosAgrupados: Map<String, List<Permiso>>,
    cargandoPermisos: Boolean,
    guardando: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    onReintentar: () -> Unit,
    onCrearRol: (String, List<String>) -> Unit
) {
    var nombreRol by remember { mutableStateOf("") }

    val permisosSeleccionados = remember {
        mutableStateListOf<String>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Nuevo Rol",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Define el nombre y los permisos que tendrá este rol.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombreRol,
            onValueChange = { nombreRol = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre del rol") },
            singleLine = true,
            enabled = !guardando
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Permisos",
                style = MaterialTheme.typography.titleMedium
            )

            TextButton(
                enabled = !guardando && permisosAgrupados.isNotEmpty(),
                onClick = {
                    val todos = permisosAgrupados.values
                        .flatten()
                        .map { it.name }

                    if (permisosSeleccionados.containsAll(todos)) {
                        permisosSeleccionados.clear()
                    } else {
                        permisosSeleccionados.clear()
                        permisosSeleccionados.addAll(todos)
                    }
                }
            ) {
                Text(
                    if (
                        permisosAgrupados.values.flatten().isNotEmpty() &&
                        permisosSeleccionados.containsAll(
                            permisosAgrupados.values.flatten().map { it.name }
                        )
                    ) {
                        "Desmarcar todos"
                    } else {
                        "Seleccionar todos"
                    }
                )
            }
        }

        when {
            cargandoPermisos -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cargando permisos...")
                }
            }

            permisosAgrupados.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No se pudieron cargar los permisos.")

                    TextButton(onClick = onReintentar) {
                        Text("Reintentar")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(permisosAgrupados.keys.toList()) { modulo ->
                        val permisos = permisosAgrupados[modulo].orEmpty()
                        val nombres = permisos.map { it.name }
                        val todosMarcados = nombres.isNotEmpty() &&
                                nombres.all { it in permisosSeleccionados }

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = todosMarcados,
                                        enabled = !guardando,
                                        onCheckedChange = { marcado ->
                                            if (marcado) {
                                                nombres.forEach { nombre ->
                                                    if (nombre !in permisosSeleccionados) {
                                                        permisosSeleccionados.add(nombre)
                                                    }
                                                }
                                            } else {
                                                permisosSeleccionados.removeAll(nombres)
                                            }
                                        }
                                    )

                                    Text(
                                        text = modulo,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }

                                Divider()

                                permisos.forEach { permiso ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = permiso.name in permisosSeleccionados,
                                            enabled = !guardando,
                                            onCheckedChange = { marcado ->
                                                if (marcado) {
                                                    if (permiso.name !in permisosSeleccionados) {
                                                        permisosSeleccionados.add(permiso.name)
                                                    }
                                                } else {
                                                    permisosSeleccionados.remove(permiso.name)
                                                }
                                            }
                                        )

                                        Text(
                                            text = permiso.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!error.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                enabled = !guardando,
                onClick = onBackClick
            ) {
                Text("Cancelar")
            }

            Button(
                modifier = Modifier.weight(1f),
                enabled = !guardando &&
                        !cargandoPermisos &&
                        permisosAgrupados.isNotEmpty() &&
                        nombreRol.isNotBlank(),
                onClick = {
                    onCrearRol(
                        nombreRol.trim(),
                        permisosSeleccionados.toList()
                    )
                }
            ) {
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear rol")
                }
            }
        }
    }
}
