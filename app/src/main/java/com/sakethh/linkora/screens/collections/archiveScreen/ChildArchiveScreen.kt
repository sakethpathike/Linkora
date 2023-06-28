package com.sakethh.linkora.screens.collections.archiveScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.home.composables.LinkUIComponent
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun ChildArchiveScreen(archiveScreenType: ArchiveScreenType) {
    val archiveScreenVM: ArchiveScreenVM = viewModel()
    val archiveLinksData = archiveScreenVM.archiveLinksData.collectAsState().value
    val archiveFoldersData = archiveScreenVM.archiveFoldersData.collectAsState().value
    LinkoraTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (archiveScreenType == ArchiveScreenType.LINKS) {
                items(archiveLinksData) {
                    LinkUIComponent(
                        title = it.title,
                        webBaseURL = it.baseURL,
                        imgURL = it.imgURL,
                        onMoreIconCLick = { /*TODO*/ },
                        onLinkClick = { /*TODO*/ },
                        webURL = it.webURL
                    )
                }
            } else {

            }
        }
    }
}