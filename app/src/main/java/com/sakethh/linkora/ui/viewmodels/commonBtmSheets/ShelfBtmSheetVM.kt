package com.sakethh.linkora.ui.viewmodels.commonBtmSheets

import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.ui.viewmodels.home.HomeScreenVM

class ShelfBtmSheetVM : HomeScreenVM() {
    companion object {
        var selectedShelfData =
            Shelf(id = 0L, shelfName = "", shelfIconName = "", folderIds = listOf())
    }
}