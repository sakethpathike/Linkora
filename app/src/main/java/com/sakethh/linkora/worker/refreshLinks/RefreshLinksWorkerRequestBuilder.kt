package com.sakethh.linkora.worker.refreshLinks

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.changeSettingPreferenceValue
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

class RefreshLinksWorkerRequestBuilder @Inject constructor(
    private val workManager: WorkManager,
    private val context: Context
) {
    companion object {
        val REFRESH_LINKS_WORKER_TAG =
            MutableStateFlow(UUID.fromString("d267865d-e1c9-42b7-be38-1ab6db0e312b"))
    }

    suspend fun request(): OneTimeWorkRequest {
        RefreshLinksWorker.successfulRefreshLinksCount.emit(0)
        changeSettingPreferenceValue(
            intPreferencesKey(SettingsPreferences.REFRESH_LINKS_TABLE_INDEX.name),
            context.dataStore, -1
        )
        changeSettingPreferenceValue(
            intPreferencesKey(SettingsPreferences.REFRESH_IMP_LINKS_TABLE_INDEX.name),
            context.dataStore, -1
        )
        changeSettingPreferenceValue(
            intPreferencesKey(SettingsPreferences.REFRESH_ARCHIVE_LINKS_TABLE_INDEX.name),
            context.dataStore, -1
        )
        changeSettingPreferenceValue(
            intPreferencesKey(SettingsPreferences.REFRESH_RECENTLY_VISITED_LINKS_TABLE_INDEX.name),
            context.dataStore, -1
        )

        val request = OneTimeWorkRequestBuilder<RefreshLinksWorker>()
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        changeSettingPreferenceValue(
            stringPreferencesKey(SettingsPreferences.CURRENT_WORK_MANAGER_WORK_UUID.name),
            context.dataStore,
            request.id.toString()
        )

        Log.d("Linkora Log", "In Builder")
        Log.d("Linkora Log", request.id.toString())
        REFRESH_LINKS_WORKER_TAG.emit(request.id)
        RefreshLinksWorker.superVisorJob?.cancel()
        workManager.enqueueUniqueWork(
            REFRESH_LINKS_WORKER_TAG.value.toString(),
            ExistingWorkPolicy.KEEP,
            request
        )
        return request
    }
}