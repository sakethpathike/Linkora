package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.shelf.shelfLists.ShelfListsRepo
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import javax.inject.Inject

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
    shelfListsRepo: ShelfListsRepo
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
    shelfListsRepo
) {
    companion object {
        var selectedShelfData =
            Shelf(id = 0L, shelfName = "", shelfIconName = "", folderIds = listOf())
    }
}