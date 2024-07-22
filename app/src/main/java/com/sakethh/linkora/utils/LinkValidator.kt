package com.sakethh.linkora.utils

fun isAValidURL(webURL: String): Boolean {
    return try {
        webURL.split("/")[2]
        true
    } catch (_: Exception) {
        false
    }
}