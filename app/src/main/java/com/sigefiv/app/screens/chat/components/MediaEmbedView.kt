package com.sigefiv.app.screens.chat.components

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sigefiv.app.utils.MediaInfo
import com.sigefiv.app.utils.MediaType

@Composable
fun MediaEmbedView(mediaInfo: MediaInfo) {
    // Si es TikTok o Facebook, mostramos la tarjeta elegante estilo WhatsApp
    if (mediaInfo.type == MediaType.TIKTOK || mediaInfo.type == MediaType.FACEBOOK) {
        MediaPreviewCard(mediaInfo = mediaInfo)
        return
    }

    // Si es YouTube, mantenemos el reproductor in-app intacto
    Spacer(modifier = Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()

                    val htmlContent = """
                        <html><body style="margin:0;padding:0;background-color:#000;">
                        <iframe width="100%" height="100%" src="${mediaInfo.embedUrl}?autoplay=0" frameborder="0" allowfullscreen></iframe>
                        </body></html>
                    """.trimIndent()

                    loadDataWithBaseURL("https://www.sigefiv.app", htmlContent, "text/html", "utf-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}