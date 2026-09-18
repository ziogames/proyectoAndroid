
package com.sigefiv.app.screens.actividad

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sigefiv.app.data.model.Actividad
import com.sigefiv.app.data.model.ActividadResponse
import com.sigefiv.app.viewmodel.ActividadUiState
import com.sigefiv.app.viewmodel.ActividadViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadScreen(
    viewModel: ActividadViewModel,
    onBack: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Actividad de usuarios",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.cargarActividades()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        when (val resultado = estado) {

            is ActividadUiState.Cargando -> {
                LoadingContent(paddingValues)
            }

            is ActividadUiState.Error -> {
                ErrorContent(
                    paddingValues = paddingValues,
                    mensaje = resultado.mensaje,
                    onRetry = {
                        viewModel.cargarActividades()
                    }
                )
            }

            is ActividadUiState.Exito -> {
                ActividadContent(
                    paddingValues = paddingValues,
                    respuesta = resultado.respuesta
                )
            }
        }
    }
}

/*
 * CONTENIDO PRINCIPAL
 */
@Composable
private fun ActividadContent(
    paddingValues: PaddingValues,
    respuesta: ActividadResponse
) {
    var busqueda by remember {
        mutableStateOf("")
    }

    var moduloSeleccionado by remember {
        mutableStateOf("Todos")
    }

    val actividadesFiltradas = respuesta.actividades.datos.filter { actividad ->

        val coincideBusqueda =
            actividad.modulo.contains(busqueda, ignoreCase = true) ||
                    actividad.accion.contains(busqueda, ignoreCase = true) ||
                    actividad.usuario?.nombre.orEmpty()
                        .contains(busqueda, ignoreCase = true) ||
                    actividad.ip.orEmpty()
                        .contains(busqueda, ignoreCase = true)

        val coincideModulo =
            moduloSeleccionado == "Todos" ||
                    actividad.modulo.equals(
                        moduloSeleccionado,
                        ignoreCase = true
                    )

        coincideBusqueda && coincideModulo
    }

    val modulos = listOf("Todos") +
            respuesta.actividades.datos
                .map { it.modulo }
                .distinct()
                .sorted()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            EncabezadoActividad()
        }

        item {
            EstadisticasSection(respuesta)
        }

        item {
            ModulosSection(respuesta)
        }

        item {
            SearchSection(
                busqueda = busqueda,
                onBusquedaChange = {
                    busqueda = it
                }
            )
        }

        item {
            FilterSection(
                modulos = modulos,
                moduloSeleccionado = moduloSeleccionado,
                onModuloSelected = {
                    moduloSeleccionado = it
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registro de actividades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "${actividadesFiltradas.size} registros",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (actividadesFiltradas.isEmpty()) {
            item {
                EmptyContent()
            }
        } else {
            items(
                items = actividadesFiltradas,
                key = { it.id }
            ) { actividad ->

                ActividadCard(
                    actividad = actividad
                )
            }
        }
    }
}

/*
 * ENCABEZADO
 */
@Composable
private fun EncabezadoActividad() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Panel de actividad",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Consulta las acciones realizadas por los usuarios de SIGEFIV.",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }
    }
}

/*
 * ESTADÍSTICAS
 */
@Composable
private fun EstadisticasSection(
    respuesta: ActividadResponse
) {
    val estadisticas = respuesta.estadisticas

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Analytics,
                title = "Actividades",
                value = estadisticas.totalActividades.toString()
            )

            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Person,
                title = "Usuarios activos",
                value = estadisticas.usuariosActivos.toString()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                title = "Hoy",
                value = estadisticas.actividadesHoy.toString()
            )

            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Code,
                title = "Módulos",
                value = estadisticas.modulos.size.toString()
            )
        }
    }
}

@Composable
private fun StatisticCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/*
 * MÓDULOS MÁS UTILIZADOS
 */
@Composable
private fun ModulosSection(
    respuesta: ActividadResponse
) {
    val modulos = respuesta.estadisticas.modulos
        .sortedByDescending { it.total }
        .take(5)

    if (modulos.isEmpty()) return

    val maxTotal = modulos.maxOfOrNull { it.total }
        ?.coerceAtLeast(1)
        ?: 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Módulos más utilizados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            modulos.forEach { modulo ->

                val fraction =
                    modulo.total.toFloat() / maxTotal.toFloat()

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = modulo.modulo,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = modulo.total.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.12f
                                )
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    MaterialTheme.colorScheme.primary
                                )
                        )
                    }
                }
            }
        }
    }
}

/*
 * BÚSQUEDA
 */
@Composable
private fun SearchSection(
    busqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onBusquedaChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (busqueda.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onBusquedaChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        },
        placeholder = {
            Text("Buscar usuario, módulo, acción o IP")
        },
        shape = RoundedCornerShape(14.dp)
    )
}

/*
 * FILTROS
 */
@Composable
private fun FilterSection(
    modulos: List<String>,
    moduloSeleccionado: String,
    onModuloSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Filtrar por módulo",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = modulos.size
            ) { index ->

                val modulo = modulos[index]

                FilterChip(
                    selected = moduloSeleccionado == modulo,
                    onClick = {
                        onModuloSelected(modulo)
                    },
                    label = {
                        Text(modulo)
                    }
                )
            }
        }
    }
}

/*
 * TARJETA DE ACTIVIDAD
 */
@Composable
private fun ActividadCard(
    actividad: Actividad
) {
    val nombreUsuario =
        actividad.usuario?.nombre ?: "Usuario desconocido"

    val inicial =
        nombreUsuario.firstOrNull()?.uppercase() ?: "?"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.12f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inicial,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = nombreUsuario,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = actividad.usuario?.email
                            ?: "Sin correo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.12f
                    )
                ) {
                    Text(
                        text = actividad.modulo,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = actividad.accion,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalInfoRow(
                label = "Fecha",
                value = formatearFecha(actividad.fechaCreacion)
            )

            actividad.ip?.let { ip ->
                HorizontalInfoRow(
                    label = "IP",
                    value = ip
                )
            }

            actividad.ruta?.let { ruta ->
                HorizontalInfoRow(
                    label = "Ruta",
                    value = ruta
                )
            }
        }
    }
}

@Composable
private fun HorizontalInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

/*
 * CARGANDO
 */
@Composable
private fun LoadingContent(
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/*
 * ERROR
 */
@Composable
private fun ErrorContent(
    paddingValues: PaddingValues,
    mensaje: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No se pudo cargar la actividad",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mensaje,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.Button(
            onClick = onRetry
        ) {
            Text("Reintentar")
        }
    }
}

/*
 * SIN RESULTADOS
 */
@Composable
private fun EmptyContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "No se encontraron actividades",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Prueba con otro filtro o búsqueda.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

/*
 * FORMATEAR FECHA
 */
private fun formatearFecha(fecha: String): String {
    return try {
        val fechaLimpia = fecha
            .replace("Z", "")
            .substringBefore(".")

        val formatoEntrada = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault()
        )

        val formatoSalida = SimpleDateFormat(
            "dd-MM-yyyy HH:mm",
            Locale.getDefault()
        )

        val fechaConvertida = formatoEntrada.parse(fechaLimpia)

        if (fechaConvertida != null) {
            formatoSalida.format(fechaConvertida)
        } else {
            fecha
        }

    } catch (e: Exception) {
        fecha
    }
}