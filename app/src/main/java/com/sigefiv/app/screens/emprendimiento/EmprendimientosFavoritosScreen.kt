package com.sigefiv.app.screens.emprendimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.EmprendimientoModel
import com.sigefiv.app.viewmodel.EmprendimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendimientosFavoritosScreen(
    viewModel: EmprendimientoViewModel,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onContactarWhatsapp: (String) -> Unit,
    onLlamar: (String) -> Unit
) {
    // Cargamos los favoritos reales de la base de datos al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarFavoritos()
    }
    val primaryColor = MaterialTheme.colorScheme.primary

    // Filtramos solo los emprendimientos que tienen esFavorito == true
    val favoritos = viewModel.emprendimientos.filter { it.esFavorito }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis favoritos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Opciones */ }) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = { /* Menú */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // Cabecera informativa estilo diseño de referencia
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tus emprendimientos favoritos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Aquí tienes los negocios que has marcado como favoritos para acceder fácilmente.",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 18.sp
                )
            }

            if (favoritos.isEmpty()) {
                // Estado vacío si no hay favoritos
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF9CA3AF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aún no tienes favoritos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4B5563)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toca el ícono de corazón en cualquier negocio para agregarlo aquí.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Lista vertical de favoritos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(favoritos) { negocio ->
                        FavoritoCard(
                            negocio = negocio,
                            onClick = { onItemClick(negocio.id) },
                            onFavoritoClick = { viewModel.toggleFavorito(negocio.id) },
                            onWhatsappClick = { onContactarWhatsapp(negocio.telefono) },
                            onLlamarClick = { onLlamar(negocio.telefono) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritoCard(
    negocio: EmprendimientoModel,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    onWhatsappClick: () -> Unit,
    onLlamarClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura de imagen cuadrada a la izquierda
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE5E7EB))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información central
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = negocio.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = negocio.categoria,
                    fontSize = 12.sp,
                    color = Color(0xFF047857),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Estrellas y opiniones
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "4.8", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1F2937))
                    Text(text = " (32)", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Acciones de la derecha (WhatsApp, Llamar y Corazón)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Corazón en rojo porque es favorito
                IconButton(
                    onClick = onFavoritoClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Quitar favorito",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Botones rápidos de contacto (WhatsApp y Llamar en miniatura)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onWhatsappClick,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFFE8F5E9), CircleShape)
                    ) {
                        Icon(
                            Icons.Outlined.Chat,
                            contentDescription = "WhatsApp",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = onLlamarClick,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFFE3F2FD), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Llamar",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}