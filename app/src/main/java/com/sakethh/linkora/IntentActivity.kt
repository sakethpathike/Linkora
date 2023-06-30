package com.sakethh.linkora

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.screens.home.composables.AddNewFolderDialogBox
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch

class IntentActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setContent {
            val btmSheetState = rememberModalBottomSheetState()
            val coroutineScope = rememberCoroutineScope()
            val noteTextFieldValue = rememberSaveable {
                mutableStateOf("")
            }
            val selectedFolder = rememberSaveable {
                mutableStateOf("")
            }
            val isDataExtractingForTheLink = rememberSaveable {
                mutableStateOf(false)
            }
            val shouldNewFolderDialogBoxAppear = rememberSaveable {
                mutableStateOf(false)
            }
            val foldersData =
                CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllFolders().collectAsState(
                    initial = emptyList()
                ).value
            if (!btmSheetState.isVisible) {
                LaunchedEffect(key1 = Unit) {
                    btmSheetState.show()
                }
            }
            LinkoraTheme {
                ModalBottomSheet(onDismissRequest = {
                    coroutineScope.launch {
                        if (btmSheetState.isVisible) {
                            btmSheetState.hide()
                        }
                    }
                }, sheetState = btmSheetState) {
                    Scaffold(bottomBar = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondary)
                        ) {
                            Spacer(modifier = Modifier.width(15.dp))
                            Column() {
                                Text(
                                    text = "Selected folder",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 24.sp,
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = selectedFolder.value,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(0.65f)
                                )
                            }
                            Button(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 20.dp
                                ),
                                shape = RoundedCornerShape(10.dp),
                                onClick = {

                                }){
                                Text(
                                    text = "Save",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(0.65f)
                                )
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                        }
                    }) {
                        LazyColumn(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "Save new link",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(start = 20.dp)
                                )
                            }
                            item {
                                OutlinedTextField(
                                    readOnly = isDataExtractingForTheLink.value,
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 15.dp
                                    ),
                                    label = {
                                        Text(
                                            text = "Note for why you're saving this link",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 12.sp
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.titleSmall,
                                    shape = RoundedCornerShape(5.dp),
                                    value = noteTextFieldValue.value,
                                    onValueChange = {
                                        noteTextFieldValue.value = it
                                    })
                            }
                            item {
                                Text(
                                    text = "Save in:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(top = 30.dp, start = 20.dp)
                                )
                            }
                            item {
                                OutlinedButton(
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        bottom = 20.dp
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    onClick = {
                                        shouldNewFolderDialogBoxAppear.value = true
                                    }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .requiredHeight(75.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxHeight(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.CreateNewFolder,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(20.dp)
                                                    .size(28.dp)
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(0.80f),
                                            verticalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Text(
                                                text = "Create a new folder",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                            items(foldersData) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                selectedFolder.value = it.folderName
                                            }
                                            .fillMaxWidth()
                                            .requiredHeight(75.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxHeight(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            androidx.compose.material3.Icon(
                                                imageVector = Icons.Outlined.Folder,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(20.dp)
                                                    .size(28.dp)
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(0.80f),
                                            verticalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Text(
                                                text = it.folderName,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Divider(
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(start = 25.dp, end = 25.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                AddNewFolderDialogBox(
                    coroutineScope = coroutineScope,
                    shouldDialogBoxAppear = shouldNewFolderDialogBoxAppear
                )
            }
        }
    }
}