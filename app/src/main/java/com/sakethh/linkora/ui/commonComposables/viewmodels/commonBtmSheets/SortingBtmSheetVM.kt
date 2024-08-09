package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import kotlinx.coroutines.launch


class SortingBtmSheetVM : ViewModel() {
    data class SortingBtmSheet(
        val sortingName: Int,
        val onClick: () -> Unit,
        val sortingType: SortingPreferences,
    )

    val sortingBottomSheetData: (context: Context) -> List<SortingBtmSheet> = {
        listOf(
            SortingBtmSheet(sortingName = R.string.newest_to_oldest, onClick = {
                SettingsPreference.selectedSortingType.value =
                    SortingPreferences.NEW_TO_OLD.name
                viewModelScope.launch {
                    SettingsPreference.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = it.dataStore,
                        newValue = SortingPreferences.NEW_TO_OLD
                    )
                }
            }, sortingType = SortingPreferences.NEW_TO_OLD),
            SortingBtmSheet(sortingName = R.string.oldest_to_newest, onClick = {
                SettingsPreference.selectedSortingType.value =
                    SortingPreferences.OLD_TO_NEW.name
                viewModelScope.launch {
                    SettingsPreference.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = it.dataStore,
                        newValue = SortingPreferences.OLD_TO_NEW
                    )
                }
            }, sortingType = SortingPreferences.OLD_TO_NEW),
            SortingBtmSheet(sortingName = R.string.a_to_z_sequence, onClick = {
                SettingsPreference.selectedSortingType.value =
                    SortingPreferences.A_TO_Z.name
                viewModelScope.launch {
                    SettingsPreference.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = it.dataStore,
                        newValue = SortingPreferences.A_TO_Z
                    )
                }
            }, sortingType = SortingPreferences.A_TO_Z),
            SortingBtmSheet(
                sortingType = SortingPreferences.Z_TO_A,
                sortingName = R.string.z_to_a_sequence,
                onClick = {
                    SettingsPreference.selectedSortingType.value =
                        SortingPreferences.Z_TO_A.name
                    viewModelScope.launch {
                        SettingsPreference.changeSortingPreferenceValue(
                            preferenceKey = stringPreferencesKey(
                                SettingsPreferences.SORTING_PREFERENCE.name
                            ),
                            dataStore = it.dataStore,
                            newValue = SortingPreferences.Z_TO_A
                        )
                    }
                }),
        )
    }
}