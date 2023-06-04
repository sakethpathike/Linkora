package com.sakethh.linkora.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.home.composables.AddNewLinkDialogBox
import com.sakethh.linkora.screens.home.composables.GeneralCard
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val isMainFabRotated = rememberSaveable() {
        mutableStateOf(false)
    }
    val rotationAnimation = remember {
        Animatable(0f)
    }
    val coroutineScope = rememberCoroutineScope()
    val currentIconForMainFAB = remember(isMainFabRotated.value) {
        mutableStateOf(
            if (isMainFabRotated.value) {
                Icons.Default.AddLink
            } else {
                Icons.Default.Add
            }
        )
    }
    val shouldDialogForNewLinkEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(floatingActionButton = {
            Column(modifier = Modifier.padding(bottom = 60.dp)) {
                if (isMainFabRotated.value) {
                    Row() {
                        Text(
                            text = "Create new folder",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 30.dp)
                        )
                        Spacer(modifier = Modifier.width(25.dp))
                        FloatingActionButton(
                            shape = RoundedCornerShape(10.dp),
                            onClick = {

                            }) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = null
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isMainFabRotated.value) {
                        Text(
                            text = "Add new link",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 30.dp)
                        )
                        Spacer(modifier = Modifier.width(25.dp))
                    }
                    FloatingActionButton(
                        modifier = Modifier.rotate(rotationAnimation.value),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            if (isMainFabRotated.value) {
                                shouldDialogForNewLinkEnabled.value = true
                            } else {
                                coroutineScope.launch {
                                    awaitAll(async {
                                        rotationAnimation.animateTo(
                                            360f,
                                            animationSpec = tween(700)
                                        )
                                    }, async { isMainFabRotated.value = true })
                                }.invokeOnCompletion {
                                    coroutineScope.launch {
                                        rotationAnimation.snapTo(0f)
                                    }
                                }
                            }
                        }) {
                        Icon(imageVector = currentIconForMainFAB.value, contentDescription = null)
                    }
                }
            }
        }, floatingActionButtonPosition = FabPosition.End) {
            val homeScreenVM: HomeScreenVM = viewModel()
            val currentPhaseOfTheDay =
                rememberSaveable(inputs = arrayOf(homeScreenVM.currentPhaseOfTheDay.value)) {
                    homeScreenVM.currentPhaseOfTheDay.value
                }
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                item {
                    Text(
                        text = currentPhaseOfTheDay,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 30.dp)
                    )
                }

                item {
                    Text(
                        text = "Recent Saves",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 45.dp)
                    )
                }
                item {
                    LazyRow(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        item {
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        items(8) {
                            GeneralCard(
                                title = "",
                                webBaseURL = "",
                                imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }
            }
        }

        AddNewLinkDialogBox(shouldDialogBoxEnabled = shouldDialogForNewLinkEnabled)
    }
    BackHandler {
        if (isMainFabRotated.value) {
            coroutineScope.launch {
                rotationAnimation.animateTo(-180f, animationSpec = tween(300))
                isMainFabRotated.value = false
                rotationAnimation.animateTo(-180f, animationSpec = tween(300))
                rotationAnimation.snapTo(0f)
            }
        }
    }
}