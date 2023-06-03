package com.sakethh.linkora.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationVM: NavigationVM = viewModel()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LinkoraTheme {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(55.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            navigationVM.btmBarList.forEach {
                NavigationBarItem(
                    selected = currentRoute == it.navigationRoute.name,
                    onClick = {
                        if (currentRoute != it.navigationRoute.name)
                            navController.navigate(it.navigationRoute.name)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == it.navigationRoute.name) {
                                it.selectedIcon
                            } else {
                                it.nonSelectedIcon
                            }, contentDescription = null
                        )
                    })
            }
        }
    }
}