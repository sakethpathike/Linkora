package com.sakethh.linkora.ui.transferActions

import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.utils.linkoraLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferActionsBtmBarVM @Inject constructor(
    private val foldersRepo: FoldersRepo,
    private val linksRepo: LinksRepo
) : ViewModel() {

    companion object {
        val currentLinkTransferProgressCount = mutableLongStateOf(0)
        val currentFolderTransferProgressCount = mutableLongStateOf(0)

        val totalSelectedFoldersCount = mutableLongStateOf(0)
        val totalSelectedLinksCount = mutableLongStateOf(0)
    }

    fun transferFolders(
        applyCopyImpl: Boolean, sourceFolderIds: List<Long>, targetParentId: Long?
    ) {
        viewModelScope.launch {
            if (applyCopyImpl) {
                sourceFolderIds.forEach { originalFolderId ->

                    foldersRepo.duplicateAFolder(originalFolderId, targetParentId)

                    val newlyDuplicatedFolderId = foldersRepo.getLastIDOfFoldersTable()
                    linksRepo.duplicateFolderBasedLinks(
                        currentIdOfLinkedFolder = originalFolderId,
                        newIdOfLinkedFolder = newlyDuplicatedFolderId
                    )

                    foldersRepo.getChildFoldersOfThisParentIDAsList(originalFolderId).map { it.id }
                        .forEach {
                            transferFolders(
                                applyCopyImpl = true,
                                sourceFolderIds = listOf(it),
                                targetParentId = newlyDuplicatedFolderId
                            )
                        }

                    TransferActionsBtmBarValues.sourceFolders.indexOfFirst {
                        it.id == originalFolderId
                    }.let { index ->
                        linkoraLog("original folder id $originalFolderId, index is $index")
                        try {
                            TransferActionsBtmBarValues.sourceFolders.removeAt(index)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            ++currentFolderTransferProgressCount.longValue
                        }
                    }
                }
            } else {
                try {
                    foldersRepo.changeTheParentIdOfASpecificFolder(sourceFolderIds, targetParentId)
                } finally {
                    currentFolderTransferProgressCount.longValue =
                        TransferActionsBtmBarValues.sourceFolders.size.toLong()
                }
            }
        }
    }

    fun transferLinks(
        applyCopyImpl: Boolean,
        sourceLinks: List<LinksTable>,
        targetFolder: SpecificScreenType
    ) {
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
                TransferActionsBtmBarValues.sourceLinks.indexOfFirst {
                    it.id == currentLink.id
                }.let { index ->
                    linkoraLog(currentLink.title + " index is " + index.toString())
                    try {
                        TransferActionsBtmBarValues.sourceLinks.removeAt(index)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        ++currentLinkTransferProgressCount.longValue
                    }
                }
        }
        }
    }
}