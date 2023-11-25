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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType

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
            val btmSheetState = rememberModalBottomSheetState()
            NewLinkBtmSheet(
                _inIntentActivity = true,
                shouldUIBeVisible = shouldUIBeVisible,
                screenType = SpecificScreenType.INTENT_ACTIVITY,
                btmSheetState = btmSheetState,
                onLinkSaved = {},
                onFolderCreated = {},
                parentFolderID = null
            )
        }
    }
}

object IntentActivityData {
    val foldersData = mutableStateOf(emptyList<FoldersTable>())
}