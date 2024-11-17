package com.sakethh.linkora.ui.screens.settings

import android.content.Context
import android.os.Build
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.BuildConfig
import com.sakethh.linkora.ui.navigation.HomeScreenRoute
import com.sakethh.linkora.ui.screens.linkLayout.LinkLayout
import com.sakethh.linkora.utils.LinkoraValues
import com.sakethh.linkora.worker.refreshLinks.RefreshLinksWorkerRequestBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

object SettingsPreference : ViewModel() {

    const val APP_VERSION_NAME = "v0.10.0"
    private const val APP_VERSION_CODE = 32

    val Context.dataStore by preferencesDataStore("linkoraDataStore")

    val shouldFollowDynamicTheming = mutableStateOf(false)
    val shouldFollowSystemTheme = mutableStateOf(true)
    val shouldDarkThemeBeEnabled = mutableStateOf(false)
    val isInAppWebTabEnabled = mutableStateOf(true)
    val isAutoDetectTitleForLinksEnabled = mutableStateOf(false)
    val showAssociatedImagesInLinkMenu = mutableStateOf(false)
    val isBtmSheetEnabledForSavingLinks = mutableStateOf(false)
    val isHomeScreenEnabled = mutableStateOf(true)
    val isSendCrashReportsEnabled = mutableStateOf(true)
    val didDataAutoDataMigratedFromV9 = mutableStateOf(false)
    val isAutoCheckUpdatesEnabled = mutableStateOf(true)
    val showDescriptionForSettingsState = mutableStateOf(true)
    val useLanguageStringsBasedOnFetchedValuesFromServer = mutableStateOf(false)
    val isOnLatestUpdate = mutableStateOf(false)
    val didServerTimeOutErrorOccurred = mutableStateOf(false)
    private val savedAppCode = mutableIntStateOf(APP_VERSION_CODE - 1)
    val selectedSortingType = mutableStateOf(SortingPreferences.NEW_TO_OLD.name)
    val primaryJsoupUserAgent =
        mutableStateOf("Twitterbot/1.0")
    val secondaryJsoupUserAgent =
        mutableStateOf("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:131.0) Gecko/20100101 Firefox/131.0")
    val localizationServerURL =
        mutableStateOf(LinkoraValues.LINKORA_LOCALIZATION_SERVER)
    val isShelfMinimizedInHomeScreen = mutableStateOf(false)
    val lastSelectedPanelID = mutableLongStateOf(-1)
    val preferredAppLanguageName = mutableStateOf("English")
    val preferredAppLanguageCode = mutableStateOf("en")
    val totalLocalAppStrings = mutableIntStateOf(328)
    val totalRemoteStrings = mutableIntStateOf(0)
    val remoteStringsLastUpdatedOn = mutableStateOf("")
    val currentlySelectedLinkLayout = mutableStateOf(LinkLayout.REGULAR_LIST_VIEW.name)
    val enableBorderForNonListViews = mutableStateOf(true)
    val enableTitleForNonListViews = mutableStateOf(true)
    val enableBaseURLForNonListViews = mutableStateOf(true)
    val enableFadedEdgeForNonListViews = mutableStateOf(true)
    val shouldFollowAmoledTheme = mutableStateOf(false)
    val forceSaveWithoutFetchingAnyMetaData = mutableStateOf(false)
    val startDestination = mutableStateOf(HomeScreenRoute.toString())

    suspend fun <T> readSettingPreferenceValue(
        preferenceKey: androidx.datastore.preferences.core.Preferences.Key<T>,
        dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
    ): T? {
        return dataStore.data.first()[preferenceKey]
    }

    private suspend fun readSortingPreferenceValue(
        preferenceKey: androidx.datastore.preferences.core.Preferences.Key<String>,
        dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
    ): String? {
        return dataStore.data.first()[preferenceKey]
    }

    fun <T> changeSettingPreferenceValue(
        preferenceKey: androidx.datastore.preferences.core.Preferences.Key<T>,
        dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
        newValue: T,
    ) {
        viewModelScope.launch {
            dataStore.edit {
                it[preferenceKey] = newValue
            }
        }
    }

