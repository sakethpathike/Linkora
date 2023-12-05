package com.sakethh.linkora.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Dao
interface DeleteDao {
    @Query("UPDATE archived_links_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromArchiveLinks(webURL: String)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun deleteALinkNoteFromArchiveBasedFolderLinks(
        webURL: String,
        folderID: Long,
    )

    @Query("UPDATE important_links_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromImportantLinks(webURL: String)


    @Query("UPDATE recently_visited_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromRecentlyVisited(webURL: String)

    @Query("UPDATE archived_folders_table SET infoForSaving = \"\"  WHERE id= :folderID")
    suspend fun deleteArchiveFolderNote(
        folderID: Long,
    )

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun deleteALinkInfoFromSavedLinks(webURL: String)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfLinkedFolderV10 = :folderID AND isLinkedWithFolders=1")
    suspend fun deleteALinkInfoOfFolders(webURL: String, folderID: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND isLinkedWithSavedLinks = 1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithArchivedFolder=0")
    suspend fun deleteALinkFromSavedLinks(webURL: String)

    @Query("UPDATE folders_table SET infoForSaving = \"\" WHERE id = :folderID")
    suspend fun deleteAFolderNote(folderID: Long)

    @Query("DELETE from important_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromImpLinks(webURL: String)

    @Query("DELETE from archived_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromArchiveLinks(webURL: String)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :archiveFolderID AND isLinkedWithArchivedFolder=1 AND isLinkedWithSavedLinks = 0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromArchiveFolderBasedLinks(webURL: String, archiveFolderID: Long)


    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfLinkedFolderV10 = :folderID AND isLinkedWithFolders=1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromSpecificFolder(webURL: String, folderID: Long)

    @Query("DELETE from recently_visited_table WHERE webURL = :webURL")
    suspend fun deleteARecentlyVisitedLink(webURL: String)

    @Query("DELETE from folders_table WHERE id = :folderID")
    suspend fun deleteAFolder(folderID: Long)

    suspend fun deleteAllChildFoldersAndLinksOfASpecificFolder(folderID: Long) {
        val childFolders = CustomFunctionsForLocalDB.localDB.readDao()
            .getThisFolderData(folderID)
        val deletionAsync = mutableListOf<Deferred<Unit>>()
        coroutineScope {
            childFolders.childFolderIDs?.forEach {
                val deleteAFolder =
                    async {
                        deleteAFolder(it)
                        deleteThisFolderLinks(it)
                    }
                deletionAsync.add(deleteAFolder)
            }
        }
        deletionAsync.awaitAll()
    }

    @Query("DELETE from links_table WHERE keyOfLinkedFolderV10 = :folderID")
    suspend fun deleteThisFolderLinks(folderID: Long)

    @Query("DELETE from links_table WHERE keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun deleteThisArchiveFolderData(folderID: Long)


    @Query("DELETE from archived_folders_table WHERE archiveFolderName= :folderName")
    suspend fun deleteAnArchiveFolder(folderName: String)
}