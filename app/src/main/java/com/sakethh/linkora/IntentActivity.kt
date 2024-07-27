package com.sakethh.linkora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.ui.commonComposables.AddNewLinkDialogBox
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.theme.LinkoraTheme

class IntentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val shouldUIBeVisible = rememberSaveable {
                mutableStateOf(true)
            }
            val context = LocalContext.current
            val isDataExtractingForTheLink = rememberSaveable {
                mutableStateOf(false)
            }
            val specificCollectionsScreenVM: SpecificCollectionsScreenVM = hiltViewModel()
            LinkoraTheme {
                AddNewLinkDialogBox(
                    shouldDialogBoxAppear = shouldUIBeVisible,
                    screenType = SpecificScreenType.INTENT_ACTIVITY,
                    onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                        isDataExtractingForTheLink.value = true
                        if (selectedDefaultFolderName == "Saved Links") {
                            specificCollectionsScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.AddANewLinkInSavedLinks(
                                    title, webURL, note, isAutoDetectSelected, onTaskCompleted = {
                                        shouldUIBeVisible.value = false
                                        isDataExtractingForTheLink.value = false
                                    }
                                )
                            )
                        }
                        if (selectedDefaultFolderName == "Important Links") {
                            specificCollectionsScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.AddANewLinkInImpLinks(
                                    onTaskCompleted = {
                                        shouldUIBeVisible.value = false
                                        isDataExtractingForTheLink.value = false
                                    },
                                    title = title,
                                    webURL = webURL,
                                    noteForSaving = note,
                                    autoDetectTitle = isAutoDetectSelected
                                )
                            )
                        }
                        when {
                            selectedDefaultFolderName != "Important Links" && selectedDefaultFolderName != "Saved Links" -> {
                                if (selectedNonDefaultFolderID != null && selectedDefaultFolderName != null) {
                                    specificCollectionsScreenVM.onUiEvent(
                                        SpecificCollectionsScreenUIEvent.AddANewLinkInAFolder(
                                            title = title,
                                            webURL = webURL,
                                            noteForSaving = note,
                                            folderID = selectedNonDefaultFolderID,
                                            folderName = selectedDefaultFolderName,
                                            autoDetectTitle = isAutoDetectSelected,
                                            onTaskCompleted = {
                                                shouldUIBeVisible.value = false
                                                isDataExtractingForTheLink.value = false
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    },
                    isDataExtractingForTheLink = isDataExtractingForTheLink.value,
                    onFolderCreateClick = { folderName, folderNote ->
                        specificCollectionsScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.CreateANewFolder(
                                FoldersTable(
                                    folderName,
                                    folderNote
                                )
                            )
                        )
                    }
                )
            }
            if (!shouldUIBeVisible.value) {
                this.finishAndRemoveTask()
            }
        }
    }
}

object IntentActivityData {
    val foldersData = mutableStateOf(emptyList<FoldersTable>())
}