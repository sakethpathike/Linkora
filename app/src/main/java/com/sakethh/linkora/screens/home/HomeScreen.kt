package com.sakethh.linkora.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.home.composables.AddNewFolderDialogBox
import com.sakethh.linkora.screens.home.composables.AddNewLinkDialogBox
import com.sakethh.linkora.screens.home.composables.GeneralCard
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val isMainFabRotated = rememberSaveable() {
        mutableStateOf(false)
    }
    val rotationAnimation = remember {
        Animatable(0f)
    }
    val shouldScreenTransparencyDecreased = rememberSaveable() {
        mutableStateOf(false)
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
    val shouldDialogForNewFolderEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    if (shouldDialogForNewFolderEnabled.value || shouldDialogForNewLinkEnabled.value) {
        shouldScreenTransparencyDecreased.value = false
        isMainFabRotated.value = false
    }
    LinkoraTheme {
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            floatingActionButton = {
                Column(modifier = Modifier.padding(bottom = 60.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        if (isMainFabRotated.value) {
                            AnimatedVisibility(
                                visible = isMainFabRotated.value,
                                enter = fadeIn(tween(200)),
                                exit = fadeOut(tween(200))
                            ) {
                                Text(
                                    text = "Create new folder",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 20.dp, end = 15.dp)
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = isMainFabRotated.value,
                            enter = scaleIn(animationSpec = tween(300)),
                            exit = scaleOut(
                                tween(300)
                            )
                        ) {
                            FloatingActionButton(
                                shape = RoundedCornerShape(10.dp),
                                onClick = {
                                    shouldScreenTransparencyDecreased.value = false
                                    shouldDialogForNewFolderEnabled.value = true
                                }) {
                                Icon(
                                    imageVector = Icons.Default.CreateNewFolder,
                                    contentDescription = null
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        if (isMainFabRotated.value) {
                            AnimatedVisibility(
                                visible = isMainFabRotated.value,
                                enter = fadeIn(tween(200)),
                                exit = fadeOut(tween(200))
                            ) {
                                Text(
                                    text = "Add new link",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 20.dp, end = 15.dp)
                                )
                            }
                        }
                        FloatingActionButton(
                            modifier = Modifier.rotate(rotationAnimation.value),
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                if (isMainFabRotated.value) {
                                    shouldScreenTransparencyDecreased.value = false
                                    shouldDialogForNewLinkEnabled.value = true
                                } else {
                                    coroutineScope.launch {
                                        awaitAll(async {
                                            rotationAnimation.animateTo(
                                                360f,
                                                animationSpec = tween(300)
                                            )
                                        }, async {
                                            shouldScreenTransparencyDecreased.value = true
                                            delay(10L)
                                            isMainFabRotated.value = true
                                        })
                                    }.invokeOnCompletion {
                                        coroutineScope.launch {
                                            rotationAnimation.snapTo(0f)
                                        }
                                    }
                                }
                            }) {
                            Icon(
                                imageVector = currentIconForMainFAB.value,
                                contentDescription = null
                            )
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
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
                                title = "ergferg",
                                webBaseURL = "regrgttrg",
                                imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }
            }
            if (shouldScreenTransparencyDecreased.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(0.85f))
                        .clickable {
                            shouldScreenTransparencyDecreased.value = false
                            coroutineScope
                                .launch {
                                    awaitAll(async {
                                        rotationAnimation.animateTo(
                                            -360f,
                                            animationSpec = tween(300)
                                        )
                                    }, async { isMainFabRotated.value = false })
                                }
                                .invokeOnCompletion {
                                    coroutineScope.launch {
                                        rotationAnimation.snapTo(0f)
                                    }
                                }
                        })
            }
        }

        AddNewLinkDialogBox(shouldDialogBoxEnabled = shouldDialogForNewLinkEnabled)
        AddNewFolderDialogBox(shouldDialogBoxEnabled = shouldDialogForNewFolderEnabled)
    }

    BackHandler {
        if (isMainFabRotated.value) {
            shouldScreenTransparencyDecreased.value = false
            coroutineScope.launch {
                awaitAll(async {
                    rotationAnimation.animateTo(
                        -360f,
                        animationSpec = tween(300)
                    )
                }, async {
                    delay(10L)
                    isMainFabRotated.value = false
                })
            }.invokeOnCompletion {
                coroutineScope.launch {
                    rotationAnimation.snapTo(0f)
                }
            }
        }
    }
}