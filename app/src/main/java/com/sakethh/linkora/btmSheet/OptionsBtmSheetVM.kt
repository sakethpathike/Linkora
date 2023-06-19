package com.sakethh.linkora.btmSheet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sakethh.linkora.localDB.LocalDBFunctions


enum class OptionsBtmSheetType {
    LINK, FOLDER, IMPORTANT_LINKS_SCREEN
}

class OptionsBtmSheetVM : ViewModel() {
    val importantCardIcon = mutableStateOf(Icons.Outlined.Favorite)
    val importantCardText = mutableStateOf("")

    suspend fun updateImportantCardData(url: String) {
        if (LocalDBFunctions.doesThisLinkExistsInImportantLinksDB(url = url)) {
            importantCardIcon.value = Icons.Outlined.DeleteForever
            importantCardText.value = "Remove from Important Links"
        } else {
            importantCardIcon.value = Icons.Outlined.StarOutline
            importantCardText.value = "Add to Important Links"
        }
    }
}