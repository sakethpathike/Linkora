package com.sakethh.linkora.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.LocalDataBase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Dao
interface DeleteDao {
    @Query("UPDATE archived_links_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromArchiveLinks(webURL: String)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV10(
        webURL: String,
        folderID: Long,
    )

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName")
    suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV9(
        webURL: String,
        folderName: String,
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

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE id = :linkID AND isLinkedWithFolders=1")
    suspend fun deleteALinkInfoOfFolders(linkID: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND isLinkedWithSavedLinks = 1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithArchivedFolder=0")
    suspend fun deleteALinkFromSavedLinks(webURL: String)

    @Query("UPDATE folders_table SET infoForSaving = \"\" WHERE id = :folderID")
    suspend fun deleteAFolderNote(folderID: Long)

    @Query("DELETE from important_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromImpLinks(webURL: String)

    @Query("DELETE from archived_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromArchiveLinksV9(webURL: String)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :archiveFolderID AND isLinkedWithArchivedFolder=1")
    suspend fun deleteALinkFromArchiveFolderBasedLinksV10(webURL: String, archiveFolderID: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName AND isLinkedWithArchivedFolder=1")
    suspend fun deleteALinkFromArchiveFolderBasedLinksV9(webURL: String, folderName: String)

    @Query("DELETE from links_table WHERE id = :linkID")
    suspend fun deleteALinkFromLinksTable(linkID: Long)


    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfLinkedFolderV10 = :folderID AND isLinkedWithFolders=1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromSpecificFolderV10(webURL: String, folderID: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromSpecificFolderV9(webURL: String, folderName: String)

    @Query("DELETE from recently_visited_table WHERE webURL = :webURL")
    suspend fun deleteARecentlyVisitedLink(webURL: String)

    @Query("DELETE from folders_table WHERE id = :folderID")
    suspend fun deleteAFolder(folderID: Long)

    suspend fun deleteAllChildFoldersAndLinksOfASpecificFolder(folderID: Long) {
        val childFolders = LocalDataBase.localDB.readDao().getThisFolderData(folderID)
        val deletionAsync = mutableListOf<Deferred<Unit>>()
        coroutineScope {
            childFolders.childFolderIDs?.forEach {
                val deleteAFolder = async {
                    deleteAFolder(it)
                    deleteThisFolderLinksV10(it)
                }
                deletionAsync.add(deleteAFolder)
            }
        }
        deletionAsync.awaitAll()
    }

    @Query("DELETE from links_table WHERE keyOfLinkedFolderV10 = :folderID")
    suspend fun deleteThisFolderLinksV10(folderID: Long)

    @Query("DELETE from links_table WHERE keyOfLinkedFolder = :folderName")
    suspend fun deleteThisFolderLinksV9(folderName: String)

    @Query("DELETE from links_table WHERE keyOfArchiveLinkedFolder = :folderID")
    suspend fun deleteThisArchiveFolderDataV9(folderID: String)


    @Query("DELETE from archived_folders_table WHERE id= :folderID")
    suspend fun deleteAnArchiveFolderV9(folderID: Long)
}