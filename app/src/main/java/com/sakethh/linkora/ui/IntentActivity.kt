package com.sakethh.linkora.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.data.localDB.dto.FoldersTable
import com.sakethh.linkora.ui.commonComposables.AddNewLinkDialogBox
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.collections.SpecificScreenType
import com.sakethh.linkora.ui.viewmodels.localDB.CreateVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IntentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val shouldUIBeVisible = rememberSaveable {
                mutableStateOf(true)
            }
            val createVM: CreateVM = viewModel()
            val context = LocalContext.current
            LaunchedEffect(key1 = createVM.showToast.value.first) {
                if (createVM.showToast.value.first) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, createVM.showToast.value.second, Toast.LENGTH_SHORT)
                            .show()
                    }
                    createVM.showToast.value = false to ""
                }
            }
            val isDataExtractingForTheLink = rememberSaveable {
                mutableStateOf(false)
            }
            LinkoraTheme {
                AddNewLinkDialogBox(
                    shouldDialogBoxAppear = shouldUIBeVisible,
                    screenType = SpecificScreenType.INTENT_ACTIVITY,
                    parentFolderID = null,
                    onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                        isDataExtractingForTheLink.value = true
                        if (selectedDefaultFolderName == "Saved Links") {
                            createVM.addANewLinkInSavedLinks(
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                autoDetectTitle = isAutoDetectSelected,
                                onTaskCompleted = {
                                    shouldUIBeVisible.value = false
                                    isDataExtractingForTheLink.value = false
                                },
                                context = context
                            )
                        }
                        if (selectedDefaultFolderName == "Important Links") {
                            createVM.addANewLinkInImpLinks(
                                context = context,
                                onTaskCompleted = {
                                    shouldUIBeVisible.value = false
                                    isDataExtractingForTheLink.value = false
                                },
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                autoDetectTitle = isAutoDetectSelected
                            )
                        }
                        when {
                            selectedDefaultFolderName != "Important Links" && selectedDefaultFolderName != "Saved Links" -> {
                                if (selectedNonDefaultFolderID != null && selectedDefaultFolderName != null) {
                                    createVM.addANewLinkInAFolderV10(
                                        title = title,
                                        webURL = webURL,
                                        noteForSaving = note,
                                        parentFolderID = selectedNonDefaultFolderID,
                                        context = context,
                                        folderName = selectedDefaultFolderName,
                                        autoDetectTitle = isAutoDetectSelected,
                                        onTaskCompleted = {
                                            shouldUIBeVisible.value = false
                                            isDataExtractingForTheLink.value = false
                                        })
                                }
                            }
                        }
                    },
                    isDataExtractingForTheLink = isDataExtractingForTheLink.value
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