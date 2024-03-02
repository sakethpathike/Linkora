package com.sakethh.linkora.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM.Settings.isOnLatestUpdate
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationVM: NavigationVM = viewModel()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val clickedSettingsRoute = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        NavigationBar(
            modifier = Modifier.fillMaxWidth()/*.requiredHeight(55.dp)*/
        ) {
            if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
                NavigationBarItem(selected = currentRoute == NavigationRoutes.HOME_SCREEN.name,
                    onClick = {
                        if (currentRoute != NavigationRoutes.HOME_SCREEN.name) navController.navigate(
                            NavigationRoutes.HOME_SCREEN.name
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == NavigationRoutes.HOME_SCREEN.name) {
                                Icons.Filled.Home
                            } else {
                                Icons.Outlined.Home
                            }, contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Home", style = MaterialTheme.typography.titleSmall)
                    })
            }
            navigationVM.btmBarList.forEach {
                NavigationBarItem(
                    selected = currentRoute == it.navigationRoute.name, onClick = {
                        if (it.navigationRoute.name == NavigationRoutes.SETTINGS_SCREEN.name) clickedSettingsRoute.value =
                            true
                        if (currentRoute != it.navigationRoute.name) navController.navigate(it.navigationRoute.name)
                    }, icon = {
                        BadgedBox(badge = {
                            if (!clickedSettingsRoute.value) {
                                if (currentRoute != NavigationRoutes.SETTINGS_SCREEN.name && it.navigationRoute.name == NavigationRoutes.SETTINGS_SCREEN.name && isAutoCheckUpdatesEnabled.value && !isOnLatestUpdate.value && SettingsScreenVM.latestAppInfoFromServer.stableVersionCode.value != 0) {
                                    Badge()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (currentRoute == it.navigationRoute.name) {
                                    it.selectedIcon
                                } else {
                                    it.nonSelectedIcon
                                }, contentDescription = null
                            )
                        }
                    }, label = {
                        Text(text = it.itemName, style = MaterialTheme.typography.titleSmall)
                    })
            }
        }
    }
}