
package com.sigefiv.app.screens.emprendimiento

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.viewmodel.EmprendimientoItemUi
import com.sigefiv.app.viewmodel.EmprendimientosListState

private val SigefivGreen = Color(0xFF15803D)
private val SigefivGreenDark = Color(0xFF166534)
private val SigefivGreenLight = Color(0xFFDCFCE7)
private val SigefivAmber = Color(0xFFB45309)
private val SigefivRed = Color(0xFFB91C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendimientosListScreen(
    state: EmprendimientosListState,
    onBackClick: () -> Unit,
    onNuevoEmprendimientoClick: () -> Unit,
    onEditarEmprendimientoClick: (Long) -> Unit,
    onCambiarEstadoClick: (Long, String) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Mis emprendimientos",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Administra tus negocios",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoEmprendimientoClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = "Nuevo negocio",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                containerColor = SigefivGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SigefivGreen
                        )
                        Text(
                            text = "Cargando tus emprendimientos...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                state.emprendimientos.isEmpty() -> {
                    EmptyEmprendimientos(
                        errorMessage = state.errorMessage,
                        onNuevoClick = onNuevoEmprendimientoClick
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Encabezado con resumen
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.5f
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 16.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SigefivGreenLight,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = SigefivGreenDark,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }

                                Spacer(Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tu actividad comercial",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        text = "${state.emprendimientos.size} " +
                                                if (state.emprendimientos.size == 1) {
                                                    "emprendimiento registrado"
                                                } else {
                                                    "emprendimientos registrados"
                                                },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                val publicados = state.emprendimientos.count {
                                    it.status.equals("published", ignoreCase = true)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = publicados.toString(),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SigefivGreen
                                    )
                                    Text(
                                        text = "Publicados",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        state.errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 4.dp
                                )
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(
                                items = state.emprendimientos,
                                key = { it.id }
                            ) { item ->
                                EmprendimientoCard(
                                    item = item,
                                    onEditarClick = {
                                        onEditarEmprendimientoClick(item.id)
                                    },
                                    onCambiarEstado = { nuevoEstado ->
                                        onCambiarEstadoClick(item.id, nuevoEstado)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyEmprendimientos(
    errorMessage: String?,
    onNuevoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(112.dp),
            shape = RoundedCornerShape(32.dp),
            color = SigefivGreenLight
        ) {
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = SigefivGreenDark,
                modifier = Modifier.padding(28.dp)
            )
        }

        Spacer(Modifier.height(26.dp))

        Text(
            text = if (errorMessage == null) {
                "Tu negocio comienza aquí"
            } else {
                "No pudimos cargar tus negocios"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = errorMessage
                ?: "Registra tu emprendimiento para que los vecinos conozcan tus servicios y productos.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onNuevoClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SigefivGreen,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 14.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )
            Spacer(Modifier.width(9.dp))
            Text(
                text = "Registrar mi negocio",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun EmprendimientoCard(
    item: EmprendimientoItemUi,
    onEditarClick: () -> Unit,
    onCambiarEstado: (String) -> Unit
) {
    val isPublished = item.status.equals("published", ignoreCase = true)
    val isDraft = item.status.equals("draft", ignoreCase = true)
    val statusText = when {
        isPublished -> "Publicado"
        isDraft -> "Borrador"
        item.status.equals("paused", ignoreCase = true) -> "Pausado"
        else -> item.status.replaceFirstChar { it.uppercase() }
    }

    val statusColor = when {
        isPublished -> SigefivGreen
        isDraft -> Color(0xFF64748B)
        else -> SigefivAmber
    }

    val statusBackground = when {
        isPublished -> SigefivGreenLight
        isDraft -> Color(0xFFE2E8F0)
        else -> Color(0xFFFEF3C7)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(15.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(13.dp)
                    )
                }

                Spacer(Modifier.width(13.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(5.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = SigefivGreen
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = item.categoria,
                            style = MaterialTheme.typography.labelMedium,
                            color = SigefivGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = statusBackground
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(6.dp),
                            shape = RoundedCornerShape(50),
                            color = statusColor
                        ) {}
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!item.description.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 21.sp,
                    maxLines = 3
                )
            }

            Spacer(Modifier.height(17.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(15.dp))

            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                ContactDetail(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = item.phone +
                            if (item.showWhatsapp) " · WhatsApp" else ""
                )

                ContactDetail(
                    icon = Icons.Default.Storefront,
                    label = "Modalidad",
                    value = item.modality
                        .replace("_", " ")
                        .replaceFirstChar { it.uppercase() }
                )

                if (!item.attentionZone.isNullOrBlank()) {
                    ContactDetail(
                        icon = Icons.Default.LocationOn,
                        label = "Zona de atención",
                        value = item.attentionZone
                    )
                }

                if (!item.schedule.isNullOrBlank()) {
                    ContactDetail(
                        icon = Icons.Default.Schedule,
                        label = "Horario",
                        value = item.schedule
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEditarClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(13.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(Modifier.width(7.dp))
                    Text("Editar", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        onCambiarEstado(
                            if (isPublished) "paused" else "published"
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPublished) {
                            SigefivAmber
                        } else {
                            SigefivGreen
                        },
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isPublished) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = if (isPublished) "Pausar" else "Publicar",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactDetail(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )

        Spacer(Modifier.width(11.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
