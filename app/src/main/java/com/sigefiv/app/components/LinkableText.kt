package com.sigefiv.app.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit

@Composable
fun LinkableText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    linkColor: Color = Color(0xFF4FC3F7), // Azul claro visible en fondo oscuro
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val uriHandler = LocalUriHandler.current

    // Detecta enlaces Markdown [Texto](https://...) O enlaces directos https://...
    val linkRegex = Regex("""\[([^\]]+)\]\((https?://[^\s)]+)\)|(https?://[^\s)]+)""")

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0

        for (match in linkRegex.findAll(text)) {
            val startIndex = match.range.first
            val endIndex = match.range.last + 1

            // Texto normal antes del enlace
            if (startIndex > lastIndex) {
                append(text.substring(lastIndex, startIndex))
            }

            val markdownTitle = match.groups[1]?.value
            val markdownUrl = match.groups[2]?.value
            val rawUrl = match.groups[3]?.value

            val urlToOpen = markdownUrl ?: rawUrl ?: match.value
            // Si quieres que se vea toda la URL o el título limpio:
            val displayText = if (markdownTitle != null && markdownUrl != null) {
                "$markdownTitle ($markdownUrl)"
            } else {
                urlToOpen
            }

            pushStringAnnotation(tag = "URL", annotation = urlToOpen)
            pushStyle(
                SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                )
            )
            append(displayText)
            pop()
            pop()

            lastIndex = endIndex
        }

        // Resto del texto normal
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = TextStyle(
            color = textColor,
            fontSize = fontSize,
            lineHeight = lineHeight
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    try {
                        uriHandler.openUri(annotation.item)
                    } catch (_: Exception) {
                    }
                }
        }
    )
}