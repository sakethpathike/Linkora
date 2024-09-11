package com.sakethh.linkora.data.local.sorting.links.folder.regular

import com.sakethh.linkora.data.local.LinksTable
import kotlinx.coroutines.flow.Flow

interface RegularFolderLinksSortingRepo {

    fun sortByAToZV10(folderID: Long): Flow<List<LinksTable>>

    fun sortByZToAV10(folderID: Long): Flow<List<LinksTable>>

    fun sortByLatestToOldestV10(folderID: Long): Flow<List<LinksTable>>

    fun sortByOldestToLatestV10(folderID: Long): Flow<List<LinksTable>>

    fun sortByAToZV10(): Flow<List<LinksTable>>

    fun sortByZToAV10(): Flow<List<LinksTable>>

    fun sortByLatestToOldestV10(): Flow<List<LinksTable>>

    fun sortByOldestToLatestV10(): Flow<List<LinksTable>>
}