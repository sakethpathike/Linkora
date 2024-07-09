package com.sakethh.linkora.data.local.sorting.links.folder.archive

import com.sakethh.linkora.data.local.LinksTable
import kotlinx.coroutines.flow.Flow

interface ArchivedFolderLinksSortingRepo {

    fun sortLinksByAToZV10(folderID: Long?): Flow<List<LinksTable>>

    fun sortLinksByZToAV10(folderID: Long?): Flow<List<LinksTable>>

    fun sortLinksByLatestToOldestV10(folderID: Long?): Flow<List<LinksTable>>

    fun sortLinksByOldestToLatestV10(folderID: Long?): Flow<List<LinksTable>>
}