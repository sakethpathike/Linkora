package com.sakethh.linkora.ui.screens.settings.specific.data.sync

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.ui.screens.settings.PreferenceType
import com.sakethh.linkora.ui.screens.settings.Preferences
import com.sakethh.linkora.ui.screens.settings.Preferences.dataStore
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
    fun saveConnection(context: Context, url: String, apiKey: String) {
        viewModelScope.launch {
            try {
                isTryingToConnectToServer.value = true
                httpClient.get("$url/testBearer") {
                    bearerAuth(apiKey)
                }.let {
                    pushUIEvent(UiEvent.ShowSnackBar(it.status.value.toString()))
                }
            } catch (e: Exception) {
                pushUIEvent(UiEvent.ShowSnackBar(e.message.toString()))
            } finally {
                Preferences.syncHostUrl.value = url
                Preferences.syncAPIKey.value = apiKey
                Preferences.changeSettingPreferenceValue(
                    preferenceKey = stringPreferencesKey(
                        PreferenceType.SYNC_API_KEY.name
                    ), dataStore = context.dataStore, newValue = apiKey
                )
                Preferences.changeSettingPreferenceValue(
                    preferenceKey = stringPreferencesKey(
                        PreferenceType.SYNC_HOST_URL.name
                    ), dataStore = context.dataStore, newValue = url
                )
                isTryingToConnectToServer.value = false
            }
        }
    }
}