    suspend fun changeSortingPreferenceValue(
        preferenceKey: androidx.datastore.preferences.core.Preferences.Key<String>,
        dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
        newValue: SortingPreferences,
    ) {
        dataStore.edit {
            it[preferenceKey] = newValue.name
        }
    }

    suspend fun readAllPreferencesValues(context: Context) {
        coroutineScope {
            kotlinx.coroutines.awaitAll(
                async {
                    isHomeScreenEnabled.value = if (readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                            dataStore = context.dataStore
                        ) == null
                    ) {
                        true
                    } else {
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                            dataStore = context.dataStore
                        ) == true
                    }
                },
                async {
                    shouldFollowSystemTheme.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.FOLLOW_SYSTEM_THEME.name),
                        dataStore = context.dataStore
                    ) ?: (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                },
                async {
                    startDestination.value = readSettingPreferenceValue(
                        preferenceKey = stringPreferencesKey(SettingsPreferences.INITIAL_ROUTE.name),
                        dataStore = context.dataStore
                    ) ?: startDestination.value
                },
                async {
                    shouldDarkThemeBeEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.DARK_THEME.name),
                        dataStore = context.dataStore
                    ) ?: (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                },
                async {
                    shouldFollowDynamicTheming.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.DYNAMIC_THEMING.name),
                        dataStore = context.dataStore
                    ) ?: false
                },
                async {
                    primaryJsoupUserAgent.value = readSettingPreferenceValue(
                        preferenceKey = stringPreferencesKey(SettingsPreferences.JSOUP_USER_AGENT.name),
                        dataStore = context.dataStore
                    )
                        ?: primaryJsoupUserAgent.value
                },
                async {
                    secondaryJsoupUserAgent.value = readSettingPreferenceValue(
                        preferenceKey = stringPreferencesKey(SettingsPreferences.SECONDARY_JSOUP_USER_AGENT.name),
                        dataStore = context.dataStore
                    )
                        ?: secondaryJsoupUserAgent.value
                },
                async {
                    showDescriptionForSettingsState.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.SETTING_COMPONENT_DESCRIPTION_STATE.name),
                        dataStore = context.dataStore
                    ) ?: true
                },
                async {
                    isInAppWebTabEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.CUSTOM_TABS.name),
                        dataStore = context.dataStore
                    ) ?: false
                },
                async {
                    didDataAutoDataMigratedFromV9.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.IS_DATA_MIGRATION_COMPLETED_FROM_V9.name),
                        dataStore = context.dataStore
                    ) ?: false
                },
                async {
                    isAutoDetectTitleForLinksEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name),
                        dataStore = context.dataStore
                    ) ?: false
                },
                async {
                    totalRemoteStrings.intValue = readSettingPreferenceValue(
                        preferenceKey = intPreferencesKey(SettingsPreferences.TOTAL_REMOTE_STRINGS.name),
                        dataStore = context.dataStore
                    ) ?: 0
                },
                async {
                    isSendCrashReportsEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.SEND_CRASH_REPORTS.name),
                        dataStore = context.dataStore
                    ) ?: true
                },
                async {
                    isAutoCheckUpdatesEnabled.value = (readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.AUTO_CHECK_UPDATES.name),
                        dataStore = context.dataStore
                    ) ?: BuildConfig.FLAVOR) != "fdroid"
                },
                async {
                    selectedSortingType.value = readSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(SettingsPreferences.SORTING_PREFERENCE.name),
                        dataStore = context.dataStore
                    ) ?: SortingPreferences.NEW_TO_OLD.name
                },
                async {
                    savedAppCode.intValue = readSettingPreferenceValue(
                        preferenceKey = intPreferencesKey(SettingsPreferences.SAVED_APP_CODE.name),
                        dataStore = context.dataStore
                    ) ?: (APP_VERSION_CODE - 1)
                },
                async {
                    showAssociatedImagesInLinkMenu.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.ASSOCIATED_IMAGES_IN_LINK_MENU_VISIBILITY.name),
                        dataStore = context.dataStore
                    ) ?: true
                },
                async {
                    isShelfMinimizedInHomeScreen.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.SHELF_VISIBLE_STATE.name),
                        dataStore = context.dataStore
                    ) ?: false
                },
                async {
                    forceSaveWithoutFetchingAnyMetaData.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.FORCE_SAVE_WITHOUT_FETCHING_META_DATA.name),
                        dataStore = context.dataStore
                    ) ?: forceSaveWithoutFetchingAnyMetaData.value
                },
                async {
                    preferredAppLanguageName.value = readSettingPreferenceValue(
                        preferenceKey = stringPreferencesKey(SettingsPreferences.APP_LANGUAGE_NAME.name),
                        dataStore = context.dataStore
                    ) ?: "English"
                },
                async {
                    preferredAppLanguageCode.value = readSettingPreferenceValue(
                        preferenceKey = stringPreferencesKey(SettingsPreferences.APP_LANGUAGE_CODE.name),
                        dataStore = context.dataStore
                    ) ?: "en"
                },
                async {
                    lastSelectedPanelID.longValue = (readSettingPreferenceValue(
                        preferenceKey = intPreferencesKey(SettingsPreferences.LAST_SELECTED_PANEL_ID.name),
                        dataStore = context.dataStore
                    ) ?: -1).toLong()
                },
                async {
                    useLanguageStringsBasedOnFetchedValuesFromServer.value =
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.USE_REMOTE_LANGUAGE_STRINGS.name),
                            dataStore = context.dataStore
                        ) ?: false
                },
                async {
                    enableBorderForNonListViews.value =
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.BORDER_VISIBILITY_FOR_NON_LIST_VIEWS.name),
                            dataStore = context.dataStore
                        ) ?: enableBorderForNonListViews.value
                },
                async {
                    enableTitleForNonListViews.value =
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.TITLE_VISIBILITY_FOR_NON_LIST_VIEWS.name),
                            dataStore = context.dataStore
                        ) ?: enableTitleForNonListViews.value
                },
                async {
                    enableBaseURLForNonListViews.value =
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.BASE_URL_VISIBILITY_FOR_NON_LIST_VIEWS.name),
                            dataStore = context.dataStore
                        ) ?: enableBaseURLForNonListViews.value
                },
                async {
                    enableFadedEdgeForNonListViews.value =
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.FADED_EDGE_VISIBILITY_FOR_NON_LIST_VIEWS.name),
                            dataStore = context.dataStore
                        ) ?: enableFadedEdgeForNonListViews.value
                },
                async {
                    localizationServerURL.value =
                        readSettingPreferenceValue(
                            preferenceKey = stringPreferencesKey(SettingsPreferences.LOCALIZATION_SERVER_URL.name),
                            dataStore = context.dataStore
                        ) ?: LinkoraValues.LINKORA_LOCALIZATION_SERVER
                },
                async {
                    remoteStringsLastUpdatedOn.value =
                        readSettingPreferenceValue(
                            preferenceKey = stringPreferencesKey(SettingsPreferences.REMOTE_STRINGS_LAST_UPDATED_ON.name),
                            dataStore = context.dataStore
                        ) ?: ""
                },
                async {
                    currentlySelectedLinkLayout.value =
                        readSettingPreferenceValue(
                            preferenceKey = stringPreferencesKey(SettingsPreferences.CURRENTLY_SELECTED_LINK_VIEW.name),
                            dataStore = context.dataStore
                        ) ?: currentlySelectedLinkLayout.value
                },
                async {
                    shouldFollowAmoledTheme.value =
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.AMOLED_THEME_STATE.name),
                            dataStore = context.dataStore
                        ) ?: false
                },
                async {
                    RefreshLinksWorkerRequestBuilder.REFRESH_LINKS_WORKER_TAG.emit(
                        UUID.fromString(
                            readSettingPreferenceValue(
                                preferenceKey = stringPreferencesKey(SettingsPreferences.CURRENT_WORK_MANAGER_WORK_UUID.name),
                                dataStore = context.dataStore
                            ) ?: "d267865d-e1c9-42b7-be38-1ab6db0e312b"
                        )
                    )
                },
            )
        }
    }
}