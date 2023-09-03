package com.sakethh.linkora.btmSheet

import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.launch


class SortingBtmSheetVM : ViewModel() {
    data class SortingBtmSheet(
        val sortingName: String,
        val onClick: () -> Unit,
        val sortingType: SettingsScreenVM.SortingPreferences,
    )

    val sortingBottomSheetData = listOf(
        SortingBtmSheet(sortingName = "Name A -> Z", onClick = {
            SettingsScreenVM.Settings.selectedSortingType.value =
                SettingsScreenVM.SortingPreferences.A_TO_Z.name
            viewModelScope.launch {
                SettingsScreenVM.Settings.changeSortingPreferenceValue(
                    preferenceKey = preferencesKey(
                        SettingsScreenVM.SettingsPreferences.SORTING_PREFERENCE.name
                    ),
                    dataStore = SettingsScreenVM.Settings.dataStore,
                    newValue = SettingsScreenVM.SortingPreferences.A_TO_Z
                )
            }
        }, sortingType = SettingsScreenVM.SortingPreferences.A_TO_Z),
        SortingBtmSheet(
            sortingType = SettingsScreenVM.SortingPreferences.Z_TO_A,
            sortingName = "Name Z -> A",
            onClick = {
                SettingsScreenVM.Settings.selectedSortingType.value =
                    SettingsScreenVM.SortingPreferences.Z_TO_A.name
                viewModelScope.launch {
                    SettingsScreenVM.Settings.changeSortingPreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsScreenVM.SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = SettingsScreenVM.Settings.dataStore,
                        newValue = SettingsScreenVM.SortingPreferences.Z_TO_A
                    )
                }
            }),
    )
}