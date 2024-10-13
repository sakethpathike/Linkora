package com.sakethh.linkora

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.commonComposables.AddANewLinkDialogBox
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.utils.linkoraLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class IntentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val shouldUIBeVisible = rememberSaveable {
                mutableStateOf(true)
            }
            val isDataExtractingForTheLink = rememberSaveable {
                mutableStateOf(false)
            }
            val specificCollectionsScreenVM: SpecificCollectionsScreenVM = hiltViewModel()
            val context = LocalContext.current
            LaunchedEffect(key1 = Unit) {
                specificCollectionsScreenVM.eventChannel.collectLatest {
                    when (it) {
                        is CommonUiEvent.ShowToast -> {
                            Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }
            LaunchedEffect(key1 = Unit) {
                LocalizedStrings.loadStrings(this@IntentActivity)
            }
            LinkoraTheme {
                AddANewLinkDialogBox(
                    shouldDialogBoxAppear = shouldUIBeVisible,
                    screenType = SpecificScreenType.INTENT_ACTIVITY,
                    onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                        isDataExtractingForTheLink.value = true
                        if (selectedNonDefaultFolderID == (-1).toLong()) {
                            linkoraLog("add in saved links, webURL is $webURL")
                            specificCollectionsScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.AddANewLinkInSavedLinks(
                                    title, webURL, note, isAutoDetectSelected, onTaskCompleted = {
                                        shouldUIBeVisible.value = false
                                        isDataExtractingForTheLink.value = false
                                    }
                                )
                            )
                            return@AddANewLinkDialogBox
                        }
                        if (selectedNonDefaultFolderID == (-2).toLong()) {
                            linkoraLog("add in imp links, webURL is $webURL")
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
                            return@AddANewLinkDialogBox
                        }
                        when {
                            selectedNonDefaultFolderID != null && selectedDefaultFolderName != null -> {
                                linkoraLog("add in folder; id is $selectedNonDefaultFolderID, name is $selectedDefaultFolderName\n webURL is $webURL")
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
                    },
                    isDataExtractingForTheLink = isDataExtractingForTheLink.value,
                    onFolderCreateClick = { givenFolderName, folderNote, folderId ->
                        specificCollectionsScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.CreateANewFolder(
                                FoldersTable(
                                    parentFolderID = folderId,
                                    folderName = givenFolderName,
                                    infoForSaving = folderNote
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