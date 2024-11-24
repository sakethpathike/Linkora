package com.sakethh.linkora.ui.screens.settings.specific.data.sync

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.utils.UiEvent
import com.sakethh.linkora.utils.UiEventManager.pushUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomServerSetupScreenViewModel @Inject constructor(private val httpClient: HttpClient) :
    ViewModel() {
    val isTryingToConnectToServer = mutableStateOf(false)
    fun connectToServer(url: String, apiKey: String) {
        viewModelScope.launch {
            try {
                isTryingToConnectToServer.value = true
                httpClient.get(url) {
                    bearerAuth(apiKey)
                }.let {
                    pushUIEvent(UiEvent.ShowSnackBar(it.status.description))
                }
            } catch (e: Exception) {
                pushUIEvent(UiEvent.ShowSnackBar(e.message.toString()))
            } finally {
                isTryingToConnectToServer.value = false
            }
        }
    }
}