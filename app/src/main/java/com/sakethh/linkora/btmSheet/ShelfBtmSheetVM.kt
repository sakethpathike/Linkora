package com.sakethh.linkora.btmSheet

import com.sakethh.linkora.localDB.dto.Shelf
import com.sakethh.linkora.screens.home.HomeScreenVM

class ShelfBtmSheetVM : HomeScreenVM() {
    companion object {
        var selectedShelfData =
            Shelf(id = 0L, shelfName = "", shelfIconName = "", folderIds = listOf())
    }
}