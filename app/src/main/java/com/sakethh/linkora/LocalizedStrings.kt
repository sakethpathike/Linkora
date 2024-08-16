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


    fun loadStrings(context: Context) {

        val translationsRepo =
            EntryPoints.get(context.applicationContext, TranslationRepoInstance::class.java)
                .getTranslationRepo()
        viewModelScope.launch {
            awaitAll(
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _sortHistoryLinksBy.value = (translationsRepo.getLocalizedStringValueFor(
                            "sort_history_links_by",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.sort_history_links_by)
                            }
                        })
                    } else {
                        _sortHistoryLinksBy.value =
                            context.getString(R.string.sort_history_links_by)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _sortBy.value = (translationsRepo.getLocalizedStringValueFor(
                            "sort_by", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.sort_by)
                            }
                        })
                    } else {
                        _sortBy.value = context.getString(R.string.sort_by)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _sortSavedLinksBy.value = (translationsRepo.getLocalizedStringValueFor(
                            "sort_saved_links_by", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.sort_saved_links_by)
                            }
                        })
                    } else {
                        _sortSavedLinksBy.value = context.getString(R.string.sort_saved_links_by)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _sortImportantLinksBy.value = (translationsRepo.getLocalizedStringValueFor(
                            "sort_important_links_by",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.sort_important_links_by)
                            }
                        })
                    } else {
                        _sortImportantLinksBy.value =
                            context.getString(R.string.sort_important_links_by)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _sortBasedOn.value = (translationsRepo.getLocalizedStringValueFor(
                            "sort_based_on", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.sort_based_on)
                            }
                        })
                    } else {
                        _sortBasedOn.value = context.getString(R.string.sort_based_on)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folders.value = (translationsRepo.getLocalizedStringValueFor(
                            "folders", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.folders)
                            }
                        })
                    } else {
                        _folders.value = context.getString(R.string.folders)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addANewLinkInImportantLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "add_a_new_link_in_important_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.add_a_new_link_in_important_links)
                                }
                            })
                    } else {
                        _addANewLinkInImportantLinks.value =
                            context.getString(R.string.add_a_new_link_in_important_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addANewLinkInSavedLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "add_a_new_link_in_saved_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.add_a_new_link_in_saved_links)
                                }
                            })
                    } else {
                        _addANewLinkInSavedLinks.value =
                            context.getString(R.string.add_a_new_link_in_saved_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addANewLinkIn.value = (translationsRepo.getLocalizedStringValueFor(
                            "add_a_new_link_in", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.add_a_new_link_in)
                            }
                        })
                    } else {
                        _addANewLinkIn.value = context.getString(R.string.add_a_new_link_in)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addANewLink.value = (translationsRepo.getLocalizedStringValueFor(
                            "add_a_new_link", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.add_a_new_link)
                            }
                        })
                    } else {
                        _addANewLink.value = context.getString(R.string.add_a_new_link)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linkAddress.value = (translationsRepo.getLocalizedStringValueFor(
                            "link_address", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.link_address)
                            }
                        })
                    } else {
                        _linkAddress.value = context.getString(R.string.link_address)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _titleForTheLink.value = (translationsRepo.getLocalizedStringValueFor(
                            "title_for_the_link", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.title_for_the_link)
                            }
                        })
                    } else {
                        _titleForTheLink.value = context.getString(R.string.title_for_the_link)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noteForSavingTheLink.value = (translationsRepo.getLocalizedStringValueFor(
                            "note_for_saving_the_link",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.note_for_saving_the_link)
                            }
                        })
                    } else {
                        _noteForSavingTheLink.value =
                            context.getString(R.string.note_for_saving_the_link)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _titleWillBeAutomaticallyDetected.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "title_will_be_automatically_detected",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.title_will_be_automatically_detected)
                                }
                            })
                    } else {
                        _titleWillBeAutomaticallyDetected.value =
                            context.getString(R.string.title_will_be_automatically_detected)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addIn.value = (translationsRepo.getLocalizedStringValueFor(
                            "add_in", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.add_in)
                            }
                        })
                    } else {
                        _addIn.value = context.getString(R.string.add_in)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _savedLinks.value = (translationsRepo.getLocalizedStringValueFor(
                            "saved_links", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.saved_links)
                            }
                        })
                    } else {
                        _savedLinks.value = context.getString(R.string.saved_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _importantLinks.value = (translationsRepo.getLocalizedStringValueFor(
                            "important_links", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.important_links)
                            }
                        })
                    } else {
                        _importantLinks.value = context.getString(R.string.important_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _forceAutoDetectTitle.value = (translationsRepo.getLocalizedStringValueFor(
                            "force_auto_detect_title",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.force_auto_detect_title)
                            }
                        })
                    } else {
                        _forceAutoDetectTitle.value =
                            context.getString(R.string.force_auto_detect_title)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _cancel.value = (translationsRepo.getLocalizedStringValueFor(
                            "cancel", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.cancel)
                            }
                        })
                    } else {
                        _cancel.value = context.getString(R.string.cancel)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _save.value = (translationsRepo.getLocalizedStringValueFor(
                            "save", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.save)
                            }
                        })
                    } else {
                        _save.value = context.getString(R.string.save)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _thisFolderHasNoSubfolders.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "this_folder_has_no_subfolders",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.this_folder_has_no_subfolders)
                                }
                            })
                    } else {
                        _thisFolderHasNoSubfolders.value =
                            context.getString(R.string.this_folder_has_no_subfolders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _saveInThisFolder.value = (translationsRepo.getLocalizedStringValueFor(
                            "save_in_this_folder", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.save_in_this_folder)
                            }
                        })
                    } else {
                        _saveInThisFolder.value = context.getString(R.string.save_in_this_folder)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addANewPanelToTheShelf.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "add_a_new_panel_to_the_shelf",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.add_a_new_panel_to_the_shelf)
                                }
                            })
                    } else {
                        _addANewPanelToTheShelf.value =
                            context.getString(R.string.add_a_new_panel_to_the_shelf)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _panelName.value = (translationsRepo.getLocalizedStringValueFor(
                            "panel_name", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.panel_name)
                            }
                        })
                    } else {
                        _panelName.value = context.getString(R.string.panel_name)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addNewPanel.value = (translationsRepo.getLocalizedStringValueFor(
                            "add_new_panel", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.add_new_panel)
                            }
                        })
                    } else {
                        _addNewPanel.value = context.getString(R.string.add_new_panel)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folderNameCannnotBeEmpty.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "folder_name_cannnot_be_empty",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.folder_name_cannnot_be_empty)
                                }
                            })
                    } else {
                        _folderNameCannnotBeEmpty.value =
                            context.getString(R.string.folder_name_cannnot_be_empty)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folderName.value = (translationsRepo.getLocalizedStringValueFor(
                            "folder_name", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.folder_name)
                            }
                        })
                    } else {
                        _folderName.value = context.getString(R.string.folder_name)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noteForCreatingTheFolder.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "note_for_creating_the_folder",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.note_for_creating_the_folder)
                                }
                            })
                    } else {
                        _noteForCreatingTheFolder.value =
                            context.getString(R.string.note_for_creating_the_folder)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _createANewFolderIn.value = (translationsRepo.getLocalizedStringValueFor(
                            "create_a_new_folder_in",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.create_a_new_folder_in)
                            }
                        })
                    } else {
                        _createANewFolderIn.value =
                            context.getString(R.string.create_a_new_folder_in)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _createANewFolder.value = (translationsRepo.getLocalizedStringValueFor(
                            "create_a_new_folder", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.create_a_new_folder)
                            }
                        })
                    } else {
                        _createANewFolder.value = context.getString(R.string.create_a_new_folder)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _create.value = (translationsRepo.getLocalizedStringValueFor(
                            "create", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.create)
                            }
                        })
                    } else {
                        _create.value = context.getString(R.string.create)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _areYouSureWantToDelete.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "are_you_sure_want_to_delete",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.are_you_sure_want_to_delete)
                                }
                            })
                    } else {
                        _areYouSureWantToDelete.value =
                            context.getString(R.string.are_you_sure_want_to_delete)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _permanentlyDeleteThePanel.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "permanently_delete_the_panel",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.permanently_delete_the_panel)
                                }
                            })
                    } else {
                        _permanentlyDeleteThePanel.value =
                            context.getString(R.string.permanently_delete_the_panel)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _onceDeletedThisPanelCannotBeRestarted.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "once_deleted_this_panel_cannot_be_restarted",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.once_deleted_this_panel_cannot_be_restarted)
                                }
                            })
                    } else {
                        _onceDeletedThisPanelCannotBeRestarted.value =
                            context.getString(R.string.once_deleted_this_panel_cannot_be_restarted)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteIt.value = (translationsRepo.getLocalizedStringValueFor(
                            "delete_it", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.delete_it)
                            }
                        })
                    } else {
                        _deleteIt.value = context.getString(R.string.delete_it)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _thisFolderDeletionWillAlsoDeleteAllItsInternalFolders.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "this_folder_deletion_will_also_delete_all_its_internal_folders",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.this_folder_deletion_will_also_delete_all_its_internal_folders)
                                }
                            })
                    } else {
                        _thisFolderDeletionWillAlsoDeleteAllItsInternalFolders.value =
                            context.getString(R.string.this_folder_deletion_will_also_delete_all_its_internal_folders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _areYouSureYouWantToDeleteAllSelectedLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "are_you_sure_you_want_to_delete_all_selected_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.are_you_sure_you_want_to_delete_all_selected_links)
                                }
                            })
                    } else {
                        _areYouSureYouWantToDeleteAllSelectedLinks.value =
                            context.getString(R.string.are_you_sure_you_want_to_delete_all_selected_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _areYouSureYouWantToDeleteTheLink.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "are_you_sure_you_want_to_delete_the_link",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.are_you_sure_you_want_to_delete_the_link)
                                }
                            })
                    } else {
                        _areYouSureYouWantToDeleteTheLink.value =
                            context.getString(R.string.are_you_sure_you_want_to_delete_the_link)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _areYouSureYouWantToDeleteAllSelectedFolders.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "are_you_sure_you_want_to_delete_all_selected_folders",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.are_you_sure_you_want_to_delete_all_selected_folders)
                                }
                            })
                    } else {
                        _areYouSureYouWantToDeleteAllSelectedFolders.value =
                            context.getString(R.string.are_you_sure_you_want_to_delete_all_selected_folders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _areYouSureWantToDeleteThe.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "are_you_sure_want_to_delete_the",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.are_you_sure_want_to_delete_the)
                                }
                            })
                    } else {
                        _areYouSureWantToDeleteThe.value =
                            context.getString(R.string.are_you_sure_want_to_delete_the)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folder.value = (translationsRepo.getLocalizedStringValueFor(
                            "folder", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.folder)
                            }
                        })
                    } else {
                        _folder.value = context.getString(R.string.folder)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _areYouSureYouWantToDeleteAllSelectedItems.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "are_you_sure_you_want_to_delete_all_selected_items",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.are_you_sure_you_want_to_delete_all_selected_items)
                                }
                            })
                    } else {
                        _areYouSureYouWantToDeleteAllSelectedItems.value =
                            context.getString(R.string.are_you_sure_you_want_to_delete_all_selected_items)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _areYouSureYouWantToDeleteAllFoldersAndLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "are_you_sure_you_want_to_delete_all_folders_and_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.are_you_sure_you_want_to_delete_all_folders_and_links)
                                }
                            })
                    } else {
                        _areYouSureYouWantToDeleteAllFoldersAndLinks.value =
                            context.getString(R.string.are_you_sure_you_want_to_delete_all_folders_and_links)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noActivityFoundToHandleIntent.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "no_activity_found_to_handle_intent",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.no_activity_found_to_handle_intent)
                                }
                            })
                    } else {
                        _noActivityFoundToHandleIntent.value =
                            context.getString(R.string.no_activity_found_to_handle_intent)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linkCopiedToTheClipboard.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "link_copied_to_the_clipboard",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.link_copied_to_the_clipboard)
                                }
                            })
                    } else {
                        _linkCopiedToTheClipboard.value =
                            context.getString(R.string.link_copied_to_the_clipboard)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _changePanelName.value = (translationsRepo.getLocalizedStringValueFor(
                            "change_panel_name", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.change_panel_name)
                            }
                        })
                    } else {
                        _changePanelName.value = context.getString(R.string.change_panel_name)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _edit.value = (translationsRepo.getLocalizedStringValueFor(
                            "edit", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.edit)
                            }
                        })
                    } else {
                        _edit.value = context.getString(R.string.edit)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newNameForPanel.value = (translationsRepo.getLocalizedStringValueFor(
                            "new_name_for_panel", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.new_name_for_panel)
                            }
                        })
                    } else {
                        _newNameForPanel.value = context.getString(R.string.new_name_for_panel)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _changeNoteOnly.value = (translationsRepo.getLocalizedStringValueFor(
                            "change_note_only", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.change_note_only)
                            }
                        })
                    } else {
                        _changeNoteOnly.value = context.getString(R.string.change_note_only)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _changeBothNameAndNote.value = (translationsRepo.getLocalizedStringValueFor(
                            "change_both_name_and_note",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.change_both_name_and_note)
                            }
                        })
                    } else {
                        _changeBothNameAndNote.value =
                            context.getString(R.string.change_both_name_and_note)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _titleCannotBeEmpty.value = (translationsRepo.getLocalizedStringValueFor(
                            "title_cannot_be_empty",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.title_cannot_be_empty)
                            }
                        })
                    } else {
                        _titleCannotBeEmpty.value =
                            context.getString(R.string.title_cannot_be_empty)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _changeLinkData.value = (translationsRepo.getLocalizedStringValueFor(
                            "change_link_data", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.change_link_data)
                            }
                        })
                    } else {
                        _changeLinkData.value = context.getString(R.string.change_link_data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newName.value = (translationsRepo.getLocalizedStringValueFor(
                            "new_name", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.new_name)
                            }
                        })
                    } else {
                        _newName.value = context.getString(R.string.new_name)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newTitle.value = (translationsRepo.getLocalizedStringValueFor(
                            "new_title", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.new_title)
                            }
                        })
                    } else {
                        _newTitle.value = context.getString(R.string.new_title)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newNote.value = (translationsRepo.getLocalizedStringValueFor(
                            "new_note", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.new_note)
                            }
                        })
                    } else {
                        _newNote.value = context.getString(R.string.new_note)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _leaveAboveFieldEmptyIfYouDoNotWantToChangeTheNote.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "leave_above_field_empty_if_you_do_not_want_to_change_the_note",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.leave_above_field_empty_if_you_do_not_want_to_change_the_note)
                                }
                            })
                    } else {
                        _leaveAboveFieldEmptyIfYouDoNotWantToChangeTheNote.value =
                            context.getString(R.string.leave_above_field_empty_if_you_do_not_want_to_change_the_note)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _home.value = (translationsRepo.getLocalizedStringValueFor(
                            "home", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.home)
                            }
                        })
                    } else {
                        _home.value = context.getString(R.string.home)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _shelfNameAlreadyExists.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "shelf_name_already_exists",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.shelf_name_already_exists)
                                }
                            })
                    } else {
                        _shelfNameAlreadyExists.value =
                            context.getString(R.string.shelf_name_already_exists)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newestToOldest.value = (translationsRepo.getLocalizedStringValueFor(
                            "newest_to_oldest", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.newest_to_oldest)
                            }
                        })
                    } else {
                        _newestToOldest.value = context.getString(R.string.newest_to_oldest)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _oldestToNewest.value = (translationsRepo.getLocalizedStringValueFor(
                            "oldest_to_newest", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.oldest_to_newest)
                            }
                        })
                    } else {
                        _oldestToNewest.value = context.getString(R.string.oldest_to_newest)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _aToZSequence.value = (translationsRepo.getLocalizedStringValueFor(
                            "a_to_z_sequence", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.a_to_z_sequence)
                            }
                        })
                    } else {
                        _aToZSequence.value = context.getString(R.string.a_to_z_sequence)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _ztoASequence.value = (translationsRepo.getLocalizedStringValueFor(
                            "z_to_a_sequence", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.z_to_a_sequence)
                            }
                        })
                    } else {
                        _ztoASequence.value = context.getString(R.string.z_to_a_sequence)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _search.value = (translationsRepo.getLocalizedStringValueFor(
                            "search", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.search)
                            }
                        })
                    } else {
                        _search.value = context.getString(R.string.search)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _collections.value = (translationsRepo.getLocalizedStringValueFor(
                            "collections", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.collections)
                            }
                        })
                    } else {
                        _collections.value = context.getString(R.string.collections)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _settings.value = (translationsRepo.getLocalizedStringValueFor(
                            "settings", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.settings)
                            }
                        })
                    } else {
                        _settings.value = context.getString(R.string.settings)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _links.value = (translationsRepo.getLocalizedStringValueFor(
                            "links", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.links)
                            }
                        })
                    } else {
                        _links.value = context.getString(R.string.links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _selectedFoldersUnarchivedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "selected_folders_unarchived_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.selected_folders_unarchived_successfully)
                                }
                            })
                    } else {
                        _selectedFoldersUnarchivedSuccessfully.value =
                            context.getString(R.string.selected_folders_unarchived_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _selectedLinksDeletedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "selected_links_deleted_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.selected_links_deleted_successfully)
                                }
                            })
                    } else {
                        _selectedLinksDeletedSuccessfully.value =
                            context.getString(R.string.selected_links_deleted_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _selectedFoldersDeletedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "selected_folders_deleted_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.selected_folders_deleted_successfully)
                                }
                            })
                    } else {
                        _selectedFoldersDeletedSuccessfully.value =
                            context.getString(R.string.selected_folders_deleted_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _selectedLinksUnarchivedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "selected_links_unarchived_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.selected_links_unarchived_successfully)
                                }
                            })
                    } else {
                        _selectedLinksUnarchivedSuccessfully.value =
                            context.getString(R.string.selected_links_unarchived_successfully)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linkUnarchivedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "link_unarchived_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.link_unarchived_successfully)
                                }
                            })
                    } else {
                        _linkUnarchivedSuccessfully.value =
                            context.getString(R.string.link_unarchived_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linkInfoUpdatedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "link_info_updated_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.link_info_updated_successfully)
                                }
                            })
                    } else {
                        _linkInfoUpdatedSuccessfully.value =
                            context.getString(R.string.link_info_updated_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folderInfoUpdatedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "folder_info_updated_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.folder_info_updated_successfully)
                                }
                            })
                    } else {
                        _folderInfoUpdatedSuccessfully.value =
                            context.getString(R.string.folder_info_updated_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _archivedLinkDeletedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "archived_link_deleted_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.archived_link_deleted_successfully)
                                }
                            })
                    } else {
                        _archivedLinkDeletedSuccessfully.value =
                            context.getString(R.string.archived_link_deleted_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deletedTheNoteSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "deleted_the_note_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.deleted_the_note_successfully)
                                }
                            })
                    } else {
                        _deletedTheNoteSuccessfully.value =
                            context.getString(R.string.deleted_the_note_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folderUnarchivedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "folder_unarchived_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.folder_unarchived_successfully)
                                }
                            })
                    } else {
                        _folderUnarchivedSuccessfully.value =
                            context.getString(R.string.folder_unarchived_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noLinksWereArchived.value = (translationsRepo.getLocalizedStringValueFor(
                            "no_links_were_archived",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.no_links_were_archived)
                            }
                        })
                    } else {
                        _noLinksWereArchived.value =
                            context.getString(R.string.no_links_were_archived)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noFoldersWereArchived.value = (translationsRepo.getLocalizedStringValueFor(
                            "no_folders_were_archived",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.no_folders_were_archived)
                            }
                        })
                    } else {
                        _noFoldersWereArchived.value =
                            context.getString(R.string.no_folders_were_archived)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _itemsSelected.value = (translationsRepo.getLocalizedStringValueFor(
                            "items_selected", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.items_selected)
                            }
                        })
                    } else {
                        _itemsSelected.value = context.getString(R.string.items_selected)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _archive.value = (translationsRepo.getLocalizedStringValueFor(
                            "archive", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.archive)
                            }
                        })
                    } else {
                        _archive.value = context.getString(R.string.archive)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _thisFolderDoesNotContainAnyLinksAddLinksForFurtherUsage.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "this_folder_does_not_contain_any_links_add_links_for_further_usage",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.this_folder_does_not_contain_any_links_add_links_for_further_usage)
                                }
                            })
                    } else {
                        _thisFolderDoesNotContainAnyLinksAddLinksForFurtherUsage.value =
                            context.getString(R.string.this_folder_does_not_contain_any_links_add_links_for_further_usage)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noLinksWereFound.value = (translationsRepo.getLocalizedStringValueFor(
                            "no_links_were_found", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.no_links_were_found)
                            }
                        })
                    } else {
                        _noLinksWereFound.value = context.getString(R.string.no_links_were_found)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noImportantLinksWereFound.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "no_important_links_were_found",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.no_important_links_were_found)
                                }
                            })
                    } else {
                        _noImportantLinksWereFound.value =
                            context.getString(R.string.no_important_links_were_found)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noLinksFoundInThisArchivedFolder.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "no_links_found_in_this_archived_folder",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.no_links_found_in_this_archived_folder)
                                }
                            })
                    } else {
                        _noLinksFoundInThisArchivedFolder.value =
                            context.getString(R.string.no_links_found_in_this_archived_folder)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deletedTheLinkSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "deleted_the_link_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.deleted_the_link_successfully)
                                }
                            })
                    } else {
                        _deletedTheLinkSuccessfully.value =
                            context.getString(R.string.deleted_the_link_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _foldersSelected.value = (translationsRepo.getLocalizedStringValueFor(
                            "folders_selected", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.folders_selected)
                            }
                        })
                    } else {
                        _foldersSelected.value = context.getString(R.string.folders_selected)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _selectAllFolders.value = (translationsRepo.getLocalizedStringValueFor(
                            "select_all_folders", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.select_all_folders)
                            }
                        })
                    } else {
                        _selectAllFolders.value = context.getString(R.string.select_all_folders)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _selectedFoldersArchivedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "selected_folders_archived_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.selected_folders_archived_successfully)
                                }
                            })
                    } else {
                        _selectedFoldersArchivedSuccessfully.value =
                            context.getString(R.string.selected_folders_archived_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newLinkAddedToTheFolder.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "new_link_added_to_the_folder",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.new_link_added_to_the_folder)
                                }
                            })
                    } else {
                        _newLinkAddedToTheFolder.value =
                            context.getString(R.string.new_link_added_to_the_folder)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newLinkAddedToImportantLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "new_link_added_to_important_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.new_link_added_to_important_links)
                                }
                            })
                    } else {
                        _newLinkAddedToImportantLinks.value =
                            context.getString(R.string.new_link_added_to_important_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newLinkAddedToSavedLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "new_link_added_to_saved_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.new_link_added_to_saved_links)
                                }
                            })
                    } else {
                        _newLinkAddedToSavedLinks.value =
                            context.getString(R.string.new_link_added_to_saved_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folderArchivedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "folder_archived_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.folder_archived_successfully)
                                }
                            })
                    } else {
                        _folderArchivedSuccessfully.value =
                            context.getString(R.string.folder_archived_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _folderCreatedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "folder_created_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.folder_created_successfully)
                                }
                            })
                    } else {
                        _folderCreatedSuccessfully.value =
                            context.getString(R.string.folder_created_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deletedTheFolder.value = (translationsRepo.getLocalizedStringValueFor(
                            "deleted_the_folder", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.deleted_the_folder)
                            }
                        })
                    } else {
                        _deletedTheFolder.value = context.getString(R.string.deleted_the_folder)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _removedLinkFromImportantLinksSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "removed_link_from_important_links_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.removed_link_from_important_links_successfully)
                                }
                            })
                    } else {
                        _removedLinkFromImportantLinksSuccessfully.value =
                            context.getString(R.string.removed_link_from_important_links_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addedLinkToImportantLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "added_link_to_important_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.added_link_to_important_links)
                                }
                            })
                    } else {
                        _addedLinkToImportantLinks.value =
                            context.getString(R.string.added_link_to_important_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _welcomeBackToLinkora.value = (translationsRepo.getLocalizedStringValueFor(
                            "welcome_back_to_linkora",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.welcome_back_to_linkora)
                            }
                        })
                    } else {
                        _welcomeBackToLinkora.value =
                            context.getString(R.string.welcome_back_to_linkora)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _goodMorning.value = (translationsRepo.getLocalizedStringValueFor(
                            "good_morning", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.good_morning)
                            }
                        })
                    } else {
                        _goodMorning.value = context.getString(R.string.good_morning)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _goodAfternoon.value = (translationsRepo.getLocalizedStringValueFor(
                            "good_afternoon", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.good_afternoon)
                            }
                        })
                    } else {
                        _goodAfternoon.value = context.getString(R.string.good_afternoon)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _goodEvening.value = (translationsRepo.getLocalizedStringValueFor(
                            "good_evening", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.good_evening)
                            }
                        })
                    } else {
                        _goodEvening.value = context.getString(R.string.good_evening)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _goodNight.value = (translationsRepo.getLocalizedStringValueFor(
                            "good_night", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.good_night)
                            }
                        })
                    } else {
                        _goodNight.value = context.getString(R.string.good_night)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _heyHi.value = (translationsRepo.getLocalizedStringValueFor(
                            "hey_hi", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.hey_hi)
                            }
                        })
                    } else {
                        _heyHi.value = context.getString(R.string.hey_hi)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _defaultShelf.value = (translationsRepo.getLocalizedStringValueFor(
                            "default_shelf", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.default_shelf)
                            }
                        })
                    } else {
                        _defaultShelf.value = context.getString(R.string.default_shelf)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _toAddFoldersIntoThisPanelClickOnThe.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "to_add_folders_into_this_panel_click_on_the",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.to_add_folders_into_this_panel_click_on_the)
                                }
                            })
                    } else {
                        _toAddFoldersIntoThisPanelClickOnThe.value =
                            context.getString(R.string.to_add_folders_into_this_panel_click_on_the)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _atThe.value = (translationsRepo.getLocalizedStringValueFor(
                            "at_the", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.at_the)
                            }
                        })
                    } else {
                        _atThe.value = context.getString(R.string.at_the)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _bottomOfTheShelf.value = (translationsRepo.getLocalizedStringValueFor(
                            "bottom_of_the_shelf", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.bottom_of_the_shelf)
                            }
                        })
                    } else {
                        _bottomOfTheShelf.value = context.getString(R.string.bottom_of_the_shelf)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _period.value = (translationsRepo.getLocalizedStringValueFor(
                            "period", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.period)
                            }
                        })
                    } else {
                        _period.value = context.getString(R.string.period)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _and.value = (translationsRepo.getLocalizedStringValueFor(
                            "and", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.and)
                            }
                        })
                    } else {
                        _and.value = context.getString(R.string.and)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _canBeAccessedFromThe.value = (translationsRepo.getLocalizedStringValueFor(
                            "can_be_accessed_from_the",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.can_be_accessed_from_the)
                            }
                        })
                    } else {
                        _canBeAccessedFromThe.value =
                            context.getString(R.string.can_be_accessed_from_the)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _defaultPanelDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "default_panel_desc", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.default_panel_desc)
                            }
                        })
                    } else {
                        _defaultPanelDesc.value = context.getString(R.string.default_panel_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _archivedFolders.value = (translationsRepo.getLocalizedStringValueFor(
                            "archived_folders", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.archived_folders)
                            }
                        })
                    } else {
                        _archivedFolders.value = context.getString(R.string.archived_folders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _archivedLinks.value = (translationsRepo.getLocalizedStringValueFor(
                            "archived_links", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.archived_links)
                            }
                        })
                    } else {
                        _archivedLinks.value = context.getString(R.string.archived_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _history.value = (translationsRepo.getLocalizedStringValueFor(
                            "history", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.history)
                            }
                        })
                    } else {
                        _history.value = context.getString(R.string.history)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linksFromFolders.value = (translationsRepo.getLocalizedStringValueFor(
                            "links_from_folders", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.links_from_folders)
                            }
                        })
                    } else {
                        _linksFromFolders.value = context.getString(R.string.links_from_folders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _searchTitlesToFindLinksAndFolders.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "search_titles_to_find_links_and_folders",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.search_titles_to_find_links_and_folders)
                                }
                            })
                    } else {
                        _searchTitlesToFindLinksAndFolders.value =
                            context.getString(R.string.search_titles_to_find_links_and_folders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _searchLinkoraRetrieveAllTheLinksYouSaved.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "search_linkora_retrieve_all_the_links_you_saved",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.search_linkora_retrieve_all_the_links_you_saved)
                                }
                            })
                    } else {
                        _searchLinkoraRetrieveAllTheLinksYouSaved.value =
                            context.getString(R.string.search_linkora_retrieve_all_the_links_you_saved)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noMatchingItemsFoundTryADifferentSearch.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "no_matching_items_found_try_a_different_search",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.no_matching_items_found_try_a_different_search)
                                }
                            })
                    } else {
                        _noMatchingItemsFoundTryADifferentSearch.value =
                            context.getString(R.string.no_matching_items_found_try_a_different_search)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _fromFolders.value = (translationsRepo.getLocalizedStringValueFor(
                            "from_folders", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.from_folders)
                            }
                        })
                    } else {
                        _fromFolders.value = context.getString(R.string.from_folders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _fromSavedLinks.value = (translationsRepo.getLocalizedStringValueFor(
                            "from_saved_links", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.from_saved_links)
                            }
                        })
                    } else {
                        _fromSavedLinks.value = context.getString(R.string.from_saved_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _fromImportantLinks.value = (translationsRepo.getLocalizedStringValueFor(
                            "from_important_links",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.from_important_links)
                            }
                        })
                    } else {
                        _fromImportantLinks.value = context.getString(R.string.from_important_links)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linksFromHistory.value = (translationsRepo.getLocalizedStringValueFor(
                            "links_from_history", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.links_from_history)
                            }
                        })
                    } else {
                        _linksFromHistory.value = context.getString(R.string.links_from_history)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linksFromArchive.value = (translationsRepo.getLocalizedStringValueFor(
                            "links_from_archive", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.links_from_archive)
                            }
                        })
                    } else {
                        _linksFromArchive.value = context.getString(R.string.links_from_archive)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _fromArchivedFolders.value = (translationsRepo.getLocalizedStringValueFor(
                            "from_archived_folders",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.from_archived_folders)
                            }
                        })
                    } else {
                        _fromArchivedFolders.value =
                            context.getString(R.string.from_archived_folders)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noLinksWereFoundInHistory.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "no_links_were_found_in_history",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.no_links_were_found_in_history)
                                }
                            })
                    } else {
                        _noLinksWereFoundInHistory.value =
                            context.getString(R.string.no_links_were_found_in_history)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _headsUp.value = (translationsRepo.getLocalizedStringValueFor(
                            "heads_up", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.heads_up)
                            }
                        })
                    } else {
                        _headsUp.value = context.getString(R.string.heads_up)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _youAlreadyHaveLinksSaved.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "you_already_have_links_saved",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.you_already_have_links_saved)
                                }
                            })
                    } else {
                        _youAlreadyHaveLinksSaved.value =
                            context.getString(R.string.you_already_have_links_saved)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _exportData.value = (translationsRepo.getLocalizedStringValueFor(
                            "export_data", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.export_data)
                            }
                        })
                    } else {
                        _exportData.value = context.getString(R.string.export_data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _importDataAndKeepTheExistingData.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "import_data_and_keep_the_existing_data",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.import_data_and_keep_the_existing_data)
                                }
                            })
                    } else {
                        _importDataAndKeepTheExistingData.value =
                            context.getString(R.string.import_data_and_keep_the_existing_data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _importDataExportAndDeleteTheExistingData.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "import_data_export_and_delete_the_existing_data",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.import_data_export_and_delete_the_existing_data)
                                }
                            })
                    } else {
                        _importDataExportAndDeleteTheExistingData.value =
                            context.getString(R.string.import_data_export_and_delete_the_existing_data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _importDataAndDeleteTheExistingData.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "import_data_and_delete_the_existing_data",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.import_data_and_delete_the_existing_data)
                                }
                            })
                    } else {
                        _importDataAndDeleteTheExistingData.value =
                            context.getString(R.string.import_data_and_delete_the_existing_data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _incompatibleFileType.value = (translationsRepo.getLocalizedStringValueFor(
                            "incompatible_file_type",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.incompatible_file_type)
                            }
                        })
                    } else {
                        _incompatibleFileType.value =
                            context.getString(R.string.incompatible_file_type)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _dataConversionFailed.value = (translationsRepo.getLocalizedStringValueFor(
                            "data_conversion_failed",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.data_conversion_failed)
                            }
                        })
                    } else {
                        _dataConversionFailed.value =
                            context.getString(R.string.data_conversion_failed)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _selectedFileDoesNotMatchLinkoraSchema.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "selected_file_does_not_match_linkora_schema",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.selected_file_does_not_match_linkora_schema)
                                }
                            })
                    } else {
                        _selectedFileDoesNotMatchLinkoraSchema.value =
                            context.getString(R.string.selected_file_does_not_match_linkora_schema)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _thereWasAnIssueImportingTheLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "there_was_an_issue_importing_the_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.there_was_an_issue_importing_the_links)
                                }
                            })
                    } else {
                        _thereWasAnIssueImportingTheLinks.value =
                            context.getString(R.string.there_was_an_issue_importing_the_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _chooseAnotherFile.value = (translationsRepo.getLocalizedStringValueFor(
                            "choose_another_file", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.choose_another_file)
                            }
                        })
                    } else {
                        _chooseAnotherFile.value = context.getString(R.string.choose_another_file)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _permissionDeniedTitle.value = (translationsRepo.getLocalizedStringValueFor(
                            "permission_denied_title",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.permission_denied_title)
                            }
                        })
                    } else {
                        _permissionDeniedTitle.value =
                            context.getString(R.string.permission_denied_title)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _permissionIsDeniedDesc.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "permission_is_denied_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.permission_is_denied_desc)
                                }
                            })
                    } else {
                        _permissionIsDeniedDesc.value =
                            context.getString(R.string.permission_is_denied_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _goToSettings.value = (translationsRepo.getLocalizedStringValueFor(
                            "go_to_settings", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.go_to_settings)
                            }
                        })
                    } else {
                        _goToSettings.value = context.getString(R.string.go_to_settings)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _retrievingLatestInformation.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "retrieving_latest_information",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.retrieving_latest_information)
                                }
                            })
                    } else {
                        _retrievingLatestInformation.value =
                            context.getString(R.string.retrieving_latest_information)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _newUpdateIsAvailable.value = (translationsRepo.getLocalizedStringValueFor(
                            "new_update_is_available",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.new_update_is_available)
                            }
                        })
                    } else {
                        _newUpdateIsAvailable.value =
                            context.getString(R.string.new_update_is_available)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _currentVersion.value = (translationsRepo.getLocalizedStringValueFor(
                            "current_version", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.current_version)
                            }
                        })
                    } else {
                        _currentVersion.value = context.getString(R.string.current_version)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _latestVersion.value = (translationsRepo.getLocalizedStringValueFor(
                            "latest_version", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.latest_version)
                            }
                        })
                    } else {
                        _latestVersion.value = context.getString(R.string.latest_version)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linkora.value = (translationsRepo.getLocalizedStringValueFor(
                            "linkora", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.app_name)
                            }
                        })
                    } else {
                        _linkora.value = context.getString(R.string.app_name)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _releasePageOnGithub.value = (translationsRepo.getLocalizedStringValueFor(
                            "release_page_on_github",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.release_page_on_github)
                            }
                        })
                    } else {
                        _releasePageOnGithub.value =
                            context.getString(R.string.release_page_on_github)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _redirectToLatestReleasePage.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "redirect_to_latest_release_page",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.redirect_to_latest_release_page)
                                }
                            })
                    } else {
                        _redirectToLatestReleasePage.value =
                            context.getString(R.string.redirect_to_latest_release_page)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _download.value = (translationsRepo.getLocalizedStringValueFor(
                            "download", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.download)
                            }
                        })
                    } else {
                        _download.value = context.getString(R.string.download)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _beta.value = (translationsRepo.getLocalizedStringValueFor(
                            "beta", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.beta)
                            }
                        })
                    } else {
                        _beta.value = context.getString(R.string.beta)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _language.value = (translationsRepo.getLocalizedStringValueFor(
                            "language", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.language)
                            }
                        })
                    } else {
                        _language.value = context.getString(R.string.language)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _appLanguage.value = (translationsRepo.getLocalizedStringValueFor(
                            "app_language", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.app_language)
                            }
                        })
                    } else {
                        _appLanguage.value = context.getString(R.string.app_language)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _resetAppLanguage.value = (translationsRepo.getLocalizedStringValueFor(
                            "reset_app_language", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.reset_app_language)
                            }
                        })
                    } else {
                        _resetAppLanguage.value = context.getString(R.string.reset_app_language)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _availableLanguages.value = (translationsRepo.getLocalizedStringValueFor(
                            "available_languages", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.available_languages)
                            }
                        })
                    } else {
                        _availableLanguages.value = context.getString(R.string.available_languages)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _representsSwitchingToTheRespectiveLanguage.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "represents_switching_to_the_respective_language",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.represents_switching_to_the_respective_language)
                                }
                            })
                    } else {
                        _representsSwitchingToTheRespectiveLanguage.value =
                            context.getString(R.string.represents_switching_to_the_respective_language)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _contributingToTheLanguageStrings.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "contributing_to_the_language_strings",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.contributing_to_the_language_strings)
                                }
                            })
                    } else {
                        _contributingToTheLanguageStrings.value =
                            context.getString(R.string.contributing_to_the_language_strings)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _about.value = (translationsRepo.getLocalizedStringValueFor(
                            "about", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.about)
                            }
                        })
                    } else {
                        _about.value = context.getString(R.string.about)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _checkForLatestVersion.value = (translationsRepo.getLocalizedStringValueFor(
                            "check_for_latest_version",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.check_for_latest_version)
                            }
                        })
                    } else {
                        _checkForLatestVersion.value =
                            context.getString(R.string.check_for_latest_version)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _networkError.value = (translationsRepo.getLocalizedStringValueFor(
                            "network_error", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.network_error)
                            }
                        })
                    } else {
                        _networkError.value = context.getString(R.string.network_error)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _isNowAvailable.value = (translationsRepo.getLocalizedStringValueFor(
                            "is_now_available", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.is_now_available)
                            }
                        })
                    } else {
                        _isNowAvailable.value = context.getString(R.string.is_now_available)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _youAreUsingLatestVersionOfLinkora.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "you_are_using_latest_version_of_linkora",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.you_are_using_latest_version_of_linkora)
                                }
                            })
                    } else {
                        _youAreUsingLatestVersionOfLinkora.value =
                            context.getString(R.string.you_are_using_latest_version_of_linkora)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _githubDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "github_desc", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.github_desc)
                            }
                        })
                    } else {
                        _githubDesc.value = context.getString(R.string.github_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _github.value = (translationsRepo.getLocalizedStringValueFor(
                            "github", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.github)
                            }
                        })
                    } else {
                        _github.value = context.getString(R.string.github)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _follow.value = (translationsRepo.getLocalizedStringValueFor(
                            "follow", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.follow)
                            }
                        })
                    } else {
                        _follow.value = context.getString(R.string.follow)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _twitterDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "twitter_desc", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.twitter_desc)
                            }
                        })
                    } else {
                        _twitterDesc.value = context.getString(R.string.twitter_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _twitter.value = (translationsRepo.getLocalizedStringValueFor(
                            "twitter", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.twitter)
                            }
                        })
                    } else {
                        _twitter.value = context.getString(R.string.twitter)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _autoCheckForUpdates.value = (translationsRepo.getLocalizedStringValueFor(
                            "auto_check_for_updates",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.auto_check_for_updates)
                            }
                        })
                    } else {
                        _autoCheckForUpdates.value =
                            context.getString(R.string.auto_check_for_updates)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _autoCheckForUpdatesDesc.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "auto_check_for_updates_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.auto_check_for_updates_desc)
                                }
                            })
                    } else {
                        _autoCheckForUpdatesDesc.value =
                            context.getString(R.string.auto_check_for_updates_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _acknowledgments.value = (translationsRepo.getLocalizedStringValueFor(
                            "acknowledgments", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.acknowledgments)
                            }
                        })
                    } else {
                        _acknowledgments.value = context.getString(R.string.acknowledgments)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _data.value = (translationsRepo.getLocalizedStringValueFor(
                            "data", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.data)
                            }
                        })
                    } else {
                        _data.value = context.getString(R.string.data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _importFeatureIsPolishedNotPerfectDesc.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "import_feature_is_polished_not_perfect_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.import_feature_is_polished_not_perfect_desc)
                                }
                            })
                    } else {
                        _importFeatureIsPolishedNotPerfectDesc.value =
                            context.getString(R.string.import_feature_is_polished_not_perfect_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _successfullyExported.value = (translationsRepo.getLocalizedStringValueFor(
                            "successfully_exported",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.successfully_exported)
                            }
                        })
                    } else {
                        _successfullyExported.value =
                            context.getString(R.string.successfully_exported)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _privacy.value = (translationsRepo.getLocalizedStringValueFor(
                            "privacy", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.privacy)
                            }
                        })
                    } else {
                        _privacy.value = context.getString(R.string.privacy)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _theme.value = (translationsRepo.getLocalizedStringValueFor(
                            "theme", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.theme)
                            }
                        })
                    } else {
                        _theme.value = context.getString(R.string.theme)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _followSystemTheme.value = (translationsRepo.getLocalizedStringValueFor(
                            "follow_system_theme", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.follow_system_theme)
                            }
                        })
                    } else {
                        _followSystemTheme.value = context.getString(R.string.follow_system_theme)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useDarkMode.value = (translationsRepo.getLocalizedStringValueFor(
                            "use_dark_mode", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.use_dark_mode)
                            }
                        })
                    } else {
                        _useDarkMode.value = context.getString(R.string.use_dark_mode)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useDynamicTheming.value = (translationsRepo.getLocalizedStringValueFor(
                            "use_dynamic_theming", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.use_dynamic_theming)
                            }
                        })
                    } else {
                        _useDynamicTheming.value = context.getString(R.string.use_dynamic_theming)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useDynamicThemingDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "use_dynamic_theming_desc",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.use_dynamic_theming_desc)
                            }
                        })
                    } else {
                        _useDynamicThemingDesc.value =
                            context.getString(R.string.use_dynamic_theming_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _kotlin.value = (translationsRepo.getLocalizedStringValueFor(
                            "kotlin", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.kotlin)
                            }
                        })
                    } else {
                        _kotlin.value = context.getString(R.string.kotlin)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _apacheLicense.value = (translationsRepo.getLocalizedStringValueFor(
                            "apache_license", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.apache_license)
                            }
                        })
                    } else {
                        _apacheLicense.value = context.getString(R.string.apache_license)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _androidJetpack.value = (translationsRepo.getLocalizedStringValueFor(
                            "android_jetpack", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.android_jetpack)
                            }
                        })
                    } else {
                        _androidJetpack.value = context.getString(R.string.android_jetpack)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _coil.value = (translationsRepo.getLocalizedStringValueFor(
                            "coil", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.coil)
                            }
                        })
                    } else {
                        _coil.value = context.getString(R.string.coil)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _materialDesign3.value = (translationsRepo.getLocalizedStringValueFor(
                            "material_design_3", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.material_design_3)
                            }
                        })
                    } else {
                        _materialDesign3.value = context.getString(R.string.material_design_3)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _materialIcons.value = (translationsRepo.getLocalizedStringValueFor(
                            "material_icons", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.material_icons)
                            }
                        })
                    } else {
                        _materialIcons.value = context.getString(R.string.material_icons)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _sendCrashReports.value = (translationsRepo.getLocalizedStringValueFor(
                            "send_crash_reports", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.send_crash_reports)
                            }
                        })
                    } else {
                        _sendCrashReports.value = context.getString(R.string.send_crash_reports)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useInAppBrowser.value = (translationsRepo.getLocalizedStringValueFor(
                            "use_in_app_browser", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.use_in_app_browser)
                            }
                        })
                    } else {
                        _useInAppBrowser.value = context.getString(R.string.use_in_app_browser)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useInAppBrowserDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "use_in_app_browser_desc",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.use_in_app_browser_desc)
                            }
                        })
                    } else {
                        _useInAppBrowserDesc.value =
                            context.getString(R.string.use_in_app_browser_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _enableHomeScreen.value = (translationsRepo.getLocalizedStringValueFor(
                            "enable_home_screen", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.enable_home_screen)
                            }
                        })
                    } else {
                        _enableHomeScreen.value = context.getString(R.string.enable_home_screen)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _enableHomeScreenDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "enable_home_screen_desc",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.enable_home_screen_desc)
                            }
                        })
                    } else {
                        _enableHomeScreenDesc.value =
                            context.getString(R.string.enable_home_screen_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _autoDetectTitle.value = (translationsRepo.getLocalizedStringValueFor(
                            "auto_detect_title", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.auto_detect_title)
                            }
                        })
                    } else {
                        _autoDetectTitle.value = context.getString(R.string.auto_detect_title)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _autoDetectTitleDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "auto_detect_title_desc",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.auto_detect_title_desc)
                            }
                        })
                    } else {
                        _autoDetectTitleDesc.value =
                            context.getString(R.string.auto_detect_title_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _showDescriptionForSettings.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "show_description_for_settings",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.show_description_for_settings)
                                }
                            })
                    } else {
                        _showDescriptionForSettings.value =
                            context.getString(R.string.show_description_for_settings)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _showDescriptionForSettingsDesc.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "show_description_for_settings_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.show_description_for_settings_desc)
                                }
                            })
                    } else {
                        _showDescriptionForSettingsDesc.value =
                            context.getString(R.string.show_description_for_settings_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _importData.value = (translationsRepo.getLocalizedStringValueFor(
                            "import_data", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.import_data)
                            }
                        })
                    } else {
                        _importData.value = context.getString(R.string.import_data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _importDataFromExternalJsonFile.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "import_data_from_external_json_file",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.import_data_from_external_json_file)
                                }
                            })
                    } else {
                        _importDataFromExternalJsonFile.value =
                            context.getString(R.string.import_data_from_external_json_file)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _exportDataDesc.value = (translationsRepo.getLocalizedStringValueFor(
                            "export_data_desc", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.export_data_desc)
                            }
                        })
                    } else {
                        _exportDataDesc.value = context.getString(R.string.export_data_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _addNewPanelToShelf.value = (translationsRepo.getLocalizedStringValueFor(
                            "add_new_panel_to_shelf",
                            SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.add_new_panel_to_shelf)
                            }
                        })
                    } else {
                        _addNewPanelToShelf.value =
                            context.getString(R.string.add_new_panel_to_shelf)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _panelsInTheShelf.value = (translationsRepo.getLocalizedStringValueFor(
                            "panels_in_the_shelf", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.panels_in_the_shelf)
                            }
                        })
                    } else {
                        _panelsInTheShelf.value = context.getString(R.string.panels_in_the_shelf)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noPanelsFound.value = (translationsRepo.getLocalizedStringValueFor(
                            "no_panels_found", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.no_panels_found)
                            }
                        })
                    } else {
                        _noPanelsFound.value = context.getString(R.string.no_panels_found)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _shelf.value = (translationsRepo.getLocalizedStringValueFor(
                            "shelf", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.shelf)
                            }
                        })
                    } else {
                        _shelf.value = context.getString(R.string.shelf)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _foldersListedInThisPanel.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "folders_listed_in_this_panel",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.folders_listed_in_this_panel)
                                }
                            })
                    } else {
                        _foldersListedInThisPanel.value =
                            context.getString(R.string.folders_listed_in_this_panel)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noFoldersFoundInThisPanel.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "no_folders_found_in_this_panel",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.no_folders_found_in_this_panel)
                                }
                            })
                    } else {
                        _noFoldersFoundInThisPanel.value =
                            context.getString(R.string.no_folders_found_in_this_panel)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _youCanAddTheFollowingFoldersToThisPanel.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "you_can_add_the_following_folders_to_this_panel",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.you_can_add_the_following_folders_to_this_panel)
                                }
                            })
                    } else {
                        _youCanAddTheFollowingFoldersToThisPanel.value =
                            context.getString(R.string.you_can_add_the_following_folders_to_this_panel)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _archivedFoldersDataMigratedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "archived_folders_data_migrated_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.archived_folders_data_migrated_successfully)
                                }
                            })
                    } else {
                        _archivedFoldersDataMigratedSuccessfully.value =
                            context.getString(R.string.archived_folders_data_migrated_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _rootFoldersDataMigratedSuccessfully.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "root_folders_data_migrated_successfully",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.root_folders_data_migrated_successfully)
                                }
                            })
                    } else {
                        _rootFoldersDataMigratedSuccessfully.value =
                            context.getString(R.string.root_folders_data_migrated_successfully)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteEntireDataPermanently.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "delete_entire_data_permanently",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_entire_data_permanently)
                                }
                            })
                    } else {
                        _deleteEntireDataPermanently.value =
                            context.getString(R.string.delete_entire_data_permanently)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteEntireDataPermanentlyDesc.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "delete_entire_data_permanently_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_entire_data_permanently_desc)
                                }
                            })
                    } else {
                        _deleteEntireDataPermanentlyDesc.value =
                            context.getString(R.string.delete_entire_data_permanently_desc)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _everySingleBitOfDataIsStoredLocallyOnYourDevice.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "every_single_bit_of_data_is_stored_locally_on_your_device",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.every_single_bit_of_data_is_stored_locally_on_your_device)
                                }
                            })
                    } else {
                        _everySingleBitOfDataIsStoredLocallyOnYourDevice.value =
                            context.getString(R.string.every_single_bit_of_data_is_stored_locally_on_your_device)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linkoraCollectsDataRelatedToAppCrashes.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "linkora_collects_data_related_to_app_crashes",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.linkora_collects_data_related_to_app_crashes)
                                }
                            })
                    } else {
                        _linkoraCollectsDataRelatedToAppCrashes.value =
                            context.getString(R.string.linkora_collects_data_related_to_app_crashes)
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _permissionRequiredToWriteTheData.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "permission_required_to_write_the_data",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.permission_required_to_write_the_data)
                                }
                            })
                    } else {
                        _permissionRequiredToWriteTheData.value =
                            context.getString(R.string.permission_required_to_write_the_data)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deletedEntireDataFromTheLocalDatabase.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "deleted_entire_data_from_the_local_database",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.deleted_entire_data_from_the_local_database)
                                }
                            })
                    } else {
                        _deletedEntireDataFromTheLocalDatabase.value =
                            context.getString(R.string.deleted_entire_data_from_the_local_database)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linkoraWouldNotBePossibleWithoutTheFollowingOpenSourceSoftwareLibraries.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "linkora_would_not_be_possible_without_the_following_open_source_software_libraries",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.linkora_would_not_be_possible_without_the_following_open_source_software_libraries)
                                }
                            })
                    } else {
                        _linkoraWouldNotBePossibleWithoutTheFollowingOpenSourceSoftwareLibraries.value =
                            context.getString(R.string.linkora_would_not_be_possible_without_the_following_open_source_software_libraries)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noFoldersAreFoundCreateFoldersForBetterOrganizationOfYourLinks.value =
                            (translationsRepo.getLocalizedStringValueFor(
                                "no_folders_are_found_create_folders_for_better_organization_of_your_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.no_folders_are_found_create_folders_for_better_organization_of_your_links)
                                }
                            })
                    } else {
                        _noFoldersAreFoundCreateFoldersForBetterOrganizationOfYourLinks.value =
                            context.getString(R.string.no_folders_are_found_create_folders_for_better_organization_of_your_links)
                    }
                },

                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _sortFoldersBy.value = (translationsRepo.getLocalizedStringValueFor(
                            "sort_folders_by", SettingsPreference.preferredAppLanguageCode.value
                        ).let {
                            it.ifNullOrBlank {
                                context.getString(R.string.sort_folders_by)
                            }
                        })
                    } else {
                        _sortFoldersBy.value = context.getString(R.string.sort_folders_by)
                    }
                },
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