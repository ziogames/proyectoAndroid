@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.emprendimiento

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

private val ColorWhatsApp = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendimientoFormScreen(
    state: EmprendimientoFormState,
    onAction: (EmprendimientoFormAction) -> Unit,
    onPickImageClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(state.errorMessage, state.successMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onAction(EmprendimientoFormAction.ClearMessages)
        }
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onAction(EmprendimientoFormAction.ClearMessages)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.inversePrimary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (state.id == null) "Nuevo emprendimiento" else "Editar emprendimiento",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Directorio comercial vecinal",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        onClick = { onAction(EmprendimientoFormAction.SaveAsDraft) },
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Borrador",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        onClick = { onAction(EmprendimientoFormAction.Publish) },
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Publish,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Publicar negocio",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FormIntroCard(isEditing = state.id != null, primaryColor = primaryColor)

            // Sección 1: Información básica
            FormSection(
                title = "Información del negocio",
                subtitle = "Presenta tu emprendimiento a los vecinos",
                icon = Icons.Outlined.Storefront,
                primaryColor = primaryColor
            ) {
                // 1. Nombre
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateNombre(it)) },
                    label = { Text("Nombre del emprendimiento *") },
                    placeholder = { Text("Ej. Bodega San Juan, Reparaciones Alex") },
                    leadingIcon = { Icon(Icons.Outlined.Storefront, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    colors = coloresCampoGenerico(primaryColor)
                )

                // 2. Selector Desplegable de Categoría
                var categoriaExpanded by remember { mutableStateOf(false) }
                val categoriasDisponibles = listOf("Comida", "Servicios", "Belleza", "Construcción", "Comercio")

                ExposedDropdownMenuBox(
                    expanded = categoriaExpanded,
                    onExpandedChange = { categoriaExpanded = !categoriaExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.categoria.ifBlank { "Selecciona una categoría" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rubro o categoría *") },
                        leadingIcon = { Icon(Icons.Outlined.Category, contentDescription = null, tint = primaryColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = coloresCampoGenerico(primaryColor)
                    )

                    ExposedDropdownMenu(
                        expanded = categoriaExpanded,
                        onDismissRequest = { categoriaExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        categoriasDisponibles.forEach { opcion ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opcion,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (state.categoria == opcion) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onAction(EmprendimientoFormAction.UpdateCategoria(opcion))
                                    categoriaExpanded = false
                                }
                            )
                        }
                    }
                }

                // 3. Palabras Clave
                OutlinedTextField(
                    value = state.palabrasClave,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdatePalabrasClave(it)) },
                    label = { Text("Productos o palabras clave *") },
                    placeholder = { Text("Ej. menú, postres, tortas, delivery, gasfitería") },
                    supportingText = {
                        Text(
                            "Servirán para que los vecinos te encuentren rápidamente en el buscador.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = { Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Next
                    ),
                    colors = coloresCampoGenerico(primaryColor)
                )

                // 4. Descripción
                OutlinedTextField(
                    value = state.descripcion,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateDescripcion(it)) },
                    label = { Text("Descripción de productos o servicios") },
                    placeholder = { Text("Describe brevemente la propuesta de tu negocio…") },
                    leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default
                    ),
                    colors = coloresCampoGenerico(primaryColor)
                )

                // Botón Asistente IA ZOE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        onClick = { onAction(EmprendimientoFormAction.RequestZoeDescription) },
                        enabled = !state.isGeneratingWithZoe,
                        shape = RoundedCornerShape(10.dp),
                        color = primaryColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.isGeneratingWithZoe) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = primaryColor
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "ZOE redactando…",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = primaryColor
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Redactar con ZOE IA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            }
                        }
                    }
                }
            }

            // Sección 2: Contacto y modalidades
            FormSection(
                title = "Contacto y atención",
                subtitle = "Facilita la comunicación con tus clientes",
                icon = Icons.Outlined.Call,
                primaryColor = primaryColor
            ) {
                OutlinedTextField(
                    value = state.telefono,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateTelefono(it)) },
                    label = { Text("Teléfono de contacto *") },
                    placeholder = { Text("Ej. 987654321") },
                    leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    colors = coloresCampoGenerico(primaryColor)
                )

                // Tarjeta Switch WhatsApp
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ColorWhatsApp.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = null,
                                tint = ColorWhatsApp,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pedidos por WhatsApp",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Permitir que los vecinos te escriban directamente",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = state.usarWhatsapp,
                            onCheckedChange = { onAction(EmprendimientoFormAction.ToggleWhatsapp(it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ColorWhatsApp
                            )
                        )
                    }
                }

                ModalitySelector(
                    selectedModality = state.modalidad,
                    primaryColor = primaryColor,
                    onModalitySelected = { onAction(EmprendimientoFormAction.UpdateModality(it)) }
                )

                OutlinedTextField(
                    value = state.zonaAtencion,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateZonaAtencion(it)) },
                    label = { Text("Zona o ubicación de atención") },
                    placeholder = { Text("Ej. Mz. I Lote 10, Sector 2") },
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = coloresCampoGenerico(primaryColor)
                )

                OutlinedTextField(
                    value = state.horario,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateHorario(it)) },
                    label = { Text("Horario de atención (opcional)") },
                    placeholder = { Text("Ej. Lun a Sáb: 8:00 AM - 6:00 PM") },
                    leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = coloresCampoGenerico(primaryColor)
                )
            }

            // Sección 3: Fotografías
            FormSection(
                title = "Fotografías del negocio",
                subtitle = "Muestra tus instalaciones, productos o menú",
                icon = Icons.Outlined.PhotoLibrary,
                primaryColor = primaryColor
            ) {
                PhotoSelectorSection(
                    fotos = state.fotos,
                    primaryColor = primaryColor,
                    onAddPhotoClick = onPickImageClick,
                    onRemovePhoto = { index -> onAction(EmprendimientoFormAction.RemovePhoto(index)) }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormIntroCard(isEditing: Boolean, primaryColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Storefront,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text = if (isEditing) "Actualizar emprendimiento" else "Impulsa tu negocio",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (isEditing)
                        "Mantén tus datos actualizados para que los vecinos puedan contactarte fácilmente."
                    else
                        "Registra tu actividad comercial y dale visibilidad en toda la comunidad vecinal.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    primaryColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                thickness = 0.8.dp
            )

            content()
        }
    }
}

