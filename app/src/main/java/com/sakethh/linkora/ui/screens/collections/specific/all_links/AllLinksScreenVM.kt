package com.sakethh.linkora.ui.screens.collections.specific.all_links

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sakethh.linkora.data.local.sorting.links.archive.ArchivedLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.history.HistoryLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllLinksScreenVM @Inject constructor(
    savedLinksSortingRepo: SavedLinksSortingRepo,
    importantLinksSortingRepo: ImportantLinksSortingRepo,
    historyLinksSortingRepo: HistoryLinksSortingRepo,
    archivedLinksSortingRepo: ArchivedLinksSortingRepo,
    regularFolderLinksSortingRepo: RegularFolderLinksSortingRepo
) : ViewModel() {


    val linkTypes = listOf(
        LinkTypeSelection(linkType = "Saved Links", isChecked = mutableStateOf(false)),
        LinkTypeSelection(linkType = "Important Links", isChecked = mutableStateOf(false)),
        LinkTypeSelection(linkType = "History Links", isChecked = mutableStateOf(false)),
        LinkTypeSelection(linkType = "Archived Links", isChecked = mutableStateOf(false)),
        LinkTypeSelection(linkType = "Folders Links", isChecked = mutableStateOf(false)),
    )

    val savedLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            savedLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            savedLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            savedLinksSortingRepo.sortByAToZ()
        }

        else -> {
            savedLinksSortingRepo.sortByZToA()
        }
    }
    val importantLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            importantLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            importantLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            importantLinksSortingRepo.sortByAToZ()
        }

        else -> {
            importantLinksSortingRepo.sortByZToA()
        }
    }
    val historyLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            historyLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            historyLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            historyLinksSortingRepo.sortByAToZ()
        }

        else -> {
            historyLinksSortingRepo.sortByZToA()
        }
    }
    val archivedLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            archivedLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            archivedLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            archivedLinksSortingRepo.sortByAToZ()
        }

        else -> {
            archivedLinksSortingRepo.sortByZToA()
        }
    }
    val regularFoldersLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            regularFolderLinksSortingRepo.sortByLatestToOldestV10()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            regularFolderLinksSortingRepo.sortByOldestToLatestV10()
        }

        SortingPreferences.A_TO_Z.name -> {
            regularFolderLinksSortingRepo.sortByAToZV10()
        }

        else -> {
            regularFolderLinksSortingRepo.sortByZToAV10()
        }
    }
}