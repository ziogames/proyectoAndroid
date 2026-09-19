package com.sigefiv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.sigefiv.app.data.model.ChatUser

@Composable
fun AvatarUsuario(
    usuario: ChatUser?,
    modifier: Modifier = Modifier
) {
    val nombre = usuario?.name?.trim().orEmpty()

    val inicial = nombre
        .firstOrNull()
        ?.uppercase()
        ?: "V"

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF607D8B)),
        contentAlignment = Alignment.Center
    ) {

        if (!usuario?.avatar.isNullOrBlank()) {

            AsyncImage(
                model = usuario?.avatar,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Text(
                text = inicial,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}