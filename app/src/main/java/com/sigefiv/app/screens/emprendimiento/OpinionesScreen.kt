package com.sigefiv.app.screens.emprendimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.api.OpinionResponseModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpinionesScreen(
    nombreNegocio: String,
    promedioEstrellas: Float = 0.0f,
    totalOpiniones: Int = 0,
    distribucionPorcentaje: Map<Int, Int> = mapOf(5 to 0, 4 to 0, 3 to 0, 2 to 0, 1 to 0),
    opiniones: List<OpinionResponseModel> = emptyList(),
    userOpinion: OpinionResponseModel? = null, // 👈 Recibe directamente si el usuario ya opinó desde la API
    cargando: Boolean = false,
    onBackClick: () -> Unit,
    onEnviarOpinion: (calificacion: Int, comentario: String) -> Unit = { _, _ -> }
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var mostrarDialogo by remember { mutableStateOf(false) }

    // 🔍 Si el servidor nos devuelve una opinión del usuario actual, es una edición
    val esEdicion = userOpinion != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opiniones y calificaciones", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = Color(0xFF10B981),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                // 🏷️ El texto cambia dinámicamente de forma automática e infalible
                Text(
                    text = if (esEdicion) "Editar mi opinión" else "Escribir opinión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta resumen de calificaciones y estadísticas reales
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = nombreNegocio, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "$promedioEstrellas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "($totalOpiniones opiniones)", fontSize = 13.sp, color = Color(0xFF6B7280))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        (5 downTo 1).forEach { estrella ->
                            val porcentaje = distribucionPorcentaje[estrella] ?: 0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            ) {
                                Text(text = "$estrella estrellas", fontSize = 12.sp, color = Color(0xFF4B5563), modifier = Modifier.width(65.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFE5E7EB))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxHeight().fillMaxWidth(porcentaje / 100f).background(Color(0xFF15803D), RoundedCornerShape(4.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "$porcentaje%", fontSize = 12.sp, color = Color(0xFF6B7280), modifier = Modifier.width(32.dp))
                            }
                        }
                    }
                }
            }

            item {
                Text(text = "Opiniones de vecinos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            }

            // Validar estado de carga o lista vacía real
            when {
                cargando -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    }
                }
                opiniones.isEmpty() -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(text = "Aún no hay opiniones registradas para este negocio.", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }
                }
                else -> {
                    items(opiniones) { opinion ->
                        OpinionItemCard(opinion = opinion)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }

    // Diálogo para escribir o editar opinión (Pre-carga valores si ya existía una reseña)
    if (mostrarDialogo) {
        var calificacionSeleccionada by remember {
            mutableStateOf(userOpinion?.calificacion ?: 5)
        }
        var comentarioTexto by remember {
            mutableStateOf(userOpinion?.comentario ?: "")
        }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = {
                Text(
                    text = if (esEdicion) "Editar tu opinión" else "Escribir opinión",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Selecciona tu calificación", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { calificacionSeleccionada = i }) {
                                Icon(
                                    imageVector = if (i <= calificacionSeleccionada) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$i estrellas",
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = comentarioTexto,
                        onValueChange = { comentarioTexto = it },
                        label = { Text("Escribe tu comentario...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEnviarOpinion(calificacionSeleccionada, comentarioTexto)
                        mostrarDialogo = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text(if (esEdicion) "Actualizar" else "Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun OpinionItemCard(opinion: OpinionResponseModel) {
    val nombreAutor = opinion.user?.name ?: "Vecino SIGEFIV"
    val inicial = nombreAutor.take(1).uppercase()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(38.dp).background(Color(0xFFDCFCE7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = inicial, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = nombreAutor, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827))
                    Text(text = opinion.createdAt ?: "Fecha reciente", fontSize = 11.sp, color = Color(0xFF6B7280))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                repeat(opinion.calificacion) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = opinion.comentario ?: "Sin comentarios escritos.", fontSize = 13.sp, color = Color(0xFF374151), lineHeight = 18.sp)
        }
    }
}