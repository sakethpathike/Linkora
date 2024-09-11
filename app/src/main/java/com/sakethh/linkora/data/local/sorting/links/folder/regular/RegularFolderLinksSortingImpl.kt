package com.sakethh.linkora.data.local.sorting.links.folder.regular

import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow

class RegularFolderLinksSortingImpl(private val localDatabase: LocalDatabase) :
    RegularFolderLinksSortingRepo {
    override fun sortByAToZV10(folderID: Long): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByAToZV10(folderID)
    }

    override fun sortByAToZV10(): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByAToZV10()
    }

    override fun sortByZToAV10(folderID: Long): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByZToAV10(folderID)
    }

    override fun sortByZToAV10(): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByZToAV10()
    }

    override fun sortByLatestToOldestV10(folderID: Long): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByLatestToOldestV10(folderID)
    }

    override fun sortByLatestToOldestV10(): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByLatestToOldestV10()
    }

    override fun sortByOldestToLatestV10(folderID: Long): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByOldestToLatestV10(folderID)
    }

    override fun sortByOldestToLatestV10(): Flow<List<LinksTable>> {
        return localDatabase.regularFolderLinksSorting().sortByOldestToLatestV10()
    }
}