package com.sakethh.linkora.utils

fun String?.ifNullOrBlank(string: () -> String): String {
    return if (this.isNullOrBlank()) {
        string()
    } else {
        this
    }
}