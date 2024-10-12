package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddANewLinkDialogBoxVM @Inject constructor(val foldersRepo: FoldersRepo) : ViewModel() {
    val subFoldersList = mutableStateListOf<FoldersTable>()

    init {
        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(SettingsPreference.isSendCrashReportsEnabled.value)
    }

    private val _childFolders = MutableStateFlow(emptyList<FoldersTable>())
    val childFolders = _childFolders.asStateFlow()

    fun changeParentFolderId(parentFolderId: Long) {
        viewModelScope.launch {
            foldersRepo.getChildFoldersOfThisParentID(parentFolderId).collectLatest {
                _childFolders.emit(it)
            }
        }
    }
}