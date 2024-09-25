package com.sakethh.linkora.ui.screens.settings.specific

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.screens.settings.PrivacySettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(navController: NavController, settingsScreenVM: SettingsScreenVM) {
    val privacySettingsScreenVM: PrivacySettingsScreenVM = viewModel()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        settingsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    SpecificScreenScaffold(
        topAppBarText = LocalizedStrings.privacy.value,
        navController = navController
    ) { paddingValues, topAppBarScrollBehaviour ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            item {
                RegularSettingComponent(
                    settingsUIElement = privacySettingsScreenVM.privacySection(
                        context
                    )
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}