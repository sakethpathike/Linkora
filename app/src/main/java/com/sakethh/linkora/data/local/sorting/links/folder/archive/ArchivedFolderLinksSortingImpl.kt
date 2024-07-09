package com.sakethh.linkora.data.local.sorting.links.folder.archive

import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArchivedFolderLinksSortingImpl @Inject constructor(
    private val localDatabase: LocalDatabase
) : ArchivedFolderLinksSortingRepo {
    override fun sortLinksByAToZV10(folderID: Long?): Flow<List<LinksTable>> {
        return localDatabase.archivedFolderLinksSorting().sortLinksByAToZV10(folderID)
    }

    override fun sortLinksByZToAV10(folderID: Long?): Flow<List<LinksTable>> {
        return localDatabase.archivedFolderLinksSorting().sortLinksByZToAV10(folderID)
    }

    override fun sortLinksByLatestToOldestV10(folderID: Long?): Flow<List<LinksTable>> {
        return localDatabase.archivedFolderLinksSorting().sortLinksByLatestToOldestV10(folderID)
    }

    override fun sortLinksByOldestToLatestV10(folderID: Long?): Flow<List<LinksTable>> {
        return localDatabase.archivedFolderLinksSorting().sortLinksByOldestToLatestV10(folderID)
    }
}