package com.sigefiv.app.screens.incidencias

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import androidx.compose.foundation.border
private data class CategoriaOpcion(
    val nombre: String,
    val icono: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReporteScreen(
    onEnviarReporte: (titulo: String, categoria: String, mensaje: String, uri: Uri?) -> Unit,
    onTomarFoto: () -> Unit,
    onElegirGaleria: () -> Unit,
    imagenUri: Uri?,
    onEliminarImagen: () -> Unit,
    cargando: Boolean,
    onBackClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())
    val colors = MaterialTheme.colorScheme

    var titulo by remember { mutableStateOf("") }
    var categoriaSelected by remember { mutableStateOf("Limpieza y Áreas Verdes") }
    var mensaje by remember { mutableStateOf("") }

    val categorias = remember {
        listOf(
            CategoriaOpcion("Limpieza y Áreas Verdes", Icons.Outlined.CleaningServices),
            CategoriaOpcion("Iluminación / Farolas", Icons.Outlined.Lightbulb),
            CategoriaOpcion("Seguridad y Accesos", Icons.Outlined.Security),
            CategoriaOpcion("Infraestructura", Icons.Outlined.Build),
            CategoriaOpcion("Otros", Icons.Outlined.MoreHoriz)
        )
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nuevo Reporte Vecinal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.onPrimary
                        )
                        Text(
                            text = "Comunícate directamente con la directiva",
                            fontSize = 12.sp,
                            color = colors.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = colors.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorPrincipal)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TARJETA DE GUÍA / ENCABEZADO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(colorPrincipal.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RateReview,
                            contentDescription = null,
                            tint = colorPrincipal,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Detalla tu aviso con precisión",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "La directiva revisará el caso para darle pronta atención.",
                            fontSize = 11.5.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            // CAMPO DE TÍTULO
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "TÍTULO DEL REPORTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurfaceVariant,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 2.dp)
                )

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    placeholder = {
                        Text("Ej. Luminaria rota en parque central", fontSize = 13.5.sp, color = colors.onSurfaceVariant)
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Title, null, tint = colorPrincipal, modifier = Modifier.size(19.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedBorderColor = colorPrincipal,
                        unfocusedBorderColor = colors.outlineVariant.copy(alpha = 0.45f)
                    )
                )
            }

            // SELECTOR DE CATEGORÍA CON CHIPS E ICONOS
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CATEGORÍA O ÁREA AFECTADA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurfaceVariant,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 2.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categorias) { item ->
                        val isSelected = categoriaSelected == item.nombre
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) colorPrincipal else colors.surface,
                            border = if (isSelected) null else BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable { categoriaSelected = item.nombre }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = item.icono,
                                    contentDescription = null,
                                    tint = if (isSelected) colors.onPrimary else colorPrincipal,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.nombre,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.onPrimary else colors.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // DESCRIPCIÓN DETALLADA
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "DESCRIPCIÓN DEL PROBLEMA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurfaceVariant,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 2.dp)
                )

                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    placeholder = {
                        Text(
                            text = "Describe qué sucedió y la ubicación exacta (ej. Frente al lote 14)...",
                            fontSize = 13.sp,
                            color = colors.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Box(modifier = Modifier.padding(bottom = 60.dp)) {
                            Icon(Icons.Outlined.Description, null, tint = colorPrincipal, modifier = Modifier.size(19.dp))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedBorderColor = colorPrincipal,
                        unfocusedBorderColor = colors.outlineVariant.copy(alpha = 0.45f)
                    )
                )
            }

            // SECCIÓN DE FOTOGRAFÍA ADJUNTA
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EVIDENCIA FOTOGRÁFICA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Text(
                        text = "Opcional",
                        fontSize = 11.sp,
                        color = colors.outline
                    )
                }

                if (imagenUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.surfaceVariant.copy(alpha = 0.4f))
                            .border(BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.4f)), RoundedCornerShape(18.dp))
                    ) {
                        AsyncImage(
                            model = imagenUri,
                            contentDescription = "Foto adjunta",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626).copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .clickable { onEliminarImagen() }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Eliminar foto",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp, horizontal = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(colorPrincipal.copy(alpha = 0.10f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AddAPhoto,
                                    contentDescription = null,
                                    tint = colorPrincipal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Adjunta una fotografía de evidencia",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onTomarFoto,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.4f))
                                ) {
                                    Icon(Icons.Outlined.CameraAlt, null, tint = colorPrincipal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cámara", color = colorPrincipal, fontSize = 12.5.sp)
                                }

                                OutlinedButton(
                                    onClick = onElegirGaleria,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.4f))
                                ) {
                                    Icon(Icons.Outlined.Image, null, tint = colorPrincipal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Galería", color = colorPrincipal, fontSize = 12.5.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // BOTÓN DE ENVIAR
            Button(
                onClick = { onEnviarReporte(titulo, categoriaSelected, mensaje, imagenUri) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = titulo.isNotBlank() && mensaje.isNotBlank() && !cargando,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrincipal,
                    contentColor = colors.onPrimary,
                    disabledContainerColor = colors.surfaceVariant.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = colors.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar Reporte a la Directiva", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}