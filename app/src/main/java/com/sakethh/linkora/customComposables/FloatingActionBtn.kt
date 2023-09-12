package com.sakethh.linkora.customComposables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingActionBtn(
    newLinkBottomModalSheetState: SheetState,
    shouldBtmSheetForNewLinkAdditionBeEnabled: MutableState<Boolean>,
    shouldScreenTransparencyDecreasedBoxVisible: MutableState<Boolean>,
    shouldDialogForNewFolderAppear: MutableState<Boolean>,
    shouldDialogForNewLinkAppear: MutableState<Boolean>,
    isMainFabRotated: MutableState<Boolean>,
    rotationAnimation: Animatable<Float, AnimationVector1D>,
) {
    val currentIconForMainFAB = remember(isMainFabRotated.value) {
        mutableStateOf(
            if (isMainFabRotated.value) {
                Icons.Default.AddLink
            } else {
                Icons.Default.Add
            }
        )
    }
    val coroutineScope = rememberCoroutineScope()
    if (SettingsScreenVM.Settings.isBtmSheetEnabledForSavingLinks.value) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.padding(
                bottom = 82.dp
            )
        ) {
            androidx.compose.material3.FloatingActionButton(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                onClick = {
                    coroutineScope.launch {
                        kotlinx.coroutines.awaitAll(
                            async {
                                newLinkBottomModalSheetState.expand()
                            },
                            async {
                                shouldBtmSheetForNewLinkAdditionBeEnabled.value = true
                            })
                    }
                }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.AddLink,
                    contentDescription = null
                )
            }
        }
    } else {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.padding(
                bottom = 82.dp
            )
        ) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.End)
            ) {
                if (isMainFabRotated.value) {
                    AnimatedVisibility(
                        visible = isMainFabRotated.value,
                        enter = androidx.compose.animation.fadeIn(
                            androidx.compose.animation.core.tween(
                                200
                            )
                        ),
                        exit = androidx.compose.animation.fadeOut(
                            androidx.compose.animation.core.tween(
                                200
                            )
                        )
                    ) {
                        androidx.compose.material3.Text(
                            text = "Create new folder",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = androidx.compose.ui.Modifier.padding(
                                top = 20.dp,
                                end = 15.dp
                            )
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isMainFabRotated.value,
                    enter = androidx.compose.animation.scaleIn(
                        animationSpec = androidx.compose.animation.core.tween(
                            300
                        )
                    ),
                    exit = androidx.compose.animation.scaleOut(
                        androidx.compose.animation.core.tween(300)
                    )
                ) {
                    androidx.compose.material3.FloatingActionButton(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                            10.dp
                        ), onClick = {
                            shouldScreenTransparencyDecreasedBoxVisible.value = false
                            shouldDialogForNewFolderAppear.value = true
                        }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.CreateNewFolder,
                            contentDescription = null
                        )
                    }
                }

            }
            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(
                    15.dp
                )
            )
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.End)
            ) {
                if (isMainFabRotated.value) {
                    AnimatedVisibility(
                        visible = isMainFabRotated.value,
                        enter = androidx.compose.animation.fadeIn(
                            androidx.compose.animation.core.tween(
                                200
                            )
                        ),
                        exit = androidx.compose.animation.fadeOut(
                            androidx.compose.animation.core.tween(
                                200
                            )
                        )
                    ) {
                        androidx.compose.material3.Text(
                            text = "Add new link",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = androidx.compose.ui.Modifier.padding(
                                top = 20.dp,
                                end = 15.dp
                            )
                        )
                    }
                }
                androidx.compose.material3.FloatingActionButton(modifier = androidx.compose.ui.Modifier.rotate(
                    rotationAnimation.value
                ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    onClick = {
                        if (isMainFabRotated.value) {
                            shouldScreenTransparencyDecreasedBoxVisible.value = false
                            shouldDialogForNewLinkAppear.value = true
                            isMainFabRotated.value = false
                        } else {
                            coroutineScope.launch {
                                kotlinx.coroutines.awaitAll(async {
                                    rotationAnimation.animateTo(
                                        360f,
                                        animationSpec = androidx.compose.animation.core.tween(300)
                                    )
                                }, async {
                                    shouldScreenTransparencyDecreasedBoxVisible.value =
                                        true
                                    kotlinx.coroutines.delay(10L)
                                    isMainFabRotated.value = true
                                })
                            }.invokeOnCompletion {
                                coroutineScope.launch {
                                    rotationAnimation.snapTo(0f)
                                }
                            }
                        }
                    }) {
                    androidx.compose.material3.Icon(
                        imageVector = currentIconForMainFAB.value,
                        contentDescription = null
                    )
                }
            }
        }
    }
}