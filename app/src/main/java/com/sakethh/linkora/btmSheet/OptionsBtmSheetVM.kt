package com.sakethh.linkora.btmSheet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB


enum class OptionsBtmSheetType {
    LINK, FOLDER, IMPORTANT_LINKS_SCREEN,
}

class OptionsBtmSheetVM : ViewModel() {
    val importantCardIcon = mutableStateOf(Icons.Outlined.Favorite)
    val importantCardText = mutableStateOf("")

    val archiveCardIcon = mutableStateOf(Icons.Outlined.Archive)
    val archiveCardText = mutableStateOf("")
    suspend fun updateImportantCardData(url: String) {
        if (CustomFunctionsForLocalDB.localDB.crudDao()
                .doesThisExistsInImpLinks(webURL = url)
        ) {
            importantCardIcon.value = Icons.Outlined.DeleteForever
            importantCardText.value = "Remove from Important Links"
        } else {
            importantCardIcon.value = Icons.Outlined.StarOutline
            importantCardText.value = "Add to Important Links"
        }
    }

    suspend fun updateArchiveLinkCardData(url: String) {
        if (CustomFunctionsForLocalDB.localDB.crudDao()
                .doesThisExistsInArchiveLinks(webURL = url)
        ) {
            archiveCardIcon.value = Icons.Outlined.Unarchive
            archiveCardText.value = "Remove from Archive"
        } else {
            archiveCardIcon.value = Icons.Outlined.Archive
            archiveCardText.value = "Move to Archive"
        }
    }

    suspend fun updateArchiveFolderCardData(folderName: String) {
        if (CustomFunctionsForLocalDB.localDB.crudDao()
                .doesThisArchiveFolderExists(folderName = folderName)
        ) {
            archiveCardIcon.value = Icons.Outlined.Unarchive
            archiveCardText.value = "Remove from Archive(s)"
        } else {
            archiveCardIcon.value = Icons.Outlined.Archive
            archiveCardText.value = "Move to Archive(s)"
        }
    }
}