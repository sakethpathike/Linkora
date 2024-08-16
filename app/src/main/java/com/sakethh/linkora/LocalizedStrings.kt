package com.sakethh.linkora

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.localization.language.translations.TranslationsRepo
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.utils.ifNullOrBlank
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

object LocalizedStrings : ViewModel() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface TranslationRepoInstance {
        fun getTranslationRepo(): TranslationsRepo
    }

    private val translationsRepo =
        EntryPoints.get(this, TranslationRepoInstance::class.java).getTranslationRepo()

    private val _general = mutableStateOf("")
    val general = _general

    private val _useLanguageStringsFetchedFromTheServer = mutableStateOf("")
    val useLanguageStringsFetchedFromTheServer = _useLanguageStringsFetchedFromTheServer

    private val _useLanguageStringsFetchedFromTheServerDesc = mutableStateOf("")
    val useLanguageStringsFetchedFromTheServerDesc = _useLanguageStringsFetchedFromTheServerDesc

    private val _userAgentDesc = mutableStateOf("")
    val userAgentDesc = _userAgentDesc

    private val _userAgent = mutableStateOf("")
    val userAgent = _userAgent

    private val _refreshingLinks = mutableStateOf("")
    val refreshingLinks = _refreshingLinks

    private val _workManagerDesc = mutableStateOf("")
    val workManagerDesc = _workManagerDesc

    private val _of = mutableStateOf("")
    val of = _of

    private val _linksRefreshed = mutableStateOf("")
    val linksRefreshed = _linksRefreshed

    private val _refreshingLinksInfo = mutableStateOf("")
    val refreshingLinksInfo = _refreshingLinksInfo

    private val _refreshAllLinksTitlesAndImages = mutableStateOf("")
    val refreshAllLinksTitlesAndImages = _refreshAllLinksTitlesAndImages

    private val _refreshAllLinksTitlesAndImagesDesc = mutableStateOf("")
    val refreshAllLinksTitlesAndImagesDesc = _refreshAllLinksTitlesAndImagesDesc

    private val _titleCopiedToClipboard = mutableStateOf("")
    val titleCopiedToClipboard = _titleCopiedToClipboard

    private val _viewNote = mutableStateOf("")
    val viewNote = _viewNote

    private val _rename = mutableStateOf("")
    val rename = _rename

    private val _refreshingTitleAndImage = mutableStateOf("")
    val refreshingTitleAndImage = _refreshingTitleAndImage

    private val _refreshImageAndTitle = mutableStateOf("")
    val refreshImageAndTitle = _refreshImageAndTitle

    private val _unarchive = mutableStateOf("")
    val unarchive = _unarchive

    private val _deleteTheNote = mutableStateOf("")
    val deleteTheNote = _deleteTheNote

    private val _deleteFolder = mutableStateOf("")
    val deleteFolder = _deleteFolder

    private val _deleteLink = mutableStateOf("")
    val deleteLink = _deleteLink

    private val _savedNote = mutableStateOf("")
    val savedNote = _savedNote

    private val _noteCopiedToClipboard = mutableStateOf("")
    val noteCopiedToClipboard = _noteCopiedToClipboard

    private val _youDidNotAddNoteForThis = mutableStateOf("")
    val youDidNotAddNoteForThis = _youDidNotAddNoteForThis


    // TODO()

    private val _sortFoldersBy = mutableStateOf("")
    val sortFoldersBy =
        _sortFoldersBy

    private val _sortHistoryLinksBy = mutableStateOf("")
    val sortHistoryLinksBy =
        _sortHistoryLinksBy

    private val _sortBy = mutableStateOf("")
    val sortBy =
        _sortBy

    private val _sortSavedLinksBy = mutableStateOf("")
    val sortSavedLinksBy =
        _sortSavedLinksBy

    private val _sortImportantLinksBy = mutableStateOf("")
    val sortImportantLinksBy =
        _sortImportantLinksBy

    private val _sortBasedOn = mutableStateOf("")
    val sortBasedOn =
        _sortBasedOn

    private val _folders = mutableStateOf("")
    val folders =
        _folders

    private val _addANewLinkInImportantLinks = mutableStateOf("")
    val addANewLinkInImportantLinks =
        _addANewLinkInImportantLinks

    private val _addANewLinkInSavedLinks = mutableStateOf("")
    val addANewLinkInSavedLinks =
        _addANewLinkInSavedLinks

    private val _addANewLinkIn = mutableStateOf("")
    val addANewLinkIn =
        _addANewLinkIn

    private val _addANewLink = mutableStateOf("")
    val addANewLink =
        _addANewLink

    private val _linkAddress = mutableStateOf("")
    val linkAddress =
        _linkAddress

    private val _titleForTheLink = mutableStateOf("")
    val titleForTheLink =
        _titleForTheLink

    private val _noteForSavingTheLink = mutableStateOf("")
    val noteForSavingTheLink =
        _noteForSavingTheLink

    private val _titleWillBeAutomaticallyDetected = mutableStateOf("")
    val titleWillBeAutomaticallyDetected =
        _titleWillBeAutomaticallyDetected

    private val _addIn = mutableStateOf("")
    val addIn = _addIn

    private val _savedLinks = mutableStateOf("")
    val savedLinks = _savedLinks

    private val _importantLinks = mutableStateOf("")
    val importantLinks = _importantLinks

    private val _forceAutoDetectTitle = mutableStateOf("")
    val forceAutoDetectTitle = _forceAutoDetectTitle

    private val _cancel = mutableStateOf("")
    val cancel = _cancel

    private val _save = mutableStateOf("")
    val save = _save

    private val _thisFolderHasNoSubfolders = mutableStateOf("")
    val thisFolderHasNoSubfolders = _thisFolderHasNoSubfolders

    private val _saveInThisFolder = mutableStateOf("")
    val saveInThisFolder = _saveInThisFolder

    private val _addANewPanelToTheShelf = mutableStateOf("")
    val addANewPanelToTheShelf = _addANewPanelToTheShelf

    private val _panelName = mutableStateOf("")
    val panelName = _panelName

    private val _addNewPanel = mutableStateOf("")
    val addNewPanel = _addNewPanel

    private val _folderNameCannnotBeEmpty = mutableStateOf("")
    val folderNameCannnotBeEmpty = _folderNameCannnotBeEmpty

    private val _folderName = mutableStateOf("")
    val folderName = _folderName

    private val _noteForCreatingTheFolder = mutableStateOf("")
    val noteForCreatingTheFolder = _noteForCreatingTheFolder

    private val _createANewFolderIn = mutableStateOf("")
    val createANewFolderIn = _createANewFolderIn

    private val _createANewFolder = mutableStateOf("")
    val createANewFolder = _createANewFolder

    private val _create = mutableStateOf("")
    val create = _create

    private val _areYouSureWantToDelete = mutableStateOf("")
    val areYouSureWantToDelete = _areYouSureWantToDelete

    private val _permanentlyDeleteThePanel = mutableStateOf("")
    val permanentlyDeleteThePanel = _permanentlyDeleteThePanel

    private val _onceDeletedThisPanelCannotBeRestarted = mutableStateOf("")
    val onceDeletedThisPanelCannotBeRestarted = _onceDeletedThisPanelCannotBeRestarted

    private val _deleteIt = mutableStateOf("")
    val deleteIt = _deleteIt

    private val _thisFolderDeletionWillAlsoDeleteAllItsInternalFolders = mutableStateOf("")
    val thisFolderDeletionWillAlsoDeleteAllItsInternalFolders = _thisFolderDeletionWillAlsoDeleteAllItsInternalFolders

    private val _areYouSureYouWantToDeleteAllSelectedLinks = mutableStateOf("")
    val areYouSureYouWantToDeleteAllSelectedLinks = _areYouSureYouWantToDeleteAllSelectedLinks

    private val _areYouSureYouWantToDeleteTheLink = mutableStateOf("")
    val areYouSureYouWantToDeleteTheLink = _areYouSureYouWantToDeleteTheLink

    private val _areYouSureYouWantToDeleteAllSelectedFolders = mutableStateOf("")
    val areYouSureYouWantToDeleteAllSelectedFolders = _areYouSureYouWantToDeleteAllSelectedFolders

    private val _areYouSureWantToDeleteThe = mutableStateOf("")
    val areYouSureWantToDeleteThe = _areYouSureWantToDeleteThe

    private val _folder = mutableStateOf("")
    val folder = _folder

    private val _areYouSureYouWantToDeleteAllSelectedItems = mutableStateOf("")
    val areYouSureYouWantToDeleteAllSelectedItems = _areYouSureYouWantToDeleteAllSelectedItems

    private val _areYouSureYouWantToDeleteAllFoldersAndLinks = mutableStateOf("")
    val areYouSureYouWantToDeleteAllFoldersAndLinks = _areYouSureYouWantToDeleteAllFoldersAndLinks

    private val _noActivityFoundToHandleIntent = mutableStateOf("")
    val noActivityFoundToHandleIntent = _noActivityFoundToHandleIntent

    private val _linkCopiedToTheClipboard = mutableStateOf("")
    val linkCopiedToTheClipboard = _linkCopiedToTheClipboard

    private val _changePanelName = mutableStateOf("")
    val changePanelName = _changePanelName

    private val _edit = mutableStateOf("")
    val edit = _edit

    private val _newNameForPanel = mutableStateOf("")
    val newNameForPanel = _newNameForPanel

    private val _changeNoteOnly = mutableStateOf("")
    val changeNoteOnly = _changeNoteOnly

    private val _changeBothNameAndNote = mutableStateOf("")
    val changeBothNameAndNote = _changeBothNameAndNote

    private val _titleCannotBeEmpty = mutableStateOf("")
    val titleCannotBeEmpty = _titleCannotBeEmpty

    private val _changeLinkData = mutableStateOf("")
    val changeLinkData = _changeLinkData

    private val _newName = mutableStateOf("")
    val newName = _newName

    private val _newTitle = mutableStateOf("")
    val newTitle = _newTitle

    private val _newNote = mutableStateOf("")
    val newNote = _newNote

    private val _leaveAboveFieldEmptyIfYouDoNotWantToChangeTheNote = mutableStateOf("")
    val leaveAboveFieldEmptyIfYouDoNotWantToChangeTheNote =
        _leaveAboveFieldEmptyIfYouDoNotWantToChangeTheNote

    private val _home = mutableStateOf("")
    val home = _home

    private val _shelfNameAlreadyExists = mutableStateOf("")
    val shelfNameAlreadyExists = _shelfNameAlreadyExists

    private val _newestToOldest = mutableStateOf("")
    val newestToOldest = _newestToOldest

    private val _oldestToNewest = mutableStateOf("")
    val oldestToNewest = _oldestToNewest

    private val _aToZSequence = mutableStateOf("")
    val aToZSequence = _aToZSequence

    private val _ztoASequence = mutableStateOf("")
    val ztoASequence = _ztoASequence

    private val _search = mutableStateOf("")
    val search = _search

    private val _collections = mutableStateOf("")
    val collections = _collections

    private val _settings = mutableStateOf("")
    val settings = _settings

    private val _links = mutableStateOf("")
    val links = _links

    private val _selectedFoldersUnarchivedSuccessfully = mutableStateOf("")
    val selectedFoldersUnarchivedSuccessfully = _selectedFoldersUnarchivedSuccessfully

    private val _selectedLinksDeletedSuccessfully = mutableStateOf("")
    val selectedLinksDeletedSuccessfully = _selectedLinksDeletedSuccessfully

    private val _selectedFoldersDeletedSuccessfully = mutableStateOf("")
    val selectedFoldersDeletedSuccessfully = _selectedFoldersDeletedSuccessfully

    private val _selectedLinksUnarchivedSuccessfully = mutableStateOf("")
    val selectedLinksUnarchivedSuccessfully = _selectedLinksUnarchivedSuccessfully

    private val _linkUnarchivedSuccessfully = mutableStateOf("")
    val linkUnarchivedSuccessfully = _linkUnarchivedSuccessfully

    private val _linkInfoUpdatedSuccessfully = mutableStateOf("")
    val linkInfoUpdatedSuccessfully = _linkInfoUpdatedSuccessfully

    private val _folderInfoUpdatedSuccessfully = mutableStateOf("")
    val folderInfoUpdatedSuccessfully = _folderInfoUpdatedSuccessfully

    private val _archivedLinkDeletedSuccessfully = mutableStateOf("")
    val archivedLinkDeletedSuccessfully = _archivedLinkDeletedSuccessfully

    private val _deletedTheNoteSuccessfully = mutableStateOf("")
    val deletedTheNoteSuccessfully = _deletedTheNoteSuccessfully

    private val _folderUnarchivedSuccessfully = mutableStateOf("")
    val folderUnarchivedSuccessfully = _folderUnarchivedSuccessfully

    private val _noLinksWereArchived = mutableStateOf("")
    val noLinksWereArchived = _noLinksWereArchived

    private val _noFoldersWereArchived = mutableStateOf("")
    val noFoldersWereArchived = _noFoldersWereArchived

    private val _itemsSelected = mutableStateOf("")
    val itemsSelected = _itemsSelected

    private val _archive = mutableStateOf("")
    val archive = _archive

    private val _thisFolderDoesNotContainAnyLinksAddLinksForFurtherUsage = mutableStateOf("")
    val thisFolderDoesNotContainAnyLinksAddLinksForFurtherUsage =
        _thisFolderDoesNotContainAnyLinksAddLinksForFurtherUsage

    private val _noLinksWereFound = mutableStateOf("")
    val noLinksWereFound = _noLinksWereFound

    private val _noImportantLinksWereFound = mutableStateOf("")
    val noImportantLinksWereFound = _noImportantLinksWereFound

    private val _noLinksFoundInThisArchivedFolder = mutableStateOf("")
    val noLinksFoundInThisArchivedFolder = _noLinksFoundInThisArchivedFolder

    private val _deletedTheLinkSuccessfully = mutableStateOf("")
    val deletedTheLinkSuccessfully = _deletedTheLinkSuccessfully

    private val _foldersSelected = mutableStateOf("")
    val foldersSelected = _foldersSelected

    private val _selectAllFolders = mutableStateOf("")
    val selectAllFolders = _selectAllFolders

    private val _selectedFoldersArchivedSuccessfully = mutableStateOf("")
    val selectedFoldersArchivedSuccessfully = _selectedFoldersArchivedSuccessfully

    private val _newLinkAddedToTheFolder = mutableStateOf("")
    val newLinkAddedToTheFolder = _newLinkAddedToTheFolder

    private val _newLinkAddedToImportantLinks = mutableStateOf("")
    val newLinkAddedToImportantLinks = _newLinkAddedToImportantLinks

    private val _newLinkAddedToSavedLinks = mutableStateOf("")
    val newLinkAddedToSavedLinks = _newLinkAddedToSavedLinks

    private val _folderArchivedSuccessfully = mutableStateOf("")
    val folderArchivedSuccessfully = _folderArchivedSuccessfully

    private val _folderCreatedSuccessfully = mutableStateOf("")
    val folderCreatedSuccessfully = _folderCreatedSuccessfully

    private val _deletedTheFolder = mutableStateOf("")
    val deletedTheFolder = _deletedTheFolder

    private val _removedLinkFromImportantLinksSuccessfully = mutableStateOf("")
    val removedLinkFromImportantLinksSuccessfully = _removedLinkFromImportantLinksSuccessfully

    private val _addedLinkToImportantLinks = mutableStateOf("")
    val addedLinkToImportantLinks = _addedLinkToImportantLinks

    private val _welcomeBackToLinkora = mutableStateOf("")
    val welcomeBackToLinkora = _welcomeBackToLinkora

    private val _goodMorning = mutableStateOf("")
    val goodMorning = _goodMorning

    private val _goodAfternoon = mutableStateOf("")
    val goodAfternoon = _goodAfternoon

    private val _goodEvening = mutableStateOf("")
    val goodEvening = _goodEvening

    private val _goodNight = mutableStateOf("")
    val goodNight = _goodNight

    private val _heyHi = mutableStateOf("")
    val heyHi = _heyHi

    private val _defaultShelf = mutableStateOf("")
    val defaultShelf = _defaultShelf

    private val _toAddFoldersIntoThisPanelClickOnThe = mutableStateOf("")
    val toAddFoldersIntoThisPanelClickOnThe = _toAddFoldersIntoThisPanelClickOnThe

    private val _atThe = mutableStateOf("")
    val atThe = _atThe

    private val _bottomOfTheShelf = mutableStateOf("")
    val bottomOfTheShelf = _bottomOfTheShelf

    private val _period = mutableStateOf("")
    val period = _period

    private val _and = mutableStateOf("")
    val and = _and

    private val _canBeAccessedFromThe = mutableStateOf("")
    val canBeAccessedFromThe = _canBeAccessedFromThe

    private val _defaultPanelDesc = mutableStateOf("")
    val defaultPanelDesc = _defaultPanelDesc

    private val _archivedFolders = mutableStateOf("")
    val archivedFolders = _archivedFolders

    private val _archivedLinks = mutableStateOf("")
    val archivedLinks = _archivedLinks

    private val _history = mutableStateOf("")
    val history = _history

    private val _linksFromFolders = mutableStateOf("")
    val linksFromFolders = _linksFromFolders

    private val _searchTitlesToFindLinksAndFolders = mutableStateOf("")
    val searchTitlesToFindLinksAndFolders = _searchTitlesToFindLinksAndFolders

    private val _searchLinkoraRetrieveAllTheLinksYouSaved = mutableStateOf("")
    val searchLinkoraRetrieveAllTheLinksYouSaved = _searchLinkoraRetrieveAllTheLinksYouSaved

    private val _noMatchingItemsFoundTryADifferentSearch = mutableStateOf("")
    val noMatchingItemsFoundTryADifferentSearch = _noMatchingItemsFoundTryADifferentSearch

    private val _fromFolders = mutableStateOf("")
    val fromFolders = _fromFolders

    private val _fromSavedLinks = mutableStateOf("")
    val fromSavedLinks = _fromSavedLinks

    private val _fromImportantLinks = mutableStateOf("")
    val fromImportantLinks = _fromImportantLinks

    private val _linksFromHistory = mutableStateOf("")
    val linksFromHistory = _linksFromHistory

    private val _linksFromArchive = mutableStateOf("")
    val linksFromArchive = _linksFromArchive

    private val _fromArchivedFolders = mutableStateOf("")
    val fromArchivedFolders = _fromArchivedFolders

    private val _noLinksWereFoundInHistory = mutableStateOf("")
    val noLinksWereFoundInHistory = _noLinksWereFoundInHistory

    private val _headsUp = mutableStateOf("")
    val headsUp = _headsUp

    private val _youAlreadyHaveLinksSaved = mutableStateOf("")
    val youAlreadyHaveLinksSaved = _youAlreadyHaveLinksSaved

    private val _exportData = mutableStateOf("")
    val exportData = _exportData

    private val _importDataAndKeepTheExistingData = mutableStateOf("")
    val importDataAndKeepTheExistingData = _importDataAndKeepTheExistingData

    private val _importDataExportAndDeleteTheExistingData = mutableStateOf("")
    val importDataExportAndDeleteTheExistingData = _importDataExportAndDeleteTheExistingData

    private val _importDataAndDeleteTheExistingData = mutableStateOf("")
    val importDataAndDeleteTheExistingData = _importDataAndDeleteTheExistingData

    private val _incompatibleFileType = mutableStateOf("")
    val incompatibleFileType = _incompatibleFileType

    private val _dataConversionFailed = mutableStateOf("")
    val dataConversionFailed = _dataConversionFailed

    private val _selectedFileDoesNotMatchLinkoraSchema = mutableStateOf("")
    val selectedFileDoesNotMatchLinkoraSchema = _selectedFileDoesNotMatchLinkoraSchema

    private val _thereWasAnIssueImportingTheLinks = mutableStateOf("")
    val thereWasAnIssueImportingTheLinks = _thereWasAnIssueImportingTheLinks

    private val _chooseAnotherFile = mutableStateOf("")
    val chooseAnotherFile = _chooseAnotherFile

    private val _permissionDeniedTitle = mutableStateOf("")
    val permissionDeniedTitle = _permissionDeniedTitle

    private val _permissionIsDeniedDesc = mutableStateOf("")
    val permissionIsDeniedDesc = _permissionIsDeniedDesc

    private val _goToSettings = mutableStateOf("")
    val goToSettings = _goToSettings

    private val _retrievingLatestInformation = mutableStateOf("")
    val retrievingLatestInformation = _retrievingLatestInformation

    private val _newUpdateIsAvailable = mutableStateOf("")
    val newUpdateIsAvailable = _newUpdateIsAvailable

    private val _currentVersion = mutableStateOf("")
    val currentVersion = _currentVersion

    private val _latestVersion = mutableStateOf("")
    val latestVersion = _latestVersion

    private val _linkora = mutableStateOf("")
    val linkora = _linkora

    private val _releasePageOnGithub = mutableStateOf("")
    val releasePageOnGithub = _releasePageOnGithub

    private val _redirectToLatestReleasePage = mutableStateOf("")
    val redirectToLatestReleasePage = _redirectToLatestReleasePage

    private val _download = mutableStateOf("")
    val download = _download

    private val _beta = mutableStateOf("")
    val beta = _beta

    private val _language = mutableStateOf("")
    val language = _language

    private val _appLanguage = mutableStateOf("")
    val appLanguage = _appLanguage

    private val _resetAppLanguage = mutableStateOf("")
    val resetAppLanguage = _resetAppLanguage

    private val _availableLanguages = mutableStateOf("")
    val availableLanguages = _availableLanguages

    private val _representsSwitchingToTheRespectiveLanguage = mutableStateOf("")
    val representsSwitchingToTheRespectiveLanguage = _representsSwitchingToTheRespectiveLanguage

    private val _contributingToTheLanguageStrings = mutableStateOf("")
    val contributingToTheLanguageStrings = _contributingToTheLanguageStrings

    private val _about = mutableStateOf("")
    val about = _about

    private val _checkForLatestVersion = mutableStateOf("")
    val checkForLatestVersion = _checkForLatestVersion

    private val _networkError = mutableStateOf("")
    val networkError = _networkError

    private val _isNowAvailable = mutableStateOf("")
    val isNowAvailable = _isNowAvailable

    private val _youAreUsingLatestVersionOfLinkora = mutableStateOf("")
    val youAreUsingLatestVersionOfLinkora = _youAreUsingLatestVersionOfLinkora

    private val _githubDesc = mutableStateOf("")
    val githubDesc = _githubDesc

    private val _github = mutableStateOf("")
    val github = _github

    private val _follow = mutableStateOf("")
    val follow = _follow

    private val _twitterDesc = mutableStateOf("")
    val twitterDesc = _twitterDesc

    private val _twitter = mutableStateOf("")
    val twitter = _twitter

    private val _autoCheckForUpdates = mutableStateOf("")
    val autoCheckForUpdates = _autoCheckForUpdates

    private val _autoCheckForUpdatesDesc = mutableStateOf("")
    val autoCheckForUpdatesDesc = _autoCheckForUpdatesDesc

    private val _acknowledgments = mutableStateOf("")
    val acknowledgments = _acknowledgments

    private val _data = mutableStateOf("")
    val data = _data

    private val _importFeatureIsPolishedNotPerfectDesc = mutableStateOf("")
    val importFeatureIsPolishedNotPerfectDesc = _importFeatureIsPolishedNotPerfectDesc

    private val _successfullyExported = mutableStateOf("")
    val successfullyExported = _successfullyExported

    private val _privacy = mutableStateOf("")
    val privacy = _privacy

    private val _theme = mutableStateOf("")
    val theme = _theme

    private val _followSystemTheme = mutableStateOf("")
    val followSystemTheme = _followSystemTheme

    private val _useDarkMode = mutableStateOf("")
    val useDarkMode = _useDarkMode

    private val _useDynamicTheming = mutableStateOf("")
    val useDynamicTheming = _useDynamicTheming

    private val _useDynamicThemingDesc = mutableStateOf("")
    val useDynamicThemingDesc = _useDynamicThemingDesc

    private val _kotlin = mutableStateOf("")
    val kotlin = _kotlin

    private val _apacheLicense = mutableStateOf("")
    val apacheLicense = _apacheLicense

    private val _androidJetpack = mutableStateOf("")
    val androidJetpack = _apacheLicense

    private val _coil = mutableStateOf("")
    val coil = _coil

    private val _materialDesign3 = mutableStateOf("")
    val materialDesign3 = _materialDesign3

    private val _materialIcons = mutableStateOf("")
    val materialIcons = _materialIcons

    private val _sendCrashReports = mutableStateOf("")
    val sendCrashReports = _sendCrashReports

    private val _useInAppBrowser = mutableStateOf("")
    val useInAppBrowser = _useInAppBrowser

    private val _useInAppBrowserDesc = mutableStateOf("")
    val useInAppBrowserDesc = _useInAppBrowserDesc

    private val _enableHomeScreen = mutableStateOf("")
    val enableHomeScreen = _enableHomeScreen

    private val _enableHomeScreenDesc = mutableStateOf("")
    val enableHomeScreenDesc = _enableHomeScreenDesc

    private val _autoDetectTitle = mutableStateOf("")
    val autoDetectTitle = _autoDetectTitle

    private val _autoDetectTitleDesc = mutableStateOf("")
    val autoDetectTitleDesc = _autoDetectTitleDesc

    private val _showDescriptionForSettings = mutableStateOf("")
    val showDescriptionForSettings = _showDescriptionForSettings

    private val _showDescriptionForSettingsDesc = mutableStateOf("")
    val showDescriptionForSettingsDesc = _showDescriptionForSettingsDesc

    private val _importData = mutableStateOf("")
    val importData = _importData

    private val _importDataFromExternalJsonFile = mutableStateOf("")
    val importDataFromExternalJsonFile = _importDataFromExternalJsonFile

    private val _exportDataDesc = mutableStateOf("")
    val exportDataDesc = _exportDataDesc

    private val _deleteEntireDataPermanently = mutableStateOf("")
    val deleteEntireDataPermanently = _deleteEntireDataPermanently

    private val _deleteEntireDataPermanentlyDesc = mutableStateOf("")
    val deleteEntireDataPermanentlyDesc = _deleteEntireDataPermanentlyDesc

    private val _everySingleBitOfDataIsStoredLocallyOnYourDevice = mutableStateOf("")
    val everySingleBitOfDataIsStoredLocallyOnYourDevice =
        _everySingleBitOfDataIsStoredLocallyOnYourDevice

    private val _linkoraCollectsDataRelatedToAppCrashes = mutableStateOf("")
    val linkoraCollectsDataRelatedToAppCrashes = _linkoraCollectsDataRelatedToAppCrashes

    private val _permissionRequiredToWriteTheData = mutableStateOf("")
    val permissionRequiredToWriteTheData = _permissionRequiredToWriteTheData

    private val _deletedEntireDataFromTheLocalDatabase = mutableStateOf("")
    val deletedEntireDataFromTheLocalDatabase = _deletedEntireDataFromTheLocalDatabase

    private val _linkoraWouldNotBePossibleWithoutTheFollowingOpenSourceSoftwareLibraries =
        mutableStateOf("")
    val linkoraWouldNotBePossibleWithoutTheFollowingOpenSourceSoftwareLibraries =
        _linkoraWouldNotBePossibleWithoutTheFollowingOpenSourceSoftwareLibraries

    private val _noFoldersAreFoundCreateFoldersForBetterOrganizationOfYourLinks = mutableStateOf("")
    val noFoldersAreFoundCreateFoldersForBetterOrganizationOfYourLinks =
        _noFoldersAreFoundCreateFoldersForBetterOrganizationOfYourLinks


    fun loadStrings(context: Context) {
        viewModelScope.launch {
            awaitAll(
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _general.value =
                            translationsRepo.getLocalizedStringValueFor(
                                "general",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                if (it.isNullOrBlank()) {
                                    context.getString(R.string.general)
                                } else {
                                    it
                                }
                            }
                    } else {
                        _general.value = (context.getString(R.string.general))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useLanguageStringsFetchedFromTheServer.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "use_language_strings_fetched_from_the_server",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.use_language_strings_fetched_from_the_server)
                                }
                            }
                        )
                    } else {
                        _useLanguageStringsFetchedFromTheServer.value =
                            (context.getString(R.string.use_language_strings_fetched_from_the_server))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useLanguageStringsFetchedFromTheServerDesc.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "use_language_strings_fetched_from_the_server_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.use_language_strings_fetched_from_the_server_desc)
                                }
                            }
                        )
                    } else {
                        _useLanguageStringsFetchedFromTheServerDesc.value =
                            (context.getString(R.string.use_language_strings_fetched_from_the_server_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _userAgentDesc.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "user_agent_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.user_agent_desc)
                                }
                            }
                        )
                    } else {
                        _userAgentDesc.value = (context.getString(R.string.user_agent_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _userAgent.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "user_agent",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.user_agent)
                                }
                            }
                        )
                    } else {
                        _userAgent.value = (context.getString(R.string.user_agent))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshingLinks.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "refreshing_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refreshing_links)
                                }
                            }
                        )
                    } else {
                        _refreshingLinks.value = (context.getString(R.string.refreshing_links))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _workManagerDesc.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "work_manager_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.work_manager_desc)
                                }
                            }
                        )
                    } else {
                        _workManagerDesc.value = (context.getString(R.string.work_manager_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linksRefreshed.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "links_refreshed",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.links_refreshed)
                                }
                            }
                        )
                    } else {
                        _linksRefreshed.value = (context.getString(R.string.links_refreshed))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshingLinksInfo.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "refreshing_links_info",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refreshing_links_info)
                                }
                            }
                        )
                    } else {
                        _refreshingLinksInfo.value =
                            (context.getString(R.string.refreshing_links_info))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshAllLinksTitlesAndImages.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "refresh_all_links_titles_and_images",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refresh_all_links_titles_and_images)
                                }
                            }
                        )
                    } else {
                        _refreshAllLinksTitlesAndImages.value =
                            (context.getString(R.string.refresh_all_links_titles_and_images))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshAllLinksTitlesAndImagesDesc.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "refresh_all_links_titles_and_images_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refresh_all_links_titles_and_images_desc)
                                }
                            }
                        )
                    } else {
                        _refreshAllLinksTitlesAndImagesDesc.value =
                            (context.getString(R.string.refresh_all_links_titles_and_images_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _of.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "of",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.of)
                                }
                            }
                        )
                    } else {
                        _of.value = (context.getString(R.string.of))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _titleCopiedToClipboard.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "title_copied_to_clipboard",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.title_copied_to_clipboard)
                                }
                            }
                        )
                    } else {
                        _titleCopiedToClipboard.value =
                            (context.getString(R.string.title_copied_to_clipboard))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _viewNote.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "view_note",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.view_note)
                                }
                            }
                        )
                    } else {
                        _viewNote.value = (context.getString(R.string.view_note))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _rename.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "rename",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.rename)
                                }
                            }
                        )
                    } else {
                        _rename.value = (context.getString(R.string.rename))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshingTitleAndImage.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "refreshing_title_and_image",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refreshing_title_and_image)
                                }
                            }
                        )
                    } else {
                        _refreshingTitleAndImage.value =
                            (context.getString(R.string.refreshing_title_and_image))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshImageAndTitle.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "refresh_image_and_title",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refresh_image_and_title)
                                }
                            }
                        )
                    } else {
                        _refreshImageAndTitle.value =
                            (context.getString(R.string.refresh_image_and_title))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _unarchive.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "unarchive",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.unarchive)
                                }
                            }
                        )
                    } else {
                        _unarchive.value = (context.getString(R.string.unarchive))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteTheNote.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "delete_the_note",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_the_note)
                                }
                            }
                        )
                    } else {
                        _deleteTheNote.value = (context.getString(R.string.delete_the_note))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteFolder.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "delete_folder",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_folder)
                                }
                            }
                        )
                    } else {
                        _deleteFolder.value = (context.getString(R.string.delete_folder))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteLink.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "delete_link",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_link)
                                }
                            }
                        )
                    } else {
                        _deleteLink.value = (context.getString(R.string.delete_link))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _savedNote.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "saved_note",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.saved_note)
                                }
                            }
                        )
                    } else {
                        _savedNote.value = (context.getString(R.string.saved_note))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noteCopiedToClipboard.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "note_copied_to_clipboard",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.note_copied_to_clipboard)
                                }
                            }
                        )
                    } else {
                        _noteCopiedToClipboard.value =
                            (context.getString(R.string.note_copied_to_clipboard))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _youDidNotAddNoteForThis.value = (
                            translationsRepo.getLocalizedStringValueFor(
                                "you_did_not_add_note_for_this",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.you_did_not_add_note_for_this)
                                }
                            }
                        )
                    } else {
                        _youDidNotAddNoteForThis.value =
                            (context.getString(R.string.you_did_not_add_note_for_this))
                    }
                },
            )
        }
    }
}