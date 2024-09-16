package com.sakethh.linkora.ui.commonComposables

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.LocalizedStrings
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class FloatingActionBtnParam @OptIn(ExperimentalMaterial3Api::class) constructor(
    val newLinkBottomModalSheetState: SheetState,
    val shouldBtmSheetForNewLinkAdditionBeEnabled: MutableState<Boolean>,
    val shouldScreenTransparencyDecreasedBoxVisible: MutableState<Boolean>,
    val shouldDialogForNewFolderAppear: MutableState<Boolean>,
    val shouldDialogForNewLinkAppear: MutableState<Boolean>,
    val isMainFabRotated: MutableState<Boolean>,
    val rotationAnimation: Animatable<Float, AnimationVector1D>,
    val inASpecificScreen: Boolean,
)

@Composable
fun FloatingActionBtn(
    floatingActionBtnParam: FloatingActionBtnParam
) {
    val currentIconForMainFAB = remember(floatingActionBtnParam.isMainFabRotated.value) {
        mutableStateOf(
            if (floatingActionBtnParam.isMainFabRotated.value) {
                Icons.Default.AddLink
            } else {
                Icons.Default.Add
            }
        )
    }
    val coroutineScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(
            bottom = if (!floatingActionBtnParam.inASpecificScreen) 82.dp else 0.dp
        )
    ) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (floatingActionBtnParam.isMainFabRotated.value) {
                AnimatedVisibility(
                    visible = floatingActionBtnParam.isMainFabRotated.value,
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
                        text = LocalizedStrings.createANewFolder.value,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(
                            end = 15.dp
                        )
                    )
                }
            }
            AnimatedVisibility(
                visible = floatingActionBtnParam.isMainFabRotated.value,
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
                    modifier = Modifier.pulsateEffect(),
                    onClick = {
                        floatingActionBtnParam.shouldScreenTransparencyDecreasedBoxVisible.value =
                            false
                        floatingActionBtnParam.shouldDialogForNewFolderAppear.value = true
                        floatingActionBtnParam.isMainFabRotated.value = false
                    }) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = null
                    )
                }
            }

        }
        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(
                15.dp
            )
        )
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (floatingActionBtnParam.isMainFabRotated.value) {
                AnimatedVisibility(
                    visible = floatingActionBtnParam.isMainFabRotated.value,
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
                        text = LocalizedStrings.addANewLink.value,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(
                            end = 15.dp
                        )
                    )
                }
            }
            androidx.compose.material3.FloatingActionButton(
                modifier = Modifier
                    .rotate(
                        floatingActionBtnParam.rotationAnimation.value
                    )
                    .pulsateEffect(),
                onClick = {
                    if (floatingActionBtnParam.isMainFabRotated.value) {
                        floatingActionBtnParam.shouldScreenTransparencyDecreasedBoxVisible.value =
                            false
                        floatingActionBtnParam.shouldDialogForNewLinkAppear.value = true
                        floatingActionBtnParam.isMainFabRotated.value = false
                    } else {
                        coroutineScope.launch {
                            kotlinx.coroutines.awaitAll(async {
                                floatingActionBtnParam.rotationAnimation.animateTo(
                                    360f,
                                    animationSpec = androidx.compose.animation.core.tween(300)
                                )
                            }, async {
                                floatingActionBtnParam.shouldScreenTransparencyDecreasedBoxVisible.value =
                                    true
                                kotlinx.coroutines.delay(10L)
                                floatingActionBtnParam.isMainFabRotated.value = true
                            })
                        }.invokeOnCompletion {
                            coroutineScope.launch {
                                floatingActionBtnParam.rotationAnimation.snapTo(0f)
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