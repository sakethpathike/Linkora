package com.sakethh.linkora.ui.screens.settings.specific.data.sync

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomServerSetupScreen(navController: NavController) {
    val serverUrl = rememberSaveable {
        mutableStateOf("")
    }
    val apiKey = rememberSaveable {
        mutableStateOf("")
    }
    val customServerSetupScreenViewModel: CustomServerSetupScreenViewModel = hiltViewModel()
    SpecificScreenScaffold(
        topAppBarText = "Custom Server Setup", navController = navController
    ) { paddingValues, topAppBarScrollBehaviour ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
        ) {

            item {
                ServerConfigValueHolder(
                    holderLabel = "Host URL",
                    value = serverUrl.value,
                    onSaveClick = {
                        serverUrl.value = it
                    },
                    info = "For a server running on a local machine (ensure that this device and the server are running on the same Wi-Fi network), the URL must include the correct port number, corresponding to the one used by the server."
                )
            }

            item {
                ServerConfigValueHolder(
                    holderLabel = "API Key",
                    value = apiKey.value,
                    onSaveClick = {
                        apiKey.value = it
                    },
                    info =
                    "The API key you provide must match the one you entered during server setup. If they don't match, the setup will fail. Also, ensure that the host URL and port (if custom hosted locally) above are correct."
                )
            }
            item {
                if (customServerSetupScreenViewModel.isTryingToConnectToServer.value) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                } else {
                    Button(modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(), onClick = {
                        customServerSetupScreenViewModel.connectToServer(
                            serverUrl.value,
                            apiKey.value
                        )
                    }) {
                        Text(text = "Connect", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}