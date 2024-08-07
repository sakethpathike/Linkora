package com.sakethh.linkora.utils

// https://open.spotify.com/track/6MMuI4k4Vs5ghtDH5i2hBg?si=37cee6b35cf44476
fun sanitizeLink(url: String): String {
    return when {
        url.contains("https://open.spotify.com") || url.contains("http://open.spotify.com") -> {
            url.substringBefore("?")
        }

        else -> url
    }
}

@JvmName("sanitizeLinkExtenxsionFunction")
fun String.sanitizeLink(): String {
    return when {
        // TODO() this.startsWith()
        this.contains("https://open.spotify.com") || this.contains("http://open.spotify.com") -> {
            this.substringBefore("?")
        }

        else -> this
    }
}