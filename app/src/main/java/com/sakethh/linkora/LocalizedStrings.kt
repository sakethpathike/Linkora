package com.sakethh.linkora

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.localization.language.translations.TranslationsRepo
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.utils.ifNullOrBlank
import com.sakethh.linkora.utils.linkoraLog
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

    private val _general = mutableStateOf("")
    val general = _general

    private val _userAgentDesc = mutableStateOf("")
    val userAgentDesc = _userAgentDesc

    private val _localizationServerDesc = mutableStateOf("")
    val localizationServerDesc = _localizationServerDesc

    private val _primaryUserAgent = mutableStateOf("")
    val primaryUserAgent = _primaryUserAgent

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

    private val _invalidUrl = mutableStateOf("")
    val invalidUrl = _invalidUrl

    private val _givenLinkAlreadyExists = mutableStateOf("")
    val givenLinkAlreadyExists = _givenLinkAlreadyExists

    private val _couldNotRetrieveMetadataNowButLinkoraSavedTheLink = mutableStateOf("")
    val couldNotRetrieveMetadataNowButLinkoraSavedTheLink =
        _couldNotRetrieveMetadataNowButLinkoraSavedTheLink

    private val _deleteTheNote = mutableStateOf("")
    val deleteTheNote = _deleteTheNote

    private val _addedTheUrl = mutableStateOf("")
    val addedTheUrl = _addedTheUrl

    private val _removedTheLinkFromArchive = mutableStateOf("")
    val removedTheLinkFromArchive = _removedTheLinkFromArchive

    private val _movedTheLinkToArchive = mutableStateOf("")
    val movedTheLinkToArchive = _movedTheLinkToArchive

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
    val thisFolderDeletionWillAlsoDeleteAllItsInternalFolders =
        _thisFolderDeletionWillAlsoDeleteAllItsInternalFolders

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

    private val _itemSelected = mutableStateOf("")
    val itemSelected = _itemSelected

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

    private val _and = mutableStateOf("")
    val and = _and

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
    val androidJetpack = _androidJetpack

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

    private val _addNewPanelToShelf = mutableStateOf("")
    val addNewPanelToShelf = _addNewPanelToShelf

    private val _panelsInTheShelf = mutableStateOf("")
    val panelsInTheShelf = _panelsInTheShelf

    private val _noPanelsFound = mutableStateOf("")
    val noPanelsFound = _noPanelsFound

    private val _shelf = mutableStateOf("")
    val shelf = _shelf

    private val _foldersListedInThisPanel = mutableStateOf("")
    val foldersListedInThisPanel = _foldersListedInThisPanel

    private val _noFoldersFoundInThisPanel = mutableStateOf("")
    val noFoldersFoundInThisPanel = _noFoldersFoundInThisPanel

    private val _youCanAddTheFollowingFoldersToThisPanel = mutableStateOf("")
    val youCanAddTheFollowingFoldersToThisPanel = _youCanAddTheFollowingFoldersToThisPanel

    private val _archivedFoldersDataMigratedSuccessfully = mutableStateOf("")
    val archivedFoldersDataMigratedSuccessfully = _archivedFoldersDataMigratedSuccessfully

    private val _rootFoldersDataMigratedSuccessfully = mutableStateOf("")
    val rootFoldersDataMigratedSuccessfully = _rootFoldersDataMigratedSuccessfully

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

    private val _displayingRemoteStrings = mutableStateOf("")
    val displayingRemoteStrings =
        _displayingRemoteStrings

    private val _displayingCompiledStrings = mutableStateOf("")
    val displayingCompiledStrings =
        _displayingCompiledStrings

    private val _retrieveLanguageInfoFromServer = mutableStateOf("")
    val retrieveLanguageInfoFromServer =
        _retrieveLanguageInfoFromServer

    private val _loadServerStrings = mutableStateOf("")
    val loadServerStrings =
        _loadServerStrings

    private val _loadCompiledStrings = mutableStateOf("")
    val loadCompiledStrings =
        _loadCompiledStrings

    private val _updateRemoteLanguageStrings = mutableStateOf("")
    val updateRemoteLanguageStrings =
        _updateRemoteLanguageStrings

    private val _removeLanguageStrings = mutableStateOf("")
    val removeLanguageStrings =
        _removeLanguageStrings

    private val _helpImproveLanguageStrings = mutableStateOf("")
    val helpImproveLanguageStrings =
        _helpImproveLanguageStrings

    private val _discord = mutableStateOf("")
    val discord =
        _discord

    private val _stringsLocalized = mutableStateOf("")
    val stringsLocalized =
        _stringsLocalized

    private val _localizationServer = mutableStateOf("")
    val localizationServer =
        _localizationServer

    private val _noFoldersAvailableInThisPanelAddFoldersToBegin = mutableStateOf("")
    val noFoldersAvailableInThisPanelAddFoldersToBegin =
        _noFoldersAvailableInThisPanelAddFoldersToBegin

    private val _youCanFindSavedLinksAndImportantLinksInTheDefaultPanel = mutableStateOf("")
    val youCanFindSavedLinksAndImportantLinksInTheDefaultPanel =
        _youCanFindSavedLinksAndImportantLinksInTheDefaultPanel

    private val _moveToArchive = mutableStateOf("")
    val moveToArchive =
        _moveToArchive

    private val _removeFromArchive = mutableStateOf("")
    val removeFromArchive =
        _removeFromArchive

    private val _addToImportantLinks = mutableStateOf("")
    val addToImportantLinks =
        _addToImportantLinks

    private val _removeFromImportantLinks = mutableStateOf("")
    val removeFromImportantLinks =
        _removeFromImportantLinks

    private val _syncingLanguageDetailsThisMayTakeSomeTime = mutableStateOf("")
    val syncingLanguageDetailsThisMayTakeSomeTime =
        _syncingLanguageDetailsThisMayTakeSomeTime

    private val _syncingTranslationsForCurrentlySelectedLanguage = mutableStateOf("")
    val syncingTranslationsForCurrentlySelectedLanguage =
        _syncingTranslationsForCurrentlySelectedLanguage

    private val _cannotRetrieveNowPleaseTryAgain = mutableStateOf("")
    val cannotRetrieveNowPleaseTryAgain =
        _cannotRetrieveNowPleaseTryAgain

    private val _fetchedSuccessfully = mutableStateOf("")
    val fetchedSuccessfully =
        _fetchedSuccessfully

    private val _socials = mutableStateOf("")
    val socials =
        _socials

    private val _development = mutableStateOf("")
    val development =
        _development

    private val _haveASuggestionCreateAnIssueOnGithubToImproveLinkora = mutableStateOf("")
    val haveASuggestionCreateAnIssueOnGithubToImproveLinkora =
        _haveASuggestionCreateAnIssueOnGithubToImproveLinkora

    private val _trackRecentChangesAndUpdatesToLinkora = mutableStateOf("")
    val trackRecentChangesAndUpdatesToLinkora =
        _trackRecentChangesAndUpdatesToLinkora

    private val _helpMakeLinkoraAccessibleInMoreLanguagesByContributingTranslations =
        mutableStateOf("")
    val helpMakeLinkoraAccessibleInMoreLanguagesByContributingTranslations =
        _helpMakeLinkoraAccessibleInMoreLanguagesByContributingTranslations

    private val _updatedLanguageInfoSuccessfully = mutableStateOf("")
    val updatedLanguageInfoSuccessfully =
        _updatedLanguageInfoSuccessfully

    private val _languageInfoAndStringsAreUpToDate = mutableStateOf("")
    val languageInfoAndStringsAreUpToDate =
        _languageInfoAndStringsAreUpToDate

    private val _openAGithubIssue = mutableStateOf("")
    val openAGithubIssue =
        _openAGithubIssue

    private val _changelog = mutableStateOf("")
    val changelog =
        _changelog

    private val _helpTranslateLinkora = mutableStateOf("")
    val helpTranslateLinkora =
        _helpTranslateLinkora

    private val _editPanelName = mutableStateOf("")
    val editPanelName =
        _editPanelName

    private val _areYouSureWantToDeleteThePanel = mutableStateOf("")
    val areYouSureWantToDeleteThePanel =
        _areYouSureWantToDeleteThePanel

    private val _areYouSureWantToDeleteTheFolder = mutableStateOf("")
    val areYouSureWantToDeleteTheFolder =
        _areYouSureWantToDeleteTheFolder

    private val _renameFolder = mutableStateOf("")
    val renameFolder =
        _renameFolder

    private val _createANewInternalFolderIn = mutableStateOf("")
    val createANewInternalFolderIn =
        _createANewInternalFolderIn

    private val _showAssociatedImageInLinkMenu = mutableStateOf("")
    val showAssociatedImageInLinkMenu =
        _showAssociatedImageInLinkMenu

    private val _enablesTheDisplayOfAnAssociatedImageWithinTheLinkMenu = mutableStateOf("")
    val enablesTheDisplayOfAnAssociatedImageWithinTheLinkMenu =
        _enablesTheDisplayOfAnAssociatedImageWithinTheLinkMenu

    private val _viewAll = mutableStateOf("")
    val viewAll =
        _viewAll

    private val _linkLayout = mutableStateOf("")
    val linkLayout =
        _linkLayout

    private val _showBorderAroundLinks = mutableStateOf("")
    val showBorderAroundLinks =
        _showBorderAroundLinks

    private val _showTitle = mutableStateOf("")
    val showTitle =
        _showTitle

    private val _showBaseUrl = mutableStateOf("")
    val showBaseUrl =
        _showBaseUrl

    private val _showBottomFadedEdge = mutableStateOf("")
    val showBottomFadedEdge =
        _showBottomFadedEdge

    private val _linkLayoutSettings = mutableStateOf("")
    val linkLayoutSettings =
        _linkLayoutSettings

    private val _chooseTheLayoutYouLikeBest = mutableStateOf("")
    val chooseTheLayoutYouLikeBest =
        _chooseTheLayoutYouLikeBest

    private val _feedPreview = mutableStateOf("")
    val feedPreview =
        _feedPreview

    private val _allLinks = mutableStateOf("")
    val allLinks =
        _allLinks

    private val _filterBasedOn = mutableStateOf("")
    val filterBasedOn =
        _filterBasedOn

    private val _foldersLinks = mutableStateOf("")
    val foldersLinks =
        _foldersLinks

    private val _regularListView = mutableStateOf("")
    val regularListView =
        _regularListView

    private val _titleOnlyListView = mutableStateOf("")
    val titleOnlyListView =
        _titleOnlyListView

    private val _gridView = mutableStateOf("")
    val gridView =
        _gridView

    private val _staggeredView = mutableStateOf("")
    val staggeredView =
        _staggeredView

    private val _advanced = mutableStateOf("")
    val advanced =
        _advanced

    private val _clearImageCache = mutableStateOf("")
    val clearImageCache =
        _clearImageCache

    private val _clearImageCacheDesc = mutableStateOf("")
    val clearImageCacheDesc =
        _clearImageCacheDesc

    private val _willBeUsedToRetrieveMetadata = mutableStateOf("")
    val willBeUsedToRetrieveMetadata =
        _willBeUsedToRetrieveMetadata

    private val _initialRequestFailed = mutableStateOf("")
    val initialRequestFailed =
        _initialRequestFailed

    private val _retryingMetadataRetrievalWithASecondaryUserAgent = mutableStateOf("")
    val retryingMetadataRetrievalWithASecondaryUserAgent =
        _retryingMetadataRetrievalWithASecondaryUserAgent

    private val _siteSpecificUserAgentSettings = mutableStateOf("")
    val siteSpecificUserAgentSettings =
        _siteSpecificUserAgentSettings

    private val _secondaryUserAgentDesc = mutableStateOf("")
    val secondaryUserAgentDesc =
        _secondaryUserAgentDesc

    private val _secondaryUserAgent = mutableStateOf("")
    val secondaryUserAgent =
        _secondaryUserAgent

    private val _aFolderCannotBeMovedIntoItself = mutableStateOf("")
    val aFolderCannotBeMovedIntoItself =
        _aFolderCannotBeMovedIntoItself

    private val _waitForTheOperationToFinish = mutableStateOf("")
    val waitForTheOperationToFinish =
        _waitForTheOperationToFinish

    private val _moveToRootFolders = mutableStateOf("")
    val moveToRootFolders =
        _moveToRootFolders

    private val _copyFolder = mutableStateOf("")
    val copyFolder =
        _copyFolder

    private val _copyLink = mutableStateOf("")
    val copyLink =
        _copyLink

    private val _moveToOtherFolder = mutableStateOf("")
    val moveToOtherFolder =
        _moveToOtherFolder

    private val _moveLink = mutableStateOf("")
    val moveLink =
        _moveLink

    private val _addANewSiteSpecificUserAgent = mutableStateOf("")
    val addANewSiteSpecificUserAgent =
        _addANewSiteSpecificUserAgent

    private val _noSiteSpecificUserAgentFoundAddOneToAlwaysRetrieveMetadataFromIt =
        mutableStateOf("")
    val noSiteSpecificUserAgentFoundAddOneToAlwaysRetrieveMetadataFromIt =
        _noSiteSpecificUserAgentFoundAddOneToAlwaysRetrieveMetadataFromIt

    private val _domain = mutableStateOf("")
    val domain =
        _domain

    private val _onlyTheDomainShouldBeSavedForExampleSave = mutableStateOf("")
    val onlyTheDomainShouldBeSavedForExampleSave =
        _onlyTheDomainShouldBeSavedForExampleSave

    private val _as = mutableStateOf("")
    val `as` =
        _as

    private val _delete = mutableStateOf("")
    val delete =
        _delete

    private val _invalidDomain = mutableStateOf("")
    val invalidDomain =
        _invalidDomain

    private val _useAmoledTheme = mutableStateOf("")
    val useAmoledTheme =
        _useAmoledTheme

    private val __foldersSelected = mutableStateOf("")
    val foldersSelected_ =
        __foldersSelected

    private val _linksSelected = mutableStateOf("")
    val linksSelected =
        _linksSelected

    private val _panels = mutableStateOf("")
    val panels =
        _panels

    private val _givenDomainAlreadyExists = mutableStateOf("")
    val givenDomainAlreadyExists =
        _givenDomainAlreadyExists

    private val _addedTheGivenDomainSuccessfully = mutableStateOf("")
    val addedTheGivenDomainSuccessfully =
        _addedTheGivenDomainSuccessfully

    private val _import = mutableStateOf("")
    val import =
        _import

    private val _importUsingJsonFile = mutableStateOf("")
    val importUsingJsonFile =
        _importUsingJsonFile

    private val _importUsingJsonFileDesc = mutableStateOf("")
    val importUsingJsonFileDesc =
        _importUsingJsonFileDesc

    private val _importDataFromHtmlFile = mutableStateOf("")
    val importDataFromHtmlFile =
        _importDataFromHtmlFile

    private val _importDataFromHtmlFileDesc = mutableStateOf("")
    val importDataFromHtmlFileDesc =
        _importDataFromHtmlFileDesc

    private val _export = mutableStateOf("")
    val export =
        _export

    private val _exportDataAsJson = mutableStateOf("")
    val exportDataAsJson =
        _exportDataAsJson

    private val _exportDataAsJsonDesc = mutableStateOf("")
    val exportDataAsJsonDesc =
        _exportDataAsJsonDesc

    private val _exportDataAsHtml = mutableStateOf("")
    val exportDataAsHtml =
        _exportDataAsHtml

    private val _exportDataAsHtmlDesc = mutableStateOf("")
    val exportDataAsHtmlDesc =
        _exportDataAsHtmlDesc

    private val _savedLinksAndLinksFromAllFoldersIncludingArchives = mutableStateOf("")
    val savedLinksAndLinksFromAllFoldersIncludingArchives =
        _savedLinksAndLinksFromAllFoldersIncludingArchives

    private val _regularFolders = mutableStateOf("")
    val regularFolders =
        _regularFolders

    private val _panelFolders = mutableStateOf("")
    val panelFolders =
        _panelFolders

    private val _parsingTheFile = mutableStateOf("")
    val parsingTheFile =
        _parsingTheFile

    private val _insertingDataIntoTheDatabase = mutableStateOf("")
    val insertingDataIntoTheDatabase =
        _insertingDataIntoTheDatabase

    private val _modifyingKeysToPreventConflictsWithLocalData = mutableStateOf("")
    val modifyingKeysToPreventConflictsWithLocalData =
        _modifyingKeysToPreventConflictsWithLocalData

    private val _importingDesc = mutableStateOf("")
    val importingDesc =
        _importingDesc

    private val _gatheringDataForExport = mutableStateOf("")
    val gatheringDataForExport =
        _gatheringDataForExport

    private val _dataExportDesc = mutableStateOf("")
    val dataExportDesc =
        _dataExportDesc

    private val _updatingFolderNameRestriction = mutableStateOf("")
    val updatingFolderNameRestriction =
        _updatingFolderNameRestriction

    private val _folderNameRestrictionDesc = mutableStateOf("")
    val folderNameRestrictionDesc =
        _folderNameRestrictionDesc

    private val _noDataWillBeRetrievedBecauseThisSettingIsEnabled = mutableStateOf("")
    val noDataWillBeRetrievedBecauseThisSettingIsEnabled =
        _noDataWillBeRetrievedBecauseThisSettingIsEnabled

    private val _forceSaveWithoutRetrievingMetadata = mutableStateOf("")
    val forceSaveWithoutRetrievingMetadata =
        _forceSaveWithoutRetrievingMetadata

    private var count = 0

    private suspend fun loadStringsHelper(
        translationsRepo: TranslationsRepo,
        remoteStringID: String,
        @StringRes localId: Int,
        mutableString: MutableState<String>,
        context: Context
    ) {
        count++
        if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
            mutableString.value =
                (translationsRepo.getLocalizedStringValueFor(
                    remoteStringID,
                    SettingsPreference.preferredAppLanguageCode.value
                ).let {
                    it.ifNullOrBlank {
                        context.getString(localId)
                    }
                })
        } else {
            mutableString.value =
                context.getString(localId)
        }
    }

    fun loadStrings(context: Context) {
        val translationsRepo =
            EntryPoints.get(context.applicationContext, TranslationRepoInstance::class.java)
                .getTranslationRepo()
        viewModelScope.launch {
            count = 0
            awaitAll(
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "force_save_without_retrieving_metadata",
                        localId = R.string.force_save_without_retrieving_metadata,
                        mutableString = _forceSaveWithoutRetrievingMetadata,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_data_will_be_retrieved_because_this_setting_is_enabled",
                        localId = R.string.no_data_will_be_retrieved_because_this_setting_is_enabled,
                        mutableString = _noDataWillBeRetrievedBecauseThisSettingIsEnabled,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder_name_restriction_desc",
                        localId = R.string.folder_name_restriction_desc,
                        mutableString = _folderNameRestrictionDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "updating_folder_name_restriction",
                        localId = R.string.updating_folder_name_restriction,
                        mutableString = _updatingFolderNameRestriction,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "data_export_desc",
                        localId = R.string.data_export_desc,
                        mutableString = _dataExportDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "gathering_data_for_export",
                        localId = R.string.gathering_data_for_export,
                        mutableString = _gatheringDataForExport,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "importing_desc",
                        localId = R.string.importing_desc,
                        mutableString = _importingDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "modifying_keys_to_prevent_conflicts_with_local_data",
                        localId = R.string.modifying_keys_to_prevent_conflicts_with_local_data,
                        mutableString = _modifyingKeysToPreventConflictsWithLocalData,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "inserting_data_into_the_database",
                        localId = R.string.inserting_data_into_the_database,
                        mutableString = _insertingDataIntoTheDatabase,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "parsing_the_file",
                        localId = R.string.parsing_the_file,
                        mutableString = _parsingTheFile,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "panel_folders",
                        localId = R.string.panel_folders,
                        mutableString = _panelFolders,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "regular_folders",
                        localId = R.string.regular_folders,
                        mutableString = _regularFolders,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "saved_links_and_links_from_all_folders_including_archives",
                        localId = R.string.saved_links_and_links_from_all_folders_including_archives,
                        mutableString = _savedLinksAndLinksFromAllFoldersIncludingArchives,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "export_data_as_html_desc",
                        localId = R.string.export_data_as_html_desc,
                        mutableString = _exportDataAsHtmlDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "export_data_as_html",
                        localId = R.string.export_data_as_html,
                        mutableString = _exportDataAsHtml,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "export_data_as_json_desc",
                        localId = R.string.export_data_as_json_desc,
                        mutableString = _exportDataAsJsonDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "export_data_as_json",
                        localId = R.string.export_data_as_json,
                        mutableString = _exportDataAsJson,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "export",
                        localId = R.string.export,
                        mutableString = _export,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_data_from_html_file_desc",
                        localId = R.string.import_data_from_html_file_desc,
                        mutableString = _importDataFromHtmlFileDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_data_from_html_file",
                        localId = R.string.import_data_from_html_file,
                        mutableString = _importDataFromHtmlFile,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_using_json_file_desc",
                        localId = R.string.import_using_json_file_desc,
                        mutableString = _importUsingJsonFileDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_using_json_file",
                        localId = R.string.import_using_json_file,
                        mutableString = _importUsingJsonFile,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "_import",
                        localId = R.string._import,
                        mutableString = _import,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "given_domain_already_exists",
                        localId = R.string.given_domain_already_exists,
                        mutableString = _givenDomainAlreadyExists,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "added_the_given_domain_successfully",
                        localId = R.string.added_the_given_domain_successfully,
                        mutableString = _addedTheGivenDomainSuccessfully,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "panels",
                        localId = R.string.panels,
                        mutableString = _panels,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "invalid_url",
                        localId = R.string.invalid_url,
                        mutableString = _invalidUrl,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "will_be_used_to_retrieve_metadata",
                        localId = R.string.will_be_used_to_retrieve_metadata,
                        mutableString = _willBeUsedToRetrieveMetadata,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "user_agent",
                        localId = R.string.user_agent,
                        mutableString = _userAgent,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "_folders_selected",
                        localId = R.string._folders_selected,
                        mutableString = __foldersSelected,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "links_selected",
                        localId = R.string.links_selected,
                        mutableString = _linksSelected,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "initial_request_failed",
                        localId = R.string.initial_request_failed,
                        mutableString = _initialRequestFailed,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "retrying_metadata_retrieval_with_a_secondary_user_agent",
                        localId = R.string.retrying_metadata_retrieval_with_a_secondary_user_agent,
                        mutableString = _retryingMetadataRetrievalWithASecondaryUserAgent,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "invalid_domain",
                        localId = R.string.invalid_domain,
                        mutableString = _invalidDomain,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "use_amoled_theme",
                        localId = R.string.use_amoled_theme,
                        mutableString = _useAmoledTheme,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "site_specific_user_agent_settings",
                        localId = R.string.site_specific_user_agent_settings,
                        mutableString = _siteSpecificUserAgentSettings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "secondary_user_agent_desc",
                        localId = R.string.secondary_user_agent_desc,
                        mutableString = _secondaryUserAgentDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "secondary_user_agent",
                        localId = R.string.secondary_user_agent,
                        mutableString = _secondaryUserAgent,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "a_folder_cannot_be_moved_into_itself",
                        localId = R.string.a_folder_cannot_be_moved_into_itself,
                        mutableString = _aFolderCannotBeMovedIntoItself,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "wait_for_the_operation_to_finish",
                        localId = R.string.wait_for_the_operation_to_finish,
                        mutableString = _waitForTheOperationToFinish,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "move_to_root_folders",
                        localId = R.string.move_to_root_folders,
                        mutableString = _moveToRootFolders,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "copy_folder",
                        localId = R.string.copy_folder,
                        mutableString = _copyFolder,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "copy_link",
                        localId = R.string.copy_link,
                        mutableString = _copyLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "move_to_other_folder",
                        localId = R.string.move_to_other_folder,
                        mutableString = _moveToOtherFolder,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "move_link",
                        localId = R.string.move_link,
                        mutableString = _moveLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "item_selected",
                        localId = R.string.item_selected,
                        mutableString = _itemSelected,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_a_new_site_specific_user_agent",
                        localId = R.string.add_a_new_site_specific_user_agent,
                        mutableString = _addANewSiteSpecificUserAgent,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_site_specific_user_agent_found_add_one_to_always_retrieve_metadata_from_it",
                        localId = R.string.no_site_specific_user_agent_found_add_one_to_always_retrieve_metadata_from_it,
                        mutableString = _noSiteSpecificUserAgentFoundAddOneToAlwaysRetrieveMetadataFromIt,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "domain",
                        localId = R.string.domain,
                        mutableString = _domain,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "only_the_domain_should_be_saved_for_example_save",
                        localId = R.string.only_the_domain_should_be_saved_for_example_save,
                        mutableString = _onlyTheDomainShouldBeSavedForExampleSave,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "as",
                        localId = R.string.`as`,
                        mutableString = _as,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "delete",
                        localId = R.string.delete,
                        mutableString = _delete,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "given_link_already_exists",
                        localId = R.string.given_link_already_exists,
                        mutableString = _givenLinkAlreadyExists,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "could_not_retrieve_metadata_now_but_linkora_saved_the_link",
                        localId = R.string.could_not_retrieve_metadata_now_but_linkora_saved_the_link,
                        mutableString = _couldNotRetrieveMetadataNowButLinkoraSavedTheLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "added_the_url",
                        localId = R.string.added_the_url,
                        mutableString = _addedTheUrl,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "removed_the_link_from_archive",
                        localId = R.string.removed_the_link_from_archive,
                        mutableString = _removedTheLinkFromArchive,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "moved_the_link_to_archive",
                        localId = R.string.moved_the_link_to_archive,
                        mutableString = _movedTheLinkToArchive,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "remove_from_archive",
                        localId = R.string.remove_from_archive,
                        mutableString = _removeFromArchive,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "clear_image_cache_desc",
                        localId = R.string.clear_image_cache_desc,
                        mutableString = _clearImageCacheDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "clear_image_cache",
                        localId = R.string.clear_image_cache,
                        mutableString = _clearImageCache,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "advanced",
                        localId = R.string.advanced,
                        mutableString = _advanced,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "staggered_view",
                        localId = R.string.staggered_view,
                        mutableString = _staggeredView,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "grid_view",
                        localId = R.string.grid_view,
                        mutableString = _gridView,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "title_only_list_view",
                        localId = R.string.title_only_list_view,
                        mutableString = _titleOnlyListView,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "regular_list_view",
                        localId = R.string.regular_list_view,
                        mutableString = _regularListView,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folders_links",
                        localId = R.string.folders_links,
                        mutableString = _foldersLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "filter_based_on",
                        localId = R.string.filter_based_on,
                        mutableString = _filterBasedOn,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "all_links",
                        localId = R.string.all_links,
                        mutableString = _allLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "feed_preview",
                        localId = R.string.feed_preview,
                        mutableString = _feedPreview,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "choose_the_layout_you_like_best",
                        localId = R.string.choose_the_layout_you_like_best,
                        mutableString = _chooseTheLayoutYouLikeBest,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "link_layout_settings",
                        localId = R.string.link_layout_settings,
                        mutableString = _linkLayoutSettings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "show_bottom_faded_edge",
                        localId = R.string.show_bottom_faded_edge,
                        mutableString = _showBottomFadedEdge,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "show_base_url",
                        localId = R.string.show_base_url,
                        mutableString = _showBaseUrl,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "show_title",
                        localId = R.string.show_title,
                        mutableString = _showTitle,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "show_border_around_links",
                        localId = R.string.show_border_around_links,
                        mutableString = _showBorderAroundLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "link_layout",
                        localId = R.string.link_layout,
                        mutableString = _linkLayout,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "view_all",
                        localId = R.string.view_all,
                        mutableString = _viewAll,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "enables_the_display_of_an_associated_image_within_the_link_menu",
                        localId = R.string.enables_the_display_of_an_associated_image_within_the_link_menu,
                        mutableString = _enablesTheDisplayOfAnAssociatedImageWithinTheLinkMenu,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "show_associated_image_in_link_menu",
                        localId = R.string.show_associated_image_in_link_menu,
                        mutableString = _showAssociatedImageInLinkMenu,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "create_a_new_internal_folder_in",
                        localId = R.string.create_a_new_internal_folder_in,
                        mutableString = _createANewInternalFolderIn,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "rename_folder",
                        localId = R.string.rename_folder,
                        mutableString = _renameFolder,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_want_to_delete_the_folder",
                        localId = R.string.are_you_sure_want_to_delete_the_folder,
                        mutableString = _areYouSureWantToDeleteTheFolder,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_want_to_delete_the_panel",
                        localId = R.string.are_you_sure_want_to_delete_the_panel,
                        mutableString = _areYouSureWantToDeleteThePanel,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "edit_panel_name",
                        localId = R.string.edit_panel_name,
                        mutableString = _editPanelName,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "help_translate_linkora",
                        localId = R.string.help_translate_linkora,
                        mutableString = _helpTranslateLinkora,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "changelog",
                        localId = R.string.changelog,
                        mutableString = _changelog,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "open_a_github_issue",
                        localId = R.string.open_a_github_issue,
                        mutableString = _openAGithubIssue,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "language_info_and_strings_are_up_to_date",
                        localId = R.string.language_info_and_strings_are_up_to_date,
                        mutableString = _languageInfoAndStringsAreUpToDate,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "updated_language_info_successfully",
                        localId = R.string.updated_language_info_successfully,
                        mutableString = _updatedLanguageInfoSuccessfully,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "help_make_linkora_accessible_in_more_languages_by_contributing_translations",
                        localId = R.string.help_make_linkora_accessible_in_more_languages_by_contributing_translations,
                        mutableString = _helpMakeLinkoraAccessibleInMoreLanguagesByContributingTranslations,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "track_recent_changes_and_updates_to_linkora",
                        localId = R.string.track_recent_changes_and_updates_to_linkora,
                        mutableString = _trackRecentChangesAndUpdatesToLinkora,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "have_a_suggestion_create_an_issue_on_github_to_improve_linkora",
                        localId = R.string.have_a_suggestion_create_an_issue_on_github_to_improve_linkora,
                        mutableString = _haveASuggestionCreateAnIssueOnGithubToImproveLinkora,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "development",
                        localId = R.string.development,
                        mutableString = _development,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "socials",
                        localId = R.string.socials,
                        mutableString = _socials,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "fetched_successfully",
                        localId = R.string.fetched_successfully,
                        mutableString = _fetchedSuccessfully,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "cannot_retrieve_now_please_try_again",
                        localId = R.string.cannot_retrieve_now_please_try_again,
                        mutableString = _cannotRetrieveNowPleaseTryAgain,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "syncing_translations_for_this_may_take_some_time",
                        localId = R.string.syncing_translations_for_this_may_take_some_time,
                        mutableString = _syncingTranslationsForCurrentlySelectedLanguage,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "syncing_language_details_this_may_take_some_time",
                        localId = R.string.syncing_language_details_this_may_take_some_time,
                        mutableString = _syncingLanguageDetailsThisMayTakeSomeTime,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "remove_from_important_links",
                        localId = R.string.remove_from_important_links,
                        mutableString = _removeFromImportantLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_to_important_links",
                        localId = R.string.add_to_important_links,
                        mutableString = _addToImportantLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "remove_from_archive",
                        localId = R.string.remove_from_archive,
                        mutableString = _moveToArchive,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "move_to_archive",
                        localId = R.string.move_to_archive,
                        mutableString = _moveToArchive,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "you_can_find_saved_links_and_important_links_in_the_default_panel",
                        localId = R.string.you_can_find_saved_links_and_important_links_in_the_default_panel,
                        mutableString = _youCanFindSavedLinksAndImportantLinksInTheDefaultPanel,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_folders_available_in_this_panel_add_folders_to_begin",
                        localId = R.string.no_folders_available_in_this_panel_add_folders_to_begin,
                        mutableString = _noFoldersAvailableInThisPanelAddFoldersToBegin,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "localization_server",
                        localId = R.string.localization_server,
                        mutableString = _localizationServer,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "localization_server_desc",
                        localId = R.string.localization_server_desc,
                        mutableString = _localizationServerDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "strings_localized",
                        localId = R.string.strings_localized,
                        mutableString = _stringsLocalized,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "discord",
                        localId = R.string.discord,
                        mutableString = _discord,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "help_improve_language_strings",
                        localId = R.string.help_improve_language_strings,
                        mutableString = _helpImproveLanguageStrings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "remove_remote_language_strings",
                        localId = R.string.remove_remote_language_strings,
                        mutableString = _removeLanguageStrings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "load_compiled_strings",
                        localId = R.string.load_compiled_strings,
                        mutableString = _loadCompiledStrings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "update_remote_language_strings",
                        localId = R.string.update_remote_language_strings,
                        mutableString = _updateRemoteLanguageStrings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "load_server_strings",
                        localId = R.string.load_server_strings,
                        mutableString = _loadServerStrings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "retrieve_language_info_from_server",
                        localId = R.string.retrieve_language_info_from_server,
                        mutableString = _retrieveLanguageInfoFromServer,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "displaying_remote_strings",
                        localId = R.string.displaying_remote_strings,
                        mutableString = _displayingRemoteStrings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "displaying_compiled_strings",
                        localId = R.string.displaying_compiled_strings,
                        mutableString = _displayingCompiledStrings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "sort_history_links_by",
                        localId = R.string.sort_history_links_by,
                        mutableString = _sortHistoryLinksBy,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "sort_by",
                        localId = R.string.sort_by,
                        mutableString = _sortBy,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "sort_saved_links_by",
                        localId = R.string.sort_saved_links_by,
                        mutableString = _sortSavedLinksBy,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "sort_important_links_by",
                        localId = R.string.sort_important_links_by,
                        mutableString = _sortImportantLinksBy,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "sort_based_on",
                        localId = R.string.sort_based_on,
                        mutableString = _sortBasedOn,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folders",
                        localId = R.string.folders,
                        mutableString = _folders,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_a_new_link_in_important_links",
                        localId = R.string.add_a_new_link_in_important_links,
                        mutableString = _addANewLinkInImportantLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_a_new_link_in_saved_links",
                        localId = R.string.add_a_new_link_in_saved_links,
                        mutableString = _addANewLinkInSavedLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_a_new_link_in",
                        localId = R.string.add_a_new_link_in,
                        mutableString = _addANewLinkIn,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_a_new_link",
                        localId = R.string.add_a_new_link,
                        mutableString = _addANewLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "link_address",
                        localId = R.string.link_address,
                        mutableString = _linkAddress,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "title_for_the_link",
                        localId = R.string.title_for_the_link,
                        mutableString = _titleForTheLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "note_for_saving_the_link",
                        localId = R.string.note_for_saving_the_link,
                        mutableString = _noteForSavingTheLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "title_will_be_automatically_detected",
                        localId = R.string.title_will_be_automatically_detected,
                        mutableString = _titleWillBeAutomaticallyDetected,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_in",
                        localId = R.string.add_in,
                        mutableString = _addIn,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "saved_links",
                        localId = R.string.saved_links,
                        mutableString = _savedLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "important_links",
                        localId = R.string.important_links,
                        mutableString = _importantLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "force_auto_detect_title",
                        localId = R.string.force_auto_detect_title,
                        mutableString = _forceAutoDetectTitle,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "cancel",
                        localId = R.string.cancel,
                        mutableString = _cancel,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "save",
                        localId = R.string.save,
                        mutableString = _save,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "this_folder_has_no_subfolders",
                        localId = R.string.this_folder_has_no_subfolders,
                        mutableString = _thisFolderHasNoSubfolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "save_in_this_folder",
                        localId = R.string.save_in_this_folder,
                        mutableString = _saveInThisFolder,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_a_new_panel_to_the_shelf",
                        localId = R.string.add_a_new_panel_to_the_shelf,
                        mutableString = _addANewPanelToTheShelf,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "panel_name",
                        localId = R.string.panel_name,
                        mutableString = _panelName,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_new_panel",
                        localId = R.string.add_new_panel,
                        mutableString = _addNewPanel,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder_name_cannnot_be_empty",
                        localId = R.string.folder_name_cannnot_be_empty,
                        mutableString = _folderNameCannnotBeEmpty,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder_name",
                        localId = R.string.folder_name,
                        mutableString = _folderName,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "note_for_creating_the_folder",
                        localId = R.string.note_for_creating_the_folder,
                        mutableString = _noteForCreatingTheFolder,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "create_a_new_internal_folder_in",
                        localId = R.string.create_a_new_internal_folder_in,
                        mutableString = _createANewFolderIn,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "create_a_new_folder",
                        localId = R.string.create_a_new_folder,
                        mutableString = _createANewFolder,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "create",
                        localId = R.string.create,
                        mutableString = _create,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_want_to_delete",
                        localId = R.string.are_you_sure_want_to_delete,
                        mutableString = _areYouSureWantToDelete,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "permanently_delete_the_panel",
                        localId = R.string.permanently_delete_the_panel,
                        mutableString = _permanentlyDeleteThePanel,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "once_deleted_this_panel_cannot_be_restarted",
                        localId = R.string.once_deleted_this_panel_cannot_be_restarted,
                        mutableString = _onceDeletedThisPanelCannotBeRestarted,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "delete_it",
                        localId = R.string.delete_it,
                        mutableString = _deleteIt,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "this_folder_deletion_will_also_delete_all_its_internal_folders",
                        localId = R.string.this_folder_deletion_will_also_delete_all_its_internal_folders,
                        mutableString = _thisFolderDeletionWillAlsoDeleteAllItsInternalFolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_you_want_to_delete_all_selected_links",
                        localId = R.string.are_you_sure_you_want_to_delete_all_selected_links,
                        mutableString = _areYouSureYouWantToDeleteAllSelectedLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_you_want_to_delete_the_link",
                        localId = R.string.are_you_sure_you_want_to_delete_the_link,
                        mutableString = _areYouSureYouWantToDeleteTheLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_you_want_to_delete_all_selected_folders",
                        localId = R.string.are_you_sure_you_want_to_delete_all_selected_folders,
                        mutableString = _areYouSureYouWantToDeleteAllSelectedFolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_want_to_delete_the",
                        localId = R.string.are_you_sure_want_to_delete_the,
                        mutableString = _areYouSureWantToDeleteThe,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder",
                        localId = R.string.folder,
                        mutableString = _folder,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_you_want_to_delete_all_selected_items",
                        localId = R.string.are_you_sure_you_want_to_delete_all_selected_items,
                        mutableString = _areYouSureYouWantToDeleteAllSelectedItems,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "are_you_sure_you_want_to_delete_all_folders_and_links",
                        localId = R.string.are_you_sure_you_want_to_delete_all_folders_and_links,
                        mutableString = _areYouSureYouWantToDeleteAllFoldersAndLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_activity_found_to_handle_intent",
                        localId = R.string.no_activity_found_to_handle_intent,
                        mutableString = _noActivityFoundToHandleIntent,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "link_copied_to_the_clipboard",
                        localId = R.string.link_copied_to_the_clipboard,
                        mutableString = _linkCopiedToTheClipboard,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "change_panel_name",
                        localId = R.string.change_panel_name,
                        mutableString = _changePanelName,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "edit",
                        localId = R.string.edit,
                        mutableString = _edit,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_name_for_panel",
                        localId = R.string.new_name_for_panel,
                        mutableString = _newNameForPanel,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "change_note_only",
                        localId = R.string.change_note_only,
                        mutableString = _changeNoteOnly,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "change_both_name_and_note",
                        localId = R.string.change_both_name_and_note,
                        mutableString = _changeBothNameAndNote,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "title_cannot_be_empty",
                        localId = R.string.title_cannot_be_empty,
                        mutableString = _titleCannotBeEmpty,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "change_link_data",
                        localId = R.string.change_link_data,
                        mutableString = _changeLinkData,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_name",
                        localId = R.string.new_name,
                        mutableString = _newName,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_title",
                        localId = R.string.new_title,
                        mutableString = _newTitle,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_note",
                        localId = R.string.new_note,
                        mutableString = _newNote,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "leave_above_field_empty_if_you_do_not_want_to_change_the_note",
                        localId = R.string.leave_above_field_empty_if_you_do_not_want_to_change_the_note,
                        mutableString = _leaveAboveFieldEmptyIfYouDoNotWantToChangeTheNote,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "home",
                        localId = R.string.home,
                        mutableString = _home,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "shelf_name_already_exists",
                        localId = R.string.shelf_name_already_exists,
                        mutableString = _shelfNameAlreadyExists,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "newest_to_oldest",
                        localId = R.string.newest_to_oldest,
                        mutableString = _newestToOldest,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "oldest_to_newest",
                        localId = R.string.oldest_to_newest,
                        mutableString = _oldestToNewest,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "a_to_z_sequence",
                        localId = R.string.a_to_z_sequence,
                        mutableString = _aToZSequence,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "z_to_a_sequence",
                        localId = R.string.z_to_a_sequence,
                        mutableString = _ztoASequence,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "search",
                        localId = R.string.search,
                        mutableString = _search,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "collections",
                        localId = R.string.collections,
                        mutableString = _collections,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "settings",
                        localId = R.string.settings,
                        mutableString = _settings,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "links",
                        localId = R.string.links,
                        mutableString = _links,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "selected_folders_unarchived_successfully",
                        localId = R.string.selected_folders_unarchived_successfully,
                        mutableString = _selectedFoldersUnarchivedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "selected_links_deleted_successfully",
                        localId = R.string.selected_links_deleted_successfully,
                        mutableString = _selectedLinksDeletedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "selected_folders_deleted_successfully",
                        localId = R.string.selected_folders_deleted_successfully,
                        mutableString = _selectedFoldersDeletedSuccessfully,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "selected_links_unarchived_successfully",
                        localId = R.string.selected_links_unarchived_successfully,
                        mutableString = _selectedLinksUnarchivedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "link_unarchived_successfully",
                        localId = R.string.link_unarchived_successfully,
                        mutableString = _linkUnarchivedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "link_info_updated_successfully",
                        localId = R.string.link_info_updated_successfully,
                        mutableString = _linkInfoUpdatedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder_info_updated_successfully",
                        localId = R.string.folder_info_updated_successfully,
                        mutableString = _folderInfoUpdatedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "archived_link_deleted_successfully",
                        localId = R.string.archived_link_deleted_successfully,
                        mutableString = _archivedLinkDeletedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "deleted_the_note_successfully",
                        localId = R.string.deleted_the_note_successfully,
                        mutableString = _deletedTheNoteSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder_unarchived_successfully",
                        localId = R.string.folder_unarchived_successfully,
                        mutableString = _folderUnarchivedSuccessfully,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_links_were_archived",
                        localId = R.string.no_links_were_archived,
                        mutableString = _noLinksWereArchived,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_folders_were_archived",
                        localId = R.string.no_folders_were_archived,
                        mutableString = _noFoldersWereArchived,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "items_selected",
                        localId = R.string.items_selected,
                        mutableString = _itemsSelected,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "archive",
                        localId = R.string.archive,
                        mutableString = _archive,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "this_folder_does_not_contain_any_links_add_links_for_further_usage",
                        localId = R.string.this_folder_does_not_contain_any_links_add_links_for_further_usage,
                        mutableString = _thisFolderDoesNotContainAnyLinksAddLinksForFurtherUsage,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_links_were_found",
                        localId = R.string.no_links_were_found,
                        mutableString = _noLinksWereFound,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_important_links_were_found",
                        localId = R.string.no_important_links_were_found,
                        mutableString = _noImportantLinksWereFound,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_links_found_in_this_archived_folder",
                        localId = R.string.no_links_found_in_this_archived_folder,
                        mutableString = _noLinksFoundInThisArchivedFolder,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "deleted_the_link_successfully",
                        localId = R.string.deleted_the_link_successfully,
                        mutableString = _deletedTheLinkSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folders_selected",
                        localId = R.string.folders_selected,
                        mutableString = _foldersSelected,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "select_all_folders",
                        localId = R.string.select_all_folders,
                        mutableString = _selectAllFolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "selected_folders_archived_successfully",
                        localId = R.string.selected_folders_archived_successfully,
                        mutableString = _selectedFoldersArchivedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_link_added_to_the_folder",
                        localId = R.string.new_link_added_to_the_folder,
                        mutableString = _newLinkAddedToTheFolder,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_link_added_to_important_links",
                        localId = R.string.new_link_added_to_important_links,
                        mutableString = _newLinkAddedToImportantLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_link_added_to_saved_links",
                        localId = R.string.new_link_added_to_saved_links,
                        mutableString = _newLinkAddedToSavedLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder_archived_successfully",
                        localId = R.string.folder_archived_successfully,
                        mutableString = _folderArchivedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folder_created_successfully",
                        localId = R.string.folder_created_successfully,
                        mutableString = _folderCreatedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "deleted_the_folder",
                        localId = R.string.deleted_the_folder,
                        mutableString = _deletedTheFolder,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "removed_link_from_important_links_successfully",
                        localId = R.string.removed_link_from_important_links_successfully,
                        mutableString = _removedLinkFromImportantLinksSuccessfully,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "added_link_to_important_links",
                        localId = R.string.added_link_to_important_links,
                        mutableString = _addedLinkToImportantLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "welcome_back_to_linkora",
                        localId = R.string.welcome_back_to_linkora,
                        mutableString = _welcomeBackToLinkora,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "good_morning",
                        localId = R.string.good_morning,
                        mutableString = _goodMorning,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "good_afternoon",
                        localId = R.string.good_afternoon,
                        mutableString = _goodAfternoon,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "good_evening",
                        localId = R.string.good_evening,
                        mutableString = _goodEvening,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "good_night",
                        localId = R.string.good_night,
                        mutableString = _goodNight,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "hey_hi",
                        localId = R.string.hey_hi,
                        mutableString = _heyHi,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "default_shelf",
                        localId = R.string.default_shelf,
                        mutableString = _defaultShelf,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "and",
                        localId = R.string.and,
                        mutableString = _and,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "archived_folders",
                        localId = R.string.archived_folders,
                        mutableString = _archivedFolders,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "archived_links",
                        localId = R.string.archived_links,
                        mutableString = _archivedLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "history",
                        localId = R.string.history,
                        mutableString = _history,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "links_from_folders",
                        localId = R.string.links_from_folders,
                        mutableString = _linksFromFolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "search_titles_to_find_links_and_folders",
                        localId = R.string.search_titles_to_find_links_and_folders,
                        mutableString = _searchTitlesToFindLinksAndFolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "search_linkora_retrieve_all_the_links_you_saved",
                        localId = R.string.search_linkora_retrieve_all_the_links_you_saved,
                        mutableString = _searchLinkoraRetrieveAllTheLinksYouSaved,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_matching_items_found_try_a_different_search",
                        localId = R.string.no_matching_items_found_try_a_different_search,
                        mutableString = _noMatchingItemsFoundTryADifferentSearch,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "from_folders",
                        localId = R.string.from_folders,
                        mutableString = _fromFolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "from_saved_links",
                        localId = R.string.from_saved_links,
                        mutableString = _fromSavedLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "from_important_links",
                        localId = R.string.from_important_links,
                        mutableString = _fromImportantLinks,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "links_from_history",
                        localId = R.string.links_from_history,
                        mutableString = _linksFromHistory,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "links_from_archive",
                        localId = R.string.links_from_archive,
                        mutableString = _linksFromArchive,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "from_archived_folders",
                        localId = R.string.from_archived_folders,
                        mutableString = _fromArchivedFolders,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_links_were_found_in_history",
                        localId = R.string.no_links_were_found_in_history,
                        mutableString = _noLinksWereFoundInHistory,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "heads_up",
                        localId = R.string.heads_up,
                        mutableString = _headsUp,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "you_already_have_links_saved",
                        localId = R.string.you_already_have_links_saved,
                        mutableString = _youAlreadyHaveLinksSaved,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "export_data",
                        localId = R.string.export_data,
                        mutableString = _exportData,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_data_and_keep_the_existing_data",
                        localId = R.string.import_data_and_keep_the_existing_data,
                        mutableString = _importDataAndKeepTheExistingData,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_data_export_and_delete_the_existing_data",
                        localId = R.string.import_data_export_and_delete_the_existing_data,
                        mutableString = _importDataExportAndDeleteTheExistingData,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_data_and_delete_the_existing_data",
                        localId = R.string.import_data_and_delete_the_existing_data,
                        mutableString = _importDataAndDeleteTheExistingData,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "incompatible_file_type",
                        localId = R.string.incompatible_file_type,
                        mutableString = _incompatibleFileType,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "data_conversion_failed",
                        localId = R.string.data_conversion_failed,
                        mutableString = _dataConversionFailed,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "selected_file_does_not_match_linkora_schema",
                        localId = R.string.selected_file_does_not_match_linkora_schema,
                        mutableString = _selectedFileDoesNotMatchLinkoraSchema,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "there_was_an_issue_importing_the_links",
                        localId = R.string.there_was_an_issue_importing_the_links,
                        mutableString = _thereWasAnIssueImportingTheLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "choose_another_file",
                        localId = R.string.choose_another_file,
                        mutableString = _chooseAnotherFile,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "permission_denied_title",
                        localId = R.string.permission_denied_title,
                        mutableString = _permissionDeniedTitle,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "permission_is_denied_desc",
                        localId = R.string.permission_is_denied_desc,
                        mutableString = _permissionIsDeniedDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "go_to_settings",
                        localId = R.string.go_to_settings,
                        mutableString = _goToSettings,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "retrieving_latest_information",
                        localId = R.string.retrieving_latest_information,
                        mutableString = _retrievingLatestInformation,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "new_update_is_available",
                        localId = R.string.new_update_is_available,
                        mutableString = _newUpdateIsAvailable,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "current_version",
                        localId = R.string.current_version,
                        mutableString = _currentVersion,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "latest_version",
                        localId = R.string.latest_version,
                        mutableString = _latestVersion,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "linkora",
                        localId = R.string.app_name,
                        mutableString = _linkora,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "release_page_on_github",
                        localId = R.string.release_page_on_github,
                        mutableString = _releasePageOnGithub,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "redirect_to_latest_release_page",
                        localId = R.string.redirect_to_latest_release_page,
                        mutableString = _redirectToLatestReleasePage,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "download",
                        localId = R.string.download,
                        mutableString = _download,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "beta",
                        localId = R.string.beta,
                        mutableString = _beta,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "language",
                        localId = R.string.language,
                        mutableString = _language,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "app_language",
                        localId = R.string.app_language,
                        mutableString = _appLanguage,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "reset_app_language",
                        localId = R.string.reset_app_language,
                        mutableString = _resetAppLanguage,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "available_languages",
                        localId = R.string.available_languages,
                        mutableString = _availableLanguages,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "about",
                        localId = R.string.about,
                        mutableString = _about,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "check_for_latest_version",
                        localId = R.string.check_for_latest_version,
                        mutableString = _checkForLatestVersion,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "network_error",
                        localId = R.string.network_error,
                        mutableString = _networkError,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "is_now_available",
                        localId = R.string.is_now_available,
                        mutableString = _isNowAvailable,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "you_are_using_latest_version_of_linkora",
                        localId = R.string.you_are_using_latest_version_of_linkora,
                        mutableString = _youAreUsingLatestVersionOfLinkora,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "github_desc",
                        localId = R.string.github_desc,
                        mutableString = _githubDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "github",
                        localId = R.string.github,
                        mutableString = _github,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "twitter",
                        localId = R.string.twitter,
                        mutableString = _twitter,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "auto_check_for_updates",
                        localId = R.string.auto_check_for_updates,
                        mutableString = _autoCheckForUpdates,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "auto_check_for_updates_desc",
                        localId = R.string.auto_check_for_updates_desc,
                        mutableString = _autoCheckForUpdatesDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "acknowledgments",
                        localId = R.string.acknowledgments,
                        mutableString = _acknowledgments,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "data",
                        localId = R.string.data,
                        mutableString = _data,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_feature_is_polished_not_perfect_desc",
                        localId = R.string.import_feature_is_polished_not_perfect_desc,
                        mutableString = _importFeatureIsPolishedNotPerfectDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "successfully_exported",
                        localId = R.string.successfully_exported,
                        mutableString = _successfullyExported,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "privacy",
                        localId = R.string.privacy,
                        mutableString = _privacy,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "theme",
                        localId = R.string.theme,
                        mutableString = _theme,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "follow_system_theme",
                        localId = R.string.follow_system_theme,
                        mutableString = _followSystemTheme,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "use_dark_mode",
                        localId = R.string.use_dark_mode,
                        mutableString = _useDarkMode,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "use_dynamic_theming",
                        localId = R.string.use_dynamic_theming,
                        mutableString = _useDynamicTheming,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "use_dynamic_theming_desc",
                        localId = R.string.use_dynamic_theming_desc,
                        mutableString = _useDynamicThemingDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "kotlin",
                        localId = R.string.kotlin,
                        mutableString = _kotlin,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "apache_license",
                        localId = R.string.apache_license,
                        mutableString = _apacheLicense,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "android_jetpack",
                        localId = R.string.android_jetpack,
                        mutableString = _androidJetpack,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "coil",
                        localId = R.string.coil,
                        mutableString = _coil,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "material_design_3",
                        localId = R.string.material_design_3,
                        mutableString = _materialDesign3,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "material_icons",
                        localId = R.string.material_icons,
                        mutableString = _materialIcons,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "send_crash_reports",
                        localId = R.string.send_crash_reports,
                        mutableString = _sendCrashReports,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "use_in_app_browser",
                        localId = R.string.use_in_app_browser,
                        mutableString = _useInAppBrowser,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "use_in_app_browser_desc",
                        localId = R.string.use_in_app_browser_desc,
                        mutableString = _useInAppBrowserDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "enable_home_screen",
                        localId = R.string.enable_home_screen,
                        mutableString = _enableHomeScreen,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "enable_home_screen_desc",
                        localId = R.string.enable_home_screen_desc,
                        mutableString = _enableHomeScreenDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "auto_detect_title",
                        localId = R.string.auto_detect_title,
                        mutableString = _autoDetectTitle,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "auto_detect_title_desc",
                        localId = R.string.auto_detect_title_desc,
                        mutableString = _autoDetectTitleDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "show_description_for_settings",
                        localId = R.string.show_description_for_settings,
                        mutableString = _showDescriptionForSettings,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "show_description_for_settings_desc",
                        localId = R.string.show_description_for_settings_desc,
                        mutableString = _showDescriptionForSettingsDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_data",
                        localId = R.string.import_data,
                        mutableString = _importData,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "import_data_from_external_json_file",
                        localId = R.string.import_data_from_external_json_file,
                        mutableString = _importDataFromExternalJsonFile,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "export_data_desc",
                        localId = R.string.export_data_desc,
                        mutableString = _exportDataDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "add_new_panel_to_shelf",
                        localId = R.string.add_new_panel_to_shelf,
                        mutableString = _addNewPanelToShelf,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "panels_in_the_shelf",
                        localId = R.string.panels_in_the_shelf,
                        mutableString = _panelsInTheShelf,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_panels_found",
                        localId = R.string.no_panels_found,
                        mutableString = _noPanelsFound,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "shelf",
                        localId = R.string.shelf,
                        mutableString = _shelf,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "folders_listed_in_this_panel",
                        localId = R.string.folders_listed_in_this_panel,
                        mutableString = _foldersListedInThisPanel,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_folders_found_in_this_panel",
                        localId = R.string.no_folders_found_in_this_panel,
                        mutableString = _noFoldersFoundInThisPanel,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "you_can_add_the_following_folders_to_this_panel",
                        localId = R.string.you_can_add_the_following_folders_to_this_panel,
                        mutableString = _youCanAddTheFollowingFoldersToThisPanel,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "archived_folders_data_migrated_successfully",
                        localId = R.string.archived_folders_data_migrated_successfully,
                        mutableString = _archivedFoldersDataMigratedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "root_folders_data_migrated_successfully",
                        localId = R.string.root_folders_data_migrated_successfully,
                        mutableString = _rootFoldersDataMigratedSuccessfully,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "delete_entire_data_permanently",
                        localId = R.string.delete_entire_data_permanently,
                        mutableString = _deleteEntireDataPermanently,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "delete_entire_data_permanently_desc",
                        localId = R.string.delete_entire_data_permanently_desc,
                        mutableString = _deleteEntireDataPermanentlyDesc,
                        context = context
                    )
                },

                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "every_single_bit_of_data_is_stored_locally_on_your_device",
                        localId = R.string.every_single_bit_of_data_is_stored_locally_on_your_device,
                        mutableString = _everySingleBitOfDataIsStoredLocallyOnYourDevice,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "linkora_collects_data_related_to_app_crashes",
                        localId = R.string.linkora_collects_data_related_to_app_crashes,
                        mutableString = _linkoraCollectsDataRelatedToAppCrashes,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "permission_required_to_write_the_data",
                        localId = R.string.permission_required_to_write_the_data,
                        mutableString = _permissionRequiredToWriteTheData,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "deleted_entire_data_from_the_local_database",
                        localId = R.string.deleted_entire_data_from_the_local_database,
                        mutableString = _deletedEntireDataFromTheLocalDatabase,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "linkora_would_not_be_possible_without_the_following_open_source_software_libraries",
                        localId = R.string.linkora_would_not_be_possible_without_the_following_open_source_software_libraries,
                        mutableString = _linkoraWouldNotBePossibleWithoutTheFollowingOpenSourceSoftwareLibraries,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "no_folders_are_found_create_folders_for_better_organization_of_your_links",
                        localId = R.string.no_folders_are_found_create_folders_for_better_organization_of_your_links,
                        mutableString = _noFoldersAreFoundCreateFoldersForBetterOrganizationOfYourLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "sort_folders_by",
                        localId = R.string.sort_folders_by,
                        mutableString = _sortFoldersBy,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "general",
                        localId = R.string.general,
                        mutableString = _general,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "user_agent_desc",
                        localId = R.string.user_agent_desc,
                        mutableString = _userAgentDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "user_agent",
                        localId = R.string.primary_user_agent,
                        mutableString = _primaryUserAgent,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "refreshing_links",
                        localId = R.string.refreshing_links,
                        mutableString = _refreshingLinks,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "work_manager_desc",
                        localId = R.string.work_manager_desc,
                        mutableString = _workManagerDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "links_refreshed",
                        localId = R.string.links_refreshed,
                        mutableString = _linksRefreshed,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "refreshing_links_info",
                        localId = R.string.refreshing_links_info,
                        mutableString = _refreshingLinksInfo,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "refresh_all_links_titles_and_images",
                        localId = R.string.refresh_all_links_titles_and_images,
                        mutableString = _refreshAllLinksTitlesAndImages,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "refresh_all_links_titles_and_images_desc",
                        localId = R.string.refresh_all_links_titles_and_images_desc,
                        mutableString = _refreshAllLinksTitlesAndImagesDesc,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "of",
                        localId = R.string.of,
                        mutableString = _of,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "title_copied_to_clipboard",
                        localId = R.string.title_copied_to_clipboard,
                        mutableString = _titleCopiedToClipboard,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "view_note",
                        localId = R.string.view_note,
                        mutableString = _viewNote,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "rename",
                        localId = R.string.rename,
                        mutableString = _rename,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "refreshing_title_and_image",
                        localId = R.string.refreshing_title_and_image,
                        mutableString = _refreshingTitleAndImage,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "refresh_image_and_title",
                        localId = R.string.refresh_image_and_title,
                        mutableString = _refreshImageAndTitle,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "unarchive",
                        localId = R.string.unarchive,
                        mutableString = _unarchive,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "delete_the_note",
                        localId = R.string.delete_the_note,
                        mutableString = _deleteTheNote,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "delete_folder",
                        localId = R.string.delete_folder,
                        mutableString = _deleteFolder,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "delete_link",
                        localId = R.string.delete_link,
                        mutableString = _deleteLink,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "saved_note",
                        localId = R.string.saved_note,
                        mutableString = _savedNote,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "note_copied_to_clipboard",
                        localId = R.string.note_copied_to_clipboard,
                        mutableString = _noteCopiedToClipboard,
                        context = context
                    )
                },
                async {
                    loadStringsHelper(
                        translationsRepo = translationsRepo,
                        remoteStringID = "you_did_not_add_note_for_this",
                        localId = R.string.you_did_not_add_note_for_this,
                        mutableString = _youDidNotAddNoteForThis,
                        context = context
                    )
                }
            )
        }.invokeOnCompletion {
            linkoraLog(count.toString())
        }
    }
}