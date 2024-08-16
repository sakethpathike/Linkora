package com.sakethh.linkora.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationVM: NavigationVM = viewModel()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LinkoraTheme {
        NavigationBar(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (SettingsPreference.isHomeScreenEnabled.value) {
                NavigationBarItem(selected = currentRoute == NavigationRoutes.HOME_SCREEN.name,
                    onClick = {
                        if (currentRoute != NavigationRoutes.HOME_SCREEN.name) {
                            navController.navigate(
                                NavigationRoutes.HOME_SCREEN.name
                            )
                            HomeScreenVM.initialStart = true
                        }
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
                        Text(
                            text = LocalizedStrings.home.value,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1
                        )
                    })
            }
            navigationVM.btmBarList.forEach {
                NavigationBarItem(
                    selected = currentRoute == it.navigationRoute.name, onClick = {
                        if (currentRoute != it.navigationRoute.name) {
                            navController.navigate(it.navigationRoute.name)
                            HomeScreenVM.initialStart = true
                        }
                    }, icon = {
                        Icon(
                            imageVector = if (currentRoute == it.navigationRoute.name) {
                                it.selectedIcon
                            } else {
                                it.nonSelectedIcon
                            }, contentDescription = null
                        )
                    }, label = {
                        Text(
                            text = it.itemName,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1
                        )
                    })
            }
        }
    }
}