package com.sigefiv.app.screens.emprendimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendimientoFormScreen(
    state: EmprendimientoFormState,
    onAction: (EmprendimientoFormAction) -> Unit,
    onPickImageClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = MaterialTheme.colorScheme

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
        containerColor = colors.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = colors.inverseSurface,
                    contentColor = colors.inverseOnSurface,
                    actionColor = colors.inversePrimary
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = if (state.id == null) "Nuevo emprendimiento" else "Editar emprendimiento",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Presenta tu negocio a la comunidad",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface,
                    navigationIconContentColor = colors.primary
                )
            )
        },
        bottomBar = {
            Surface(
                color = colors.surface,
                contentColor = colors.onSurface,
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp),
                        onClick = { onAction(EmprendimientoFormAction.SaveAsDraft) },
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar borrador")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp),
                        onClick = { onAction(EmprendimientoFormAction.Publish) },
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(19.dp),
                                color = colors.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Publish,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Publicar")
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
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormIntroCard(isEditing = state.id != null)

            FormSection(
                title = "Información del negocio",
                subtitle = "Cuéntales a los vecinos qué ofreces",
                icon = Icons.Default.Store
            ) {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateNombre(it)) },
                    label = { Text("Nombre del emprendimiento *") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = state.palabrasClave,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdatePalabrasClave(it)) },
                    label = { Text("Palabras clave o productos *") },
                    placeholder = { Text("Ej. marcianos, helados, tortas, cerrajería") },
                    supportingText = {
                        Text("Estas palabras se usarán para que los vecinos puedan buscar tu emprendimiento.")
                    },
                    leadingIcon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = state.descripcion,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateDescripcion(it)) },
                    label = { Text("Descripción del servicio") },
                    placeholder = { Text("Describe tus productos o servicios…") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            onAction(EmprendimientoFormAction.RequestZoeDescription)
                        },
                        enabled = !state.isGeneratingWithZoe,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        if (state.isGeneratingWithZoe) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(17.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("ZOE está redactando…")
                        } else {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Crear descripción con ZOE")
                        }
                    }
                }
            }

            FormSection(
                title = "Contacto y atención",
                subtitle = "Indica cómo pueden encontrarte",
                icon = Icons.Default.ContactPhone
            ) {
                OutlinedTextField(
                    value = state.telefono,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateTelefono(it)) },
                    label = { Text("Teléfono de contacto *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        colors.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null,
                            tint = colors.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Contacto por WhatsApp",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Permitir que te escriban directamente",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = state.usarWhatsapp,
                            onCheckedChange = {
                                onAction(EmprendimientoFormAction.ToggleWhatsapp(it))
                            }
                        )
                    }
                }

                ModalitySelector(
                    selectedModality = state.modalidad,
                    onModalitySelected = {
                        onAction(EmprendimientoFormAction.UpdateModality(it))
                    }
                )

                OutlinedTextField(
                    value = state.zonaAtencion,
                    onValueChange = {
                        onAction(EmprendimientoFormAction.UpdateZonaAtencion(it))
                    },
                    label = { Text("Zona de atención") },
                    placeholder = { Text("Ej. Etapa 3, Mz. A, Villa El Salvador") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = state.horario,
                    onValueChange = { onAction(EmprendimientoFormAction.UpdateHorario(it)) },
                    label = { Text("Horario de atención (opcional)") },
                    placeholder = { Text("Ej. Lunes a sábado, 9:00 a. m. – 7:00 p. m.") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            FormSection(
                title = "Fotos del negocio",
                subtitle = "Las imágenes ayudan a mostrar lo que ofreces",
                icon = Icons.Default.PhotoLibrary
            ) {
                PhotoSelectorSection(
                    fotos = state.fotos,
                    onAddPhotoClick = onPickImageClick,
                    onRemovePhoto = { index ->
                        onAction(EmprendimientoFormAction.RemovePhoto(index))
                    }
                )
                Text(
                    text = "Puedes agregar hasta 5 fotografías.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FormIntroCard(isEditing: Boolean) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = colors.primary,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = colors.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isEditing) "Actualiza tu negocio" else "Haz crecer tu negocio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onPrimaryContainer
                )
                Text(
                    text = if (isEditing)
                        "Mantén tus datos al día para que los vecinos puedan contactarte."
                    else
                        "Registra tu emprendimiento y dale visibilidad dentro de tu comunidad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = colors.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = colors.onPrimaryContainer
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(color = colors.outlineVariant)
            content()
        }
    }
}

@Composable
fun ModalitySelector(
    selectedModality: String,
    onModalitySelected: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val modalities = listOf(
        "local_emprendedor" to ("Local" to Icons.Default.Store),
        "domicilio_cliente" to ("A domicilio" to Icons.Default.Home),
        "ambos" to ("Ambos" to Icons.Default.Handshake),
        "virtual" to ("Virtual" to Icons.Default.Language)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Modalidad de atención",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            modalities.forEach { (key, option) ->
                FilterChip(
                    selected = selectedModality == key,
                    onClick = { onModalitySelected(key) },
                    label = { Text(option.first) },
                    leadingIcon = {
                        Icon(
                            option.second,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primaryContainer,
                        selectedLabelColor = colors.onPrimaryContainer,
                        selectedLeadingIconColor = colors.onPrimaryContainer
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedModality == key,
                        borderColor = colors.outlineVariant,
                        selectedBorderColor = colors.primary
                    )
                )
            }
        }
    }
}

@Composable
fun PhotoSelectorSection(
    fotos: List<String>,
    onAddPhotoClick: () -> Unit,
    onRemovePhoto: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fotografías (${fotos.size}/5)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (fotos.size < 5) {
                TextButton(
                    onClick = onAddPhotoClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.primary)
                ) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text("Agregar")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            fotos.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            colors.outlineVariant,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Fotografía ${index + 1}",
                            tint = colors.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { onRemovePhoto(index) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(3.dp)
                            .size(25.dp)
                            .background(colors.errorContainer, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Eliminar fotografía",
                            tint = colors.onErrorContainer,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            if (fotos.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .width(240.dp)
                        .height(76.dp)
                        .clickable(onClick = onAddPhotoClick),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        colors.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = colors.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Añadir imágenes de tu negocio",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}