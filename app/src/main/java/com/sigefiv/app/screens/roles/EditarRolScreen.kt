package com.sigefiv.app.screens.roles

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.RolDetalle
import androidx.compose.foundation.layout.navigationBarsPadding
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

/*
|--------------------------------------------------------------------------
| DESIGN SYSTEM: SIGEFIV ROLES
|--------------------------------------------------------------------------
*/
private val VerdeSuave = Color(0xFFDCFCE7)
private val VerdeBorde = Color(0xFFBBF7D0)


@Composable
fun EditarRolScreen(
    rol: RolDetalle,
    guardando: Boolean,
    onBackClick: () -> Unit,
    onGuardar: (String, List<String>) -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val colorPrincipalOscuro = colorPrincipal.copy(alpha = 0.88f)

    var nombreRol by remember { mutableStateOf(rol.name) }
    val esAdmin = rol.name.trim().lowercase() == "administrador"

    val permisosSeleccionados = remember {
        mutableStateMapOf<String, Boolean>().apply {
            rol.permisosAgrupados.values.flatten().forEach {
                put(it.name, it.asignado)
            }
        }
    }

    val totalPermisos = remember(rol.permisosAgrupados) {
        rol.permisosAgrupados.values.sumOf { it.size }
    }
    val seleccionadosCount = permisosSeleccionados.count { it.value }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                // Barra inferior de acción persistente
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                val seleccionados = permisosSeleccionados.filter { it.value }.keys.toList()
                                onGuardar(nombreRol, seleccionados)
                            },
                            enabled = !guardando && nombreRol.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorPrincipal,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (guardando) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Guardando cambios...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar Modificaciones", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                // Header corporativo SIGEFIV
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 6.dp,
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(colorPrincipalOscuro, colorPrincipal)
                                )
                            )
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    onClick = onBackClick,
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.18f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Volver",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = "Configuración del Rol",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Módulos y privilegios del sistema",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            // Botón de guardado rápido superior
                            IconButton(
                                onClick = {
                                    val seleccionados = permisosSeleccionados.filter { it.value }.keys.toList()
                                    onGuardar(nombreRol, seleccionados)
                                },
                                enabled = !guardando && nombreRol.isNotBlank(),
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.White.copy(alpha = 0.18f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Guardar",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Contenedor principal scrollable
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen de privilegios asignados
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(VerdeSuave, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = colorPrincipal,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "COBERTURA DE ACCESOS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "$seleccionadosCount de $totalPermisos concedidos",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Surface(
                                    color = VerdeSuave,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, VerdeBorde)
                                ) {
                                    Text(
                                        text = "${if (totalPermisos > 0) (seleccionadosCount * 100 / totalPermisos) else 0}%",
                                        color = colorPrincipalOscuro,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Datos generales
                    item {
                        Text(
                            text = "IDENTIFICACIÓN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = nombreRol,
                                    onValueChange = { if (!esAdmin) nombreRol = it },
                                    label = { Text("Nombre del Rol") },
                                    enabled = !esAdmin,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Badge,
                                            contentDescription = null,
                                            tint = colorPrincipal,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colorPrincipal,
                                        cursorColor = colorPrincipal,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                                if (esAdmin) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "El nombre del rol de Administrador está protegido por el sistema.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Título de sección de permisos
                    item {
                        Text(
                            text = "MÓDULOS DEL SISTEMA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.6.sp
                        )
                    }

                    // Tarjeta por cada categoría de permisos
                    items(rol.permisosAgrupados.keys.toList(), key = { it }) { categoria ->
                        val listaPermisos = rol.permisosAgrupados[categoria] ?: emptyList()
                        val todosMarcados = listaPermisos.all { permisosSeleccionados[it.name] == true }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Barra del encabezado de cada módulo
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Folder,
                                                contentDescription = null,
                                                tint = colorPrincipal,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = categoria,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${listaPermisos.count { permisosSeleccionados[it.name] == true }} de ${listaPermisos.size} activos",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Botón de alternado rápido por módulo
                                    TextButton(
                                        onClick = {
                                            val nuevoEstado = !todosMarcados
                                            listaPermisos.forEach { p ->
                                                permisosSeleccionados[p.name] = nuevoEstado
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = if (todosMarcados) "Desmarcar" else "Todos",
                                            color = colorPrincipal,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )

                                // Lista estructurada de permisos con layout interactivo
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listaPermisos.forEach { permiso ->
                                        val isChecked = permisosSeleccionados[permiso.name] ?: false
                                        val animBorderColor by animateColorAsState(
                                            targetValue = if (isChecked) VerdeBorde else MaterialTheme.colorScheme.outlineVariant,
                                            label = "bordePermiso"
                                        )
                                        val animFondoColor by animateColorAsState(
                                            targetValue = if (isChecked) VerdeSuave.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
                                            label = "fondoPermiso"
                                        )

                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    permisosSeleccionados[permiso.name] = !isChecked
                                                },
                                            shape = RoundedCornerShape(10.dp),
                                            color = animFondoColor,
                                            border = BorderStroke(1.dp, animBorderColor)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = formatearNombrePermiso(permiso.name),
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = permiso.name,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                Switch(
                                                    checked = isChecked,
                                                    onCheckedChange = { permisosSeleccionados[permiso.name] = it },
                                                    colors = SwitchDefaults.colors(
                                                        checkedThumbColor = Color.White,
                                                        checkedTrackColor = colorPrincipal,
                                                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Convierte el identificador técnico en una etiqueta amigable para el usuario
 * Ej: "asambleas.create" -> "Crear / Registrar"
 */
private fun formatearNombrePermiso(permisoTecnico: String): String {
    val accion = permisoTecnico.split(".").lastOrNull()?.lowercase() ?: permisoTecnico
    return when (accion) {
        "index" -> "Ver listado y detalles"
        "create" -> "Crear y registrar nuevo"
        "edit" -> "Modificar y actualizar"
        "destroy", "delete" -> "Eliminar registro"
        "enviar" -> "Enviar notificaciones"
        "imprimir" -> "Generar reportes e impresión"
        "publicar" -> "Publicar información"
        "dashboard" -> "Acceso a panel principal"
        "configuracion" -> "Configuración general"
        else -> accion.replaceFirstChar { it.uppercase() }
    }
}

