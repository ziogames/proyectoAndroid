package com.sigefiv.app.screens.emprendimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo temporal para las opiniones
data class OpinionModel(
    val id: Int,
    val autor: String,
    val tiempo: String,
    val calificacion: Int,
    val comentario: String,
    val utilidadCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpinionesScreen(
    nombreNegocio: String,
    onBackClick: () -> Unit,
    onEscribirOpinionClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    // Datos de ejemplo para las opiniones
    val opiniones = listOf(
        OpinionModel(1, "María Torres", "Hace 2 semanas", 5, "Los panes son deliciosos y siempre muy frescos. Muy recomendados.", 3),
        OpinionModel(2, "Carlos Rojas", "Hace 1 mes", 5, "Excelente atención y calidad. El pastel de chocolate estuvo riquísimo.", 2),
        OpinionModel(3, "Ana López", "Hace 2 meses", 4, "Buen servicio y precios justos. Volveré a comprar.", 1)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opiniones y calificaciones", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onEscribirOpinionClick,
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Text("Escribir opinión", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera con resumen de puntuación
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = nombreNegocio, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "4.8", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "(32 opiniones)", color = Color.Gray, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Barras de progreso por estrella (Ejemplo estático visual)
                        BarraEstrella(estrellas = "5 estrellas", porcentaje = 0.76f, textoPorcentaje = "76%")
                        BarraEstrella(estrellas = "4 estrellas", porcentaje = 0.16f, textoPorcentaje = "16%")
                        BarraEstrella(estrellas = "3 estrellas", porcentaje = 0.06f, textoPorcentaje = "6%")
                        BarraEstrella(estrellas = "2 estrellas", porcentaje = 0.02f, textoPorcentaje = "2%")
                        BarraEstrella(estrellas = "1 estrella", porcentaje = 0.0f, textoPorcentaje = "0%")
                    }
                }
            }

            // Título de la sección
            item {
                Text(text = "Opiniones de vecinos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            // Lista de comentarios
            items(opiniones) { opinion ->
                OpinionItem(opinion = opinion)
            }
        }
    }
}

@Composable
fun BarraEstrella(estrellas: String, porcentaje: Float, textoPorcentaje: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = estrellas, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(70.dp))
        LinearProgressIndicator(
            progress = porcentaje,
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF2E7D32),
            trackColor = Color(0xFFE0E0E0)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = textoPorcentaje, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(35.dp))
    }
}

@Composable
fun OpinionItem(opinion: OpinionModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opinion.autor.take(1),
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = opinion.autor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = opinion.tiempo, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Estrellas de la reseña
            Row {
                repeat(5) { index ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < opinion.calificacion) Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = opinion.comentario, fontSize = 13.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "👍 Útil (${opinion.utilidadCount})", fontSize = 12.sp, color = Color(0xFF1976D2))
        }
    }
}