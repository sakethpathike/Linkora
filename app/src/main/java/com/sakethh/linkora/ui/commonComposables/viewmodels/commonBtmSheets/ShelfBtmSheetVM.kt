package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.HomeScreenListTable
import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.shelf.ShelfRepo
import com.sakethh.linkora.data.local.shelf.shelfLists.ShelfListsRepo
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.shelf.ShelfUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShelfBtmSheetVM @Inject constructor(
    linksRepo: LinksRepo,
    foldersRepo: FoldersRepo,
    savedLinksSortingRepo: SavedLinksSortingRepo,
    importantLinksSortingRepo: ImportantLinksSortingRepo,
    folderLinksSortingRepo: RegularFolderLinksSortingRepo,
    archiveFolderLinksSortingRepo: ArchivedFolderLinksSortingRepo,
    subFoldersSortingRepo: SubFoldersSortingRepo,
    regularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    parentRegularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    shelfListsRepo: ShelfListsRepo,
    shelfRepo: ShelfRepo,
    customWebTab: CustomWebTab
) : HomeScreenVM(
    linksRepo,
    foldersRepo,
    savedLinksSortingRepo,
    importantLinksSortingRepo,
    folderLinksSortingRepo,
    archiveFolderLinksSortingRepo,
    subFoldersSortingRepo,
    regularFoldersSortingRepo,
    parentRegularFoldersSortingRepo,
    shelfListsRepo,
    shelfRepo,
    customWebTab
) {
    companion object {
        var selectedShelfData =
            Shelf(id = 0L, shelfName = "", shelfIconName = "", folderIds = listOf())
    }

    fun onShelfUiEvent(shelfUIEvent: ShelfUIEvent) {
        when (shelfUIEvent) {
            is ShelfUIEvent.DeleteAShelfFolder -> {
                viewModelScope.launch {
                    shelfListsRepo.deleteAShelfFolder(shelfUIEvent.folderId)
                }
            }

            is ShelfUIEvent.InsertANewElementInHomeScreenList -> {
                viewModelScope.launch {
                    val homeScreenListTable =
                        HomeScreenListTable(
                            id = shelfUIEvent.folderID,
                            position = withContext(Dispatchers.IO) {
                                try {
                                    ++shelfListsRepo.getLastInsertedElement().position
                                } catch (_: NullPointerException) {
                                    1
                                }
                            },
                            folderName = shelfUIEvent.folderName,
                            parentShelfID = shelfUIEvent.parentShelfID
                        )
                    shelfListsRepo.addAHomeScreenListFolder(homeScreenListTable)
                }
            }

            is ShelfUIEvent.AddANewShelf -> {
                viewModelScope.launch {
                    if (shelfRepo.doesThisShelfExists(shelfUIEvent.shelf.shelfName)) {
                        pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.shelfNameAlreadyExists.value))
                    } else {
                        shelfRepo.addANewShelf(shelfUIEvent.shelf)
                    }
                }
            }

            is ShelfUIEvent.DeleteAPanel -> {
                viewModelScope.launch {
                    shelfRepo.deleteAShelf(shelfUIEvent.shelf)
                }
            }

            is ShelfUIEvent.UpdateAShelfName -> {
                viewModelScope.launch {
                    shelfRepo.updateAShelfName(shelfUIEvent.newName, shelfUIEvent.selectedShelfID)
                }
            }
        }
    }
}