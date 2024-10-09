package com.sakethh.linkora.ui.transferActions

import android.content.Context
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.utils.linkoraLog
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch


object TransferActions : ViewModel() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface TransferActionsEntryPoint {
        fun foldersRepo(): FoldersRepo
        fun linksRepo(): LinksRepo
    }

    val currentTransferActionType = mutableStateOf(TransferActionType.NOTHING)

    val sourceFolders = mutableStateListOf<FoldersTable>()

    val sourceLinks = mutableStateListOf<LinksTable>()

    val isAnyActionGoingOn = mutableStateOf(false)


    val currentLinkTransferProgressCount = mutableLongStateOf(0)
    val currentFolderTransferProgressCount = mutableLongStateOf(0)

    val totalSelectedFoldersCount = mutableLongStateOf(0)
    val totalSelectedLinksCount = mutableLongStateOf(0)



    fun transferFolders(
        applyCopyImpl: Boolean, sourceFolderIds: List<Long>, targetParentId: Long?, context: Context
    ) {
        val linksRepo =
            EntryPoints.get(context.applicationContext, TransferActionsEntryPoint::class.java)
                .linksRepo()
        val foldersRepo =
            EntryPoints.get(context.applicationContext, TransferActionsEntryPoint::class.java)
                .foldersRepo()
        viewModelScope.launch {
            if (applyCopyImpl) {
                sourceFolderIds.forEach { originalFolderId ->

                    foldersRepo.getThisFolderData(originalFolderId).folderName.let {
                        linkoraLog("originally : $it")
                    }


                    val newlyDuplicatedFolderId =
                        foldersRepo.duplicateAFolder(originalFolderId, targetParentId)

                    linksRepo.duplicateFolderBasedLinks(
                        currentIdOfLinkedFolder = originalFolderId,
                        newIdOfLinkedFolder = newlyDuplicatedFolderId
                    )

                    foldersRepo.getThisFolderData(newlyDuplicatedFolderId).folderName.let {
                        linkoraLog(
                            "duplicated : $it, links:\n${
                                linksRepo.getLinksOfThisFolderAsList(
                                    newlyDuplicatedFolderId
                                ).map { it.title }
                            }"
                        )
                    }
                    transferFolders(
                        applyCopyImpl = true,
                        sourceFolderIds = foldersRepo.getChildFoldersOfThisParentIDAsList(
                            originalFolderId
                        ).map { it.id },
                        targetParentId = newlyDuplicatedFolderId,
                        context
                    )

                    if (sourceFolders.map { it.id }.contains(originalFolderId)) {
                        sourceFolders.indexOfFirst {
                            it.id == originalFolderId
                        }.let { index ->
                            linkoraLog("original folder id $originalFolderId, index is $index")
                            try {
                                sourceFolders.removeAt(index)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                ++currentFolderTransferProgressCount.longValue
                            }
                        }
                    }
                }
            } else {
                try {
                    foldersRepo.changeTheParentIdOfASpecificFolder(sourceFolderIds, targetParentId)
                } finally {
                    currentFolderTransferProgressCount.longValue =
                        sourceFolders.size.toLong()
                }
            }
        }
    }

    fun transferLinks(
        applyCopyImpl: Boolean,
        sourceLinks: List<LinksTable>,
        targetFolder: SpecificScreenType,
        context: Context
    ) {
        val linksRepo =
            EntryPoints.get(context.applicationContext, TransferActionsEntryPoint::class.java)
                .linksRepo()
        viewModelScope.launch {

            sourceLinks.forEach { currentLink ->

                when (targetFolder) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        if (currentLink.isLinkedWithImpFolder) return@forEach

                        linksRepo.addANewLinkToImpLinks(
                            ImportantLinks(
                                title = currentLink.title,
                                webURL = currentLink.webURL,
                                baseURL = currentLink.baseURL,
                                imgURL = currentLink.imgURL,
                                infoForSaving = currentLink.infoForSaving
                            )
                        )

                        if (applyCopyImpl) {
                            return@forEach
                        }
                        linksRepo.deleteALinkFromLinksTable(currentLink.id)
                    }

                    SpecificScreenType.SAVED_LINKS_SCREEN -> {
                        if (currentLink.isLinkedWithSavedLinks && !applyCopyImpl) return@forEach

                        if (currentLink.isLinkedWithFolders) {
                            if (applyCopyImpl) {
                                linksRepo.addALinkInLinksTable(
                                    LinksTable(
                                        title = currentLink.title,
                                        webURL = currentLink.webURL,
                                        baseURL = currentLink.baseURL,
                                        imgURL = currentLink.imgURL,
                                        infoForSaving = currentLink.imgURL,
                                        isLinkedWithSavedLinks = true,
                                        isLinkedWithFolders = false,
                                        isLinkedWithImpFolder = false,
                                        keyOfImpLinkedFolder = "",
                                        isLinkedWithArchivedFolder = false
                                    )
                                )
                            } else {
                                linksRepo.markThisLinkFromLinksTableAsSavedLink(linkID = currentLink.id)
                            }
                        } else {

                            // if link is located in `Important Links`

                            linksRepo.addALinkInLinksTable(
                                LinksTable(
                                    title = currentLink.title,
                                    webURL = currentLink.webURL,
                                    baseURL = currentLink.baseURL,
                                    imgURL = currentLink.imgURL,
                                    infoForSaving = currentLink.infoForSaving,
                                    isLinkedWithSavedLinks = true,
                                    isLinkedWithFolders = false,
                                    isLinkedWithImpFolder = false,
                                    keyOfImpLinkedFolder = "",
                                    isLinkedWithArchivedFolder = false
                                )
                            )

                            if (applyCopyImpl) {
                                return@forEach
                            }
                            linksRepo.deleteALinkFromImpLinks(currentLink.id)
                        }
                    }

                    else /* else = SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN */ -> {
                        if (currentLink.isLinkedWithFolders && !applyCopyImpl && currentLink.keyOfLinkedFolderV10 == CollectionsScreenVM.currentClickedFolderData.value.id) return@forEach

                        if (currentLink.isLinkedWithSavedLinks || currentLink.isLinkedWithFolders) {
                            if (applyCopyImpl) {
                                linksRepo.addALinkInLinksTable(
                                    LinksTable(
                                        title = currentLink.title,
                                        webURL = currentLink.webURL,
                                        baseURL = currentLink.baseURL,
                                        imgURL = currentLink.imgURL,
                                        infoForSaving = currentLink.imgURL,
                                        isLinkedWithSavedLinks = false,
                                        isLinkedWithFolders = true,
                                        isLinkedWithImpFolder = false,
                                        keyOfImpLinkedFolder = "",
                                        isLinkedWithArchivedFolder = false,
                                        keyOfLinkedFolderV10 = CollectionsScreenVM.currentClickedFolderData.value.id
                                    )
                                )
                            } else {
                                linksRepo.markThisLinkFromLinksTableAsFolderLink(
                                    linkID = currentLink.id,
                                    targetFolderId = CollectionsScreenVM.currentClickedFolderData.value.id
                                )
                            }

                        } else {

                            // if link is originally located in `Important Links`

                            linksRepo.addALinkInLinksTable(
                                LinksTable(
                                    title = currentLink.title,
                                    webURL = currentLink.webURL,
                                    baseURL = currentLink.baseURL,
                                    imgURL = currentLink.imgURL,
                                    infoForSaving = currentLink.infoForSaving,
                                    isLinkedWithSavedLinks = false,
                                    isLinkedWithFolders = true,
                                    isLinkedWithImpFolder = false,
                                    keyOfImpLinkedFolder = "",
                                    isLinkedWithArchivedFolder = false,
                                    keyOfLinkedFolderV10 = CollectionsScreenVM.currentClickedFolderData.value.id
                                )
                            )

                            if (applyCopyImpl) return@forEach

                            linksRepo.deleteALinkFromImpLinks(currentLink.id)
                        }
                    }
                }
                TransferActions.sourceLinks.indexOfFirst {
                    it.id == currentLink.id
                }.let { index ->
                    linkoraLog(currentLink.title + " index is " + index.toString())
                    try {
                        TransferActions.sourceLinks.removeAt(index)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        ++currentLinkTransferProgressCount.longValue
                    }
                }
            }
        }
    }

    init {
        combine(
            snapshotFlow { sourceLinks.toList() },
            snapshotFlow { sourceFolders.toList() }
        ) { linkData, folderData ->

            val areBothEmpty = folderData.isEmpty() && linkData.isEmpty()

            if (isAnyActionGoingOn.value.not()) {
                totalSelectedLinksCount.longValue = linkData.size.toLong()
                totalSelectedFoldersCount.longValue = folderData.size.toLong()
            }
            if (areBothEmpty) {
                reset()
            }

        }.launchIn(viewModelScope)
    }

    fun reset() {
        currentTransferActionType.value = TransferActionType.NOTHING
        sourceFolders.clear()
        sourceLinks.clear()
        currentFolderTransferProgressCount.longValue = 0
        currentLinkTransferProgressCount.longValue = 0
        isAnyActionGoingOn.value = false
    }
}