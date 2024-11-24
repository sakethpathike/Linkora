package com.sakethh.linkora.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
inline fun <reified T : Any> customRememberSavable(noinline init: () -> T): T {
    return rememberSaveable(saver = Saver(save = {
        Json.encodeToString(it)
    }, restore = {
        Json.decodeFromString<T>(it)
    }), init = init)
}

@Composable
inline fun <reified T : Any> customMutableRememberSavable(noinline init: () -> MutableState<T>): MutableState<T> {
    return rememberSaveable(saver = Saver(save = {
        Json.encodeToString(it.value)
    }, restore = {
        mutableStateOf(Json.decodeFromString<T>(it))
    }), init = init)
}

