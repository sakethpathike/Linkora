package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


enum class OptionsBtmSheetType {
    LINK, FOLDER, IMPORTANT_LINKS_SCREEN,
}

@HiltViewModel
class OptionsBtmSheetVM @Inject constructor(
    private val linksRepo: LinksRepo,
    private val foldersRepo: FoldersRepo
) : ViewModel() {
    val importantCardIcon = mutableStateOf(Icons.Outlined.Favorite)

    val archiveCardIcon = mutableStateOf(Icons.Outlined.Archive)
    suspend fun updateImportantCardData(url: String) {
        if (linksRepo
                .doesThisExistsInImpLinks(webURL = url)
        ) {
            importantCardIcon.value = Icons.Outlined.DeleteForever
        } else {
            importantCardIcon.value = Icons.Outlined.StarOutline
        }
    }

    suspend fun updateArchiveLinkCardData(url: String) {
        if (linksRepo
                .doesThisExistsInArchiveLinks(webURL = url)
        ) {
            archiveCardIcon.value = Icons.Outlined.Unarchive
        } else {
            archiveCardIcon.value = Icons.Outlined.Archive
        }
    }

    suspend fun updateArchiveFolderCardData(folderID: Long) {
        if (foldersRepo
                .doesThisArchiveFolderExistsV10(folderID)
        ) {
            archiveCardIcon.value = Icons.Outlined.Unarchive
        } else {
            archiveCardIcon.value = Icons.Outlined.Archive
        }
    }
}