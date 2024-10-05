package com.sakethh.linkora.ui.transferActions

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

    fun changeTheParentIdOfASpecificFolder(sourceFolderIds: List<Long>, targetParentId: Long?) {
        viewModelScope.launch {
            foldersRepo.changeTheParentIdOfASpecificFolder(sourceFolderIds, targetParentId)
            linkoraLog("2")
        }
    }

    fun movingTheLinks(sourceLinks: List<LinksTable>, targetFolder: SpecificScreenType) {
        linkoraLog("4")
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

                        linksRepo.deleteALinkFromLinksTable(currentLink.id)
                }

                SpecificScreenType.SAVED_LINKS_SCREEN -> {
                    if (currentLink.isLinkedWithSavedLinks) return@forEach

                    if (currentLink.isLinkedWithFolders) {
                            linksRepo.markThisLinkFromLinksTableAsSavedLink(linkID = currentLink.id)
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
                            linksRepo.deleteALinkFromImpLinks(currentLink.id)
                        }
                }

                else /* else = SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN */ -> {
                    if (currentLink.isLinkedWithFolders && currentLink.keyOfLinkedFolderV10 == CollectionsScreenVM.currentClickedFolderData.value.id) return@forEach

                    if (currentLink.isLinkedWithSavedLinks || currentLink.isLinkedWithFolders) {
                            linkoraLog(currentLink.title + " in ${CollectionsScreenVM.currentClickedFolderData.value.folderName}")
                            linksRepo.markThisLinkFromLinksTableAsFolderLink(
                                linkID = currentLink.id,
                                targetFolderId = CollectionsScreenVM.currentClickedFolderData.value.id
                            )
                        } else {

                            // if link is located in `Important Links`

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
                            linksRepo.deleteALinkFromImpLinks(currentLink.id)
                        }
                }
            }
        }
        }.invokeOnCompletion {
            TransferActionsBtmBarValues.reset()
        }
    }
}