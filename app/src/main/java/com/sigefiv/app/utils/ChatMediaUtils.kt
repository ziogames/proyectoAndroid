package com.sigefiv.app.utils

data class MediaInfo(
    val type: MediaType,
    val embedUrl: String,
    val originalUrl: String,
    val title: String
)

enum class MediaType {
    YOUTUBE, TIKTOK, FACEBOOK, WEB
}

object ChatMediaUtils {
    fun parseMediaUrl(text: String): MediaInfo? {
        val youtubeRegex = "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})".toRegex()
        val tiktokRegex = "(?:https?:\\/\\/)?(?:www\\.|[a-z]{2}\\.)?(?:tiktok\\.com|vt\\.tiktok\\.com|vm\\.tiktok\\.com)\\/[a-zA-Z0-9_-]+".toRegex()
        val facebookRegex = "(?:https?:\\/\\/)?(?:www\\.)?(?:facebook\\.com|fb\\.watch|fb\\.me)\\/[a-zA-Z0-9_./?-]+".toRegex()

        youtubeRegex.find(text)?.let { match ->
            val videoId = match.groupValues[1]
            return MediaInfo(
                type = MediaType.YOUTUBE,
                embedUrl = "https://www.youtube.com/embed/$videoId",
                originalUrl = match.value,
                title = "Video de YouTube"
            )
        }

        tiktokRegex.find(text)?.let { match ->
            return MediaInfo(
                type = MediaType.TIKTOK,
                embedUrl = "",
                originalUrl = match.value,
                title = "Ver video en TikTok"
            )
        }

        facebookRegex.find(text)?.let { match ->
            return MediaInfo(
                type = MediaType.FACEBOOK,
                embedUrl = "",
                originalUrl = match.value,
                title = "Ver video en Facebook"
            )
        }

        return null
    }
}