@Composable
fun ModalitySelector(
    selectedModality: String,
    primaryColor: Color,
    onModalitySelected: (String) -> Unit
) {
    val modalities = listOf(
        "local_emprendedor" to ("Local físico" to Icons.Outlined.Storefront),
        "domicilio_cliente" to ("A domicilio" to Icons.Outlined.Home),
        "ambos" to ("Ambos" to Icons.Outlined.Handshake),
        "virtual" to ("Virtual" to Icons.Outlined.Language)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Modalidad de atención",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            modalities.forEach { (key, option) ->
                val selected = selectedModality == key

                FilterChip(
                    selected = selected,
                    onClick = { onModalitySelected(key) },
                    label = {
                        Text(
                            text = option.first,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = option.second,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor.copy(alpha = 0.14f),
                        selectedLabelColor = primaryColor,
                        selectedLeadingIconColor = primaryColor,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selected) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
fun PhotoSelectorSection(
    fotos: List<String>,
    primaryColor: Color,
    onAddPhotoClick: () -> Unit,
    onRemovePhoto: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fotos adjuntas (${fotos.size}/5)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (fotos.size < 5) {
                Surface(
                    onClick = onAddPhotoClick,
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddAPhoto,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Añadir foto",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            fotos.forEachIndexed { index, photoPath ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = photoPath),
                        contentDescription = "Fotografía ${index + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                            .clickable { onRemovePhoto(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Eliminar fotografía",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            if (fotos.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable(onClick = onAddPhotoClick),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddPhotoAlternate,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Toca aquí para seleccionar fotos de la galería",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun coloresCampoGenerico(primaryColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = primaryColor,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
    focusedLabelColor = primaryColor,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = primaryColor
)