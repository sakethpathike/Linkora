package com.sakethh.linkora

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.NewLinkBtmSheetUIParam
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import kotlinx.coroutines.launch

class IntentActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        window.setBackgroundDrawable(ColorDrawable(0))
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        val windowsType = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        window.setType(windowsType)
        setContent {
            val shouldUIBeVisible = rememberSaveable {
                mutableStateOf(true)
            }
            val isDataExtractingForTheLink = rememberSaveable {
                mutableStateOf(false)
            }
            val createVM: CreateVM = viewModel()
            val context = LocalContext.current
            val btmSheetState = rememberModalBottomSheetState()
            val coroutineScope = rememberCoroutineScope()
            NewLinkBtmSheet(
                NewLinkBtmSheetUIParam(
                    inIntentActivity = true,
                    shouldUIBeVisible = shouldUIBeVisible,
                    screenType = SpecificScreenType.INTENT_ACTIVITY,
                    btmSheetState = btmSheetState,
                    onLinkSaveClick = { isAutoDetectSelected, webURL, title, note, selectedDefaultFolder, selectedNonDefaultFolderID ->
                        isDataExtractingForTheLink.value = true
                        if (selectedDefaultFolder == "Saved Links") {
                            createVM.addANewLinkInSavedLinks(
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                autoDetectTitle = isAutoDetectSelected,
                                onTaskCompleted = {
                                    coroutineScope.launch {
                                        btmSheetState.hide()
                                        shouldUIBeVisible.value = false
                                        isDataExtractingForTheLink.value = false
                                    }
                                },
                                context = context
                            )
                        }
                        if (selectedDefaultFolder == "Important Links") {
                            createVM.addANewLinkInImpLinks(
                                context = context,
                                onTaskCompleted = {
                                    coroutineScope.launch {
                                        btmSheetState.hide()
                                        shouldUIBeVisible.value = false
                                        isDataExtractingForTheLink.value = false
                                    }
                                },
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                autoDetectTitle = isAutoDetectSelected
                            )
                        }
                        when {
                            selectedDefaultFolder != "Important Links" && selectedDefaultFolder != "Saved Links" -> {
                                if (selectedNonDefaultFolderID != null && selectedDefaultFolder != null) {
                                    createVM.addANewLinkInAFolderV10(
                                        title = title,
                                        webURL = webURL,
                                        noteForSaving = note,
                                        parentFolderID = selectedNonDefaultFolderID,
                                        context = context,
                                        folderName = selectedDefaultFolder,
                                        autoDetectTitle = isAutoDetectSelected,
                                        onTaskCompleted = {
                                            coroutineScope.launch {
                                                btmSheetState.hide()
                                                shouldUIBeVisible.value = false
                                                isDataExtractingForTheLink.value = false
                                            }
                                        })
                                }
                            }
                        }
                    },
                    parentFolderID = null,
                    onFolderCreated = {},
                    isDataExtractingForTheLink = isDataExtractingForTheLink
                )
            )
        }
    }
}

object IntentActivityData {
    val foldersData = mutableStateOf(emptyList<FoldersTable>())
}