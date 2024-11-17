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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationVM: NavigationVM = viewModel()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination
    LinkoraTheme {
        NavigationBar(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (SettingsPreference.isHomeScreenEnabled.value) {
                NavigationBarItem(
                    selected = currentRoute?.hasRoute(HomeScreenRoute::class) == true,
                    onClick = {
                        if (currentRoute?.hasRoute(HomeScreenRoute::class) == false) {
                            navController.navigate(HomeScreenRoute)
                            HomeScreenVM.initialStart = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute?.hasRoute(HomeScreenRoute::class) == true) {
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
                    selected = currentRoute?.hasRoute(it.navigationRoute::class) == true,
                    onClick = {
                        if (currentRoute?.hasRoute(it.navigationRoute::class) == false) {
                            navController.navigate(it.navigationRoute)
                            HomeScreenVM.initialStart = true
                        }
                    }, icon = {
                        Icon(
                            imageVector = if (currentRoute?.hasRoute(it.navigationRoute::class) == true) {
                                it.selectedIcon
                            } else {
                                it.nonSelectedIcon
                            }, contentDescription = null
                        )
                    }, label = {
                        Text(
                            text = it.itemName.value,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1
                        )
                    })
            }
        }
    }
}