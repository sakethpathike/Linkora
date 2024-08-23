package com.sakethh.linkora.ui.screens.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings.beta
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsSections
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificSettingsScreenScaffold(
    topAppBarText: String,
    navController: NavController,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (paddingValues: PaddingValues, topAppBarScrollBehaviour: TopAppBarScrollBehavior) -> Unit
) {
    val topAppBarScrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LinkoraTheme {
        Scaffold(floatingActionButton = {
            floatingActionButton()
        }, floatingActionButtonPosition = FabPosition.Center, topBar = {
            Column {
                LargeTopAppBar(navigationIcon = {
                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, scrollBehavior = topAppBarScrollBehaviour, title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = topAppBarText,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        if (SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.DATA || SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.LANGUAGE) {
                            Text(
                                text = beta.value,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(
                                        start = 4.dp
                                    )
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .padding(4.dp)
                            )
                        }
                    }
                })
            }
        }) {
            content(it, topAppBarScrollBehaviour)
        }
    }
}