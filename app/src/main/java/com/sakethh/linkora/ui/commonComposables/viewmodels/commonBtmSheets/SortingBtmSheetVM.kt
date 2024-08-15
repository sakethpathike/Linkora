package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.LocalizedStringsVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import kotlinx.coroutines.launch


class SortingBtmSheetVM :
    ViewModel() {
    data class SortingBtmSheet(
        val sortingName: String,
        val onClick: () -> Unit,
        val sortingType: SortingPreferences,
    )

    val sortingBottomSheetData: (context: Context, localizedStringsVM: LocalizedStringsVM) -> List<SortingBtmSheet> =
        { context, localizedStringsVM ->
        listOf(
            SortingBtmSheet(sortingName = localizedStringsVM.newestToOldest.value, onClick = {
                SettingsPreference.selectedSortingType.value =
                    SortingPreferences.NEW_TO_OLD.name
                viewModelScope.launch {
                    SettingsPreference.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = context.dataStore,
                        newValue = SortingPreferences.NEW_TO_OLD
                    )
                }
            }, sortingType = SortingPreferences.NEW_TO_OLD),
            SortingBtmSheet(sortingName = localizedStringsVM.oldestToNewest.value, onClick = {
                SettingsPreference.selectedSortingType.value =
                    SortingPreferences.OLD_TO_NEW.name
                viewModelScope.launch {
                    SettingsPreference.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = context.dataStore,
                        newValue = SortingPreferences.OLD_TO_NEW
                    )
                }
            }, sortingType = SortingPreferences.OLD_TO_NEW),
            SortingBtmSheet(sortingName = localizedStringsVM.aToZSequence.value, onClick = {
                SettingsPreference.selectedSortingType.value =
                    SortingPreferences.A_TO_Z.name
                viewModelScope.launch {
                    SettingsPreference.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = context.dataStore,
                        newValue = SortingPreferences.A_TO_Z
                    )
                }
            }, sortingType = SortingPreferences.A_TO_Z),
            SortingBtmSheet(
                sortingType = SortingPreferences.Z_TO_A,
                sortingName = localizedStringsVM.ztoASequence.value,
                onClick = {
                    SettingsPreference.selectedSortingType.value =
                        SortingPreferences.Z_TO_A.name
                    viewModelScope.launch {
                        SettingsPreference.changeSortingPreferenceValue(
                            preferenceKey = stringPreferencesKey(
                                SettingsPreferences.SORTING_PREFERENCE.name
                            ),
                            dataStore = context.dataStore,
                            newValue = SortingPreferences.Z_TO_A
                        )
                    }
                }),
        )
    }
}