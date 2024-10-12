package com.sakethh.linkora.ui.screens.settings.specific.language

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.LocalizedStrings.appLanguage
import com.sakethh.linkora.LocalizedStrings.availableLanguages
import com.sakethh.linkora.LocalizedStrings.language
import com.sakethh.linkora.LocalizedStrings.resetAppLanguage
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.preferredAppLanguageCode
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.preferredAppLanguageName
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.readSettingPreferenceValue
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionCheckerDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LanguageSettingsScreen(
    navController: NavController,
    customWebTab: CustomWebTab
) {
    val context = LocalContext.current
    val languageSettingsScreenVM: LanguageSettingsScreenVM = hiltViewModel()
    LaunchedEffect(key1 = Unit) {
        languageSettingsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    val isLanguageSelectionBtmSheetVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val currentlySelectedLanguageCode = rememberSaveable {
        mutableStateOf("")
    }
    val currentlySelectedLanguageName = rememberSaveable {
        mutableStateOf("")
    }
    val currentlySelectedLanguageContributionLink = rememberSaveable {
        mutableStateOf("")
    }
    val remotelyAvailableLanguages =
        languageSettingsScreenVM.remotelyAvailableLanguages.collectAsStateWithLifecycle().value
    val doesRemoteLanguagePackExistsLocallyForTheSelectedLanguage = rememberSaveable {
        mutableStateOf(false)
    }
    val isSelectedLanguageAvailableOnlyRemotely = rememberSaveable {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    val isRetrieveLanguageInfoFABTriggered = rememberSaveable {
        mutableStateOf(false)
    }
    val localUriHandler = LocalUriHandler.current
    SpecificScreenScaffold(
        topAppBarText = language.value,
        navController = navController,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                onClick = {
                isRetrieveLanguageInfoFABTriggered.value = true
                languageSettingsScreenVM.onClick(
                    LanguageSettingsScreenUIEvent.RetrieveRemoteLanguagesInfo(
                        context
                    )
                )
            }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "")
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = LocalizedStrings.retrieveLanguageInfoFromServer.value,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
        }
    ) { paddingValues, topAppBarScrollBehaviour ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
                .navigationBarsPadding()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
            item {
                Text(
                    text = appLanguage.value,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            item {
                Text(
                    text = preferredAppLanguageName.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )
            }
            item {
                Card(
                    border = BorderStroke(
                        1.dp,
                        contentColorFor(MaterialTheme.colorScheme.surface)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(
                                top = 10.dp, bottom = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(
                                        start = 10.dp, end = 10.dp
                                    )
                            )
                        }
                        Text(
                            text = if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value)
                                LocalizedStrings.displayingRemoteStrings.value else
                                LocalizedStrings.displayingCompiledStrings.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(end = 10.dp)
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    if (preferredAppLanguageCode.value != "en") {
                        FilledTonalButton(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, bottom = 15.dp)
                            .pulsateEffect(), onClick = {
                            languageSettingsScreenVM.onClick(
                                LanguageSettingsScreenUIEvent.ResetAppLanguage(context)
                            )
                        }) {
                            Text(
                                text = resetAppLanguage.value,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }

            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = availableLanguages.value,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
            items(languageSettingsScreenVM.compiledLanguages) {
                LanguageUIComponent(
                    onClick = {
                        isRetrieveLanguageInfoFABTriggered.value = false
                        coroutineScope.launch {
                            isSelectedLanguageAvailableOnlyRemotely.value = false
                            currentlySelectedLanguageCode.value = it.languageCode
                            currentlySelectedLanguageName.value = it.languageName
                            currentlySelectedLanguageContributionLink.value =
                                remotelyAvailableLanguages.find { remoteLanguage -> it.languageCode == remoteLanguage.languageCode }?.contributionLink
                                    ?: ""
                            async {
                                doesRemoteLanguagePackExistsLocallyForTheSelectedLanguage.value =
                                    languageSettingsScreenVM.translationsRepo.doesStringsPackForThisLanguageExists(
                                        currentlySelectedLanguageCode.value
                                    )
                            }.await()
                            linkoraLog(
                                doesRemoteLanguagePackExistsLocallyForTheSelectedLanguage.value.toString()
                            )
                            isLanguageSelectionBtmSheetVisible.value =
                                !isLanguageSelectionBtmSheetVisible.value
                        }
                    },
                    text = it.languageName,
                    isRemoteLanguage = false,
                    localizationStatus = (if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value)
                        remotelyAvailableLanguages.find { language -> language.languageCode == it.languageCode }?.localizedStringsCount
                            ?: 0 else it.localizedStringsCount).toString() + "/" + (if (!SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) SettingsPreference.totalLocalAppStrings.intValue else SettingsPreference.totalRemoteStrings.intValue.toString()) + " " + LocalizedStrings.stringsLocalized.value,
                    localizationStatusFraction = (if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) remotelyAvailableLanguages.find { lang -> lang.languageCode == it.languageCode }?.localizedStringsCount
                        ?: 0 else it.localizedStringsCount.toFloat()).toFloat() / (if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) SettingsPreference.totalRemoteStrings.intValue else SettingsPreference.totalLocalAppStrings.intValue).toFloat(),
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            items(remotelyAvailableLanguages.filterNot { remoteLanguage ->
                languageSettingsScreenVM.compiledLanguages.any { remoteLanguage.languageCode == it.languageCode }
            }) {
                LanguageUIComponent(
                    onClick = {
                        isRetrieveLanguageInfoFABTriggered.value = false
                        isSelectedLanguageAvailableOnlyRemotely.value = true
                        coroutineScope.launch {
                            currentlySelectedLanguageCode.value = it.languageCode
                            currentlySelectedLanguageName.value = it.languageName
                            currentlySelectedLanguageContributionLink.value = it.contributionLink
                            async {
                                doesRemoteLanguagePackExistsLocallyForTheSelectedLanguage.value =
                                    languageSettingsScreenVM.translationsRepo.doesStringsPackForThisLanguageExists(
                                        currentlySelectedLanguageCode.value
                                    )
                            }.await()
                            linkoraLog(
                                doesRemoteLanguagePackExistsLocallyForTheSelectedLanguage.value.toString()
                            )
                            isLanguageSelectionBtmSheetVisible.value =
                                !isLanguageSelectionBtmSheetVisible.value
                        }
                    },
                    text = it.languageName,
                    isRemoteLanguage = true,
                    localizationStatus = it.localizedStringsCount.toString() + "/" + SettingsPreference.totalLocalAppStrings.intValue + " ${LocalizedStrings.stringsLocalized.value}",
                    localizationStatusFraction = it.localizedStringsCount.toFloat() / SettingsPreference.totalLocalAppStrings.intValue.toFloat(),
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            item {
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }
    if (isLanguageSelectionBtmSheetVisible.value) {
        ModalBottomSheet(onDismissRequest = {
            isLanguageSelectionBtmSheetVisible.value =
                !isLanguageSelectionBtmSheetVisible.value
        }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = currentlySelectedLanguageName.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp, modifier = Modifier.padding(start = 15.dp, bottom = 7.5.dp)
                )
                if (doesRemoteLanguagePackExistsLocallyForTheSelectedLanguage.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = {
                                languageSettingsScreenVM.onClick(
                                    LanguageSettingsScreenUIEvent.UseStringsFetchedFromTheServer(
                                        context = context,
                                        languageCode = currentlySelectedLanguageCode.value,
                                        languageName = currentlySelectedLanguageName.value
                                    )
                                )
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }, indication = null, interactionSource = remember {
                                MutableInteractionSource()
                            })
                            .pulsateEffect()
                            .fillMaxWidth()
                            .padding(top = 7.5.dp, bottom = 7.5.dp, start = 10.dp, end = 15.dp)
                    ) {
                        FilledTonalIconButton(
                            modifier = Modifier.pulsateEffect(),
                            onClick = {
                                languageSettingsScreenVM.onClick(
                                    LanguageSettingsScreenUIEvent.UseStringsFetchedFromTheServer(
                                        context = context,
                                        languageCode = currentlySelectedLanguageCode.value,
                                        languageName = currentlySelectedLanguageName.value
                                    )
                                )
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }) {
                            Icon(imageVector = Icons.Default.Cloud, contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = LocalizedStrings.loadServerStrings.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
                if (!isSelectedLanguageAvailableOnlyRemotely.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = {
                                languageSettingsScreenVM.onClick(
                                    LanguageSettingsScreenUIEvent.UseCompiledStrings(
                                        context = context,
                                        languageCode = currentlySelectedLanguageCode.value,
                                        languageName = currentlySelectedLanguageName.value
                                    )
                                )
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }, indication = null, interactionSource = remember {
                                MutableInteractionSource()
                            })
                            .pulsateEffect()
                            .fillMaxWidth()
                            .padding(top = 7.5.dp, bottom = 7.5.dp, start = 10.dp, end = 15.dp)
                    ) {
                        FilledTonalIconButton(
                            modifier = Modifier.pulsateEffect(),
                            onClick = {
                                languageSettingsScreenVM.onClick(
                                    LanguageSettingsScreenUIEvent.UseCompiledStrings(
                                        context = context,
                                        languageCode = currentlySelectedLanguageCode.value,
                                        languageName = currentlySelectedLanguageName.value
                                    )
                                )
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = LocalizedStrings.loadCompiledStrings.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = {
                            linkoraLog(currentlySelectedLanguageCode.value)
                            languageSettingsScreenVM.onClick(
                                LanguageSettingsScreenUIEvent.DownloadLatestLanguageStrings(
                                    languageCode = currentlySelectedLanguageCode.value,
                                    languageName = currentlySelectedLanguageName.value,
                                    context
                                )
                            )
                            isLanguageSelectionBtmSheetVisible.value =
                                !isLanguageSelectionBtmSheetVisible.value
                        }, indication = null, interactionSource = remember {
                            MutableInteractionSource()
                        })
                        .pulsateEffect()
                        .fillMaxWidth()
                        .padding(top = 7.5.dp, bottom = 7.5.dp, start = 10.dp, end = 15.dp)
                ) {
                    FilledTonalIconButton(
                        modifier = Modifier.pulsateEffect(),
                        onClick = {
                            languageSettingsScreenVM.onClick(
                                LanguageSettingsScreenUIEvent.DownloadLatestLanguageStrings(
                                    languageCode = currentlySelectedLanguageCode.value,
                                    languageName = currentlySelectedLanguageName.value,
                                    context
                                )
                            )
                            isLanguageSelectionBtmSheetVisible.value =
                                !isLanguageSelectionBtmSheetVisible.value
                        }) {
                        Icon(
                            imageVector = Icons.Default.DownloadForOffline,
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = LocalizedStrings.updateRemoteLanguageStrings.value,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
                if (doesRemoteLanguagePackExistsLocallyForTheSelectedLanguage.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = {
                                languageSettingsScreenVM.onClick(
                                    LanguageSettingsScreenUIEvent.DeleteLanguageStrings(
                                        languageName = currentlySelectedLanguageName.value,
                                        languageCode = currentlySelectedLanguageCode.value,
                                        context = context
                                    )
                                )
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }, indication = null, interactionSource = remember {
                                MutableInteractionSource()
                            })
                            .pulsateEffect()
                            .fillMaxWidth()
                            .padding(top = 7.5.dp, bottom = 7.5.dp, start = 10.dp, end = 15.dp)
                    ) {
                        FilledTonalIconButton(
                            modifier = Modifier.pulsateEffect(),
                            onClick = {
                                languageSettingsScreenVM.onClick(
                                    LanguageSettingsScreenUIEvent.DeleteLanguageStrings(
                                        languageName = currentlySelectedLanguageName.value,
                                        context = context,
                                        languageCode = currentlySelectedLanguageCode.value
                                    )
                                )
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = ""
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = LocalizedStrings.removeLanguageStrings.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }

                if (currentlySelectedLanguageContributionLink.value.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                localUriHandler.openUri(currentlySelectedLanguageContributionLink.value)
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }, indication = null, interactionSource = remember {
                                MutableInteractionSource()
                            })
                            .pulsateEffect()
                            .padding(start = 10.dp, top = 7.5.dp, bottom = 7.5.dp, end = 15.dp)
                    ) {
                        FilledTonalIconButton(
                            modifier = Modifier.pulsateEffect(),
                            onClick = {
                                localUriHandler.openUri(currentlySelectedLanguageContributionLink.value)
                                isLanguageSelectionBtmSheetVisible.value =
                                    !isLanguageSelectionBtmSheetVisible.value
                            }) {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = LocalizedStrings.helpImproveLanguageStrings.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(22.5.dp))
            }
        }
    }
    SettingsNewVersionCheckerDialogBox(
        text = if (isRetrieveLanguageInfoFABTriggered.value)
            LocalizedStrings.syncingLanguageDetailsThisMayTakeSomeTime.value else LocalizedStrings.syncingTranslationsForCurrentlySelectedLanguage.value.replace(
            "\$\$\$\$",
            currentlySelectedLanguageName.value
        ),
        shouldDialogBoxAppear = languageSettingsScreenVM.shouldRequestingDataFromServerDialogBoxShouldAppear
    )
    LaunchedEffect(key1 = Unit) {
        preferredAppLanguageName.value = readSettingPreferenceValue(
            stringPreferencesKey(SettingsPreferences.APP_LANGUAGE_NAME.name),
            context.dataStore
        ) ?: "English"

        preferredAppLanguageCode.value = readSettingPreferenceValue(
            stringPreferencesKey(SettingsPreferences.APP_LANGUAGE_CODE.name),
            context.dataStore
        ) ?: "en"
    }
}

@Composable
private fun LanguageUIComponent(
    onClick: () -> Unit,
    text: String,
    isRemoteLanguage: Boolean,
    localizationStatus: String,
    localizationStatusFraction: Float
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = {
                onClick()
            })
            .pulsateEffect(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isRemoteLanguage) Icons.Default.Cloud else Icons.Default.Code,
                    contentDescription = "",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp
                )
            }
            LinearProgressIndicator(
                progress = { localizationStatusFraction }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            Text(
                text = localizationStatus,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                modifier = Modifier.pulsateEffect(),
                onClick = {
                    onClick()
                }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = ""
                )
            }
        }
    }
}