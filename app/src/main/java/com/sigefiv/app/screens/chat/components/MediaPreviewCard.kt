package com.sigefiv.app.screens.chat.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.utils.MediaInfo
import com.sigefiv.app.utils.MediaType

@Composable
fun MediaPreviewCard(mediaInfo: MediaInfo) {
    val context = LocalContext.current

    // Colores e identidad según la plataforma
    val (colorFondoIcono, tituloPlataforma, subtituloTexto) = when (mediaInfo.type) {
        MediaType.TIKTOK -> Triple(Color.Black, "Video de TikTok", "Toca para abrir en TikTok")
        MediaType.FACEBOOK -> Triple(Color(0xFF1877F2), "Video / Reel de Facebook", "Toca para abrir en Facebook")
        else -> Triple(Color(0xFFFF0000), "Video de YouTube", "Toca para reproducir")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mediaInfo.originalUrl))
                context.startActivity(intent)
            }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Caja tipo miniatura con el color e ícono de la red social
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorFondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Textos descriptivos limpios
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tituloPlataforma,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtituloTexto,
                    fontSize = 11.sp,
                    color = Color(0xFF4B5563),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Icono sutil de enlace externo
            Icon(
                imageVector = Icons.Outlined.OpenInNew,
                contentDescription = "Abrir",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}