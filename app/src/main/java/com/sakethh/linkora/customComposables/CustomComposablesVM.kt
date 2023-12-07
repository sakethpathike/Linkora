package com.sakethh.linkora.customComposables

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

data class UpdateBothNameAndNoteParam(
    val renameDialogBoxParam: RenameDialogBoxParam,
    val context: Context,
    val newFolderOrTitleName: String,
    val newNote: String,
    val parentFolderID: Long?
)

class CustomComposablesVM : CustomFunctionsForLocalDB() {
    companion object {
        var selectedFolderID: Long = 0
    }

    fun updateBothNameAndNote(
        updateBothNameAndNoteParam: UpdateBothNameAndNoteParam
    ) {
        when (updateBothNameAndNoteParam.newFolderOrTitleName) {
            "" -> {
                Toast.makeText(
                    updateBothNameAndNoteParam.context,
                    if (updateBothNameAndNoteParam.renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) "Folder name can't be empty" else "title can't be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                if (updateBothNameAndNoteParam.renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER && !updateBothNameAndNoteParam.renameDialogBoxParam.selectedV9ArchivedFolder.value) {
                    fun updateFolderTitle() {
                        viewModelScope.launch {
                            val doesFolderExists =
                                if (!updateBothNameAndNoteParam.renameDialogBoxParam.inASpecificScreen) {
                                    localDB.readDao().doesThisRootFolderExists(
                                        folderName = updateBothNameAndNoteParam.newFolderOrTitleName
                                    )
                                } else {
                                    localDB.readDao().doesThisChildFolderExists(
                                        folderName = updateBothNameAndNoteParam.newFolderOrTitleName,
                                        parentFolderID = updateBothNameAndNoteParam.parentFolderID
                                    ) >= 1
                                }
                            if (doesFolderExists) {
                                Toast.makeText(
                                    updateBothNameAndNoteParam.context,
                                    "folder name already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                localDB.updateDao().renameAFolderName(
                                    folderID = updateBothNameAndNoteParam.renameDialogBoxParam.currentFolderID,
                                    newFolderName = updateBothNameAndNoteParam.newFolderOrTitleName
                                )
                            }
                        }
                    }
                    when (updateBothNameAndNoteParam.newNote) {
                        "" -> {
                            updateFolderTitle()
                        }

                        else -> {
                            viewModelScope.launch {
                                awaitAll(async {
                                    localDB.updateDao().renameAFolderNoteV10(
                                        folderID = updateBothNameAndNoteParam.renameDialogBoxParam.currentFolderID,
                                        newNote = updateBothNameAndNoteParam.newNote
                                    )
                                }, async { updateFolderTitle() })
                            }
                        }
                    }
                } else if (updateBothNameAndNoteParam.renameDialogBoxParam.selectedV9ArchivedFolder.value) {
                    fun updateArchivedFolderTitle() {
                        viewModelScope.launch {
                            val doesFolderExists = async {
                                updateBothNameAndNoteParam.renameDialogBoxParam.existingFolderName?.let {
                                    localDB.readDao().doesThisArchiveFolderExistsV9(
                                        folderName = it
                                    )
                                }
                            }.await()
                            if (doesFolderExists == true) {
                                Toast.makeText(
                                    updateBothNameAndNoteParam.context,
                                    "folder name already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                localDB.updateDao().renameAFolderArchiveNameV9(
                                    folderID = updateBothNameAndNoteParam.renameDialogBoxParam.currentFolderID,
                                    newFolderName = updateBothNameAndNoteParam.newFolderOrTitleName
                                )
                            }
                        }
                    }
                    when (updateBothNameAndNoteParam.newNote) {
                        "" -> {
                            updateArchivedFolderTitle()
                        }

                        else -> {
                            viewModelScope.launch {
                                awaitAll(async {
                                    updateBothNameAndNoteParam.renameDialogBoxParam.existingFolderName?.let {
                                        localDB.updateDao().renameInfoOfArchiveFoldersV9(
                                            folderName = it,
                                            newInfo = updateBothNameAndNoteParam.newNote
                                        )
                                    }
                                }, async { updateArchivedFolderTitle() })
                            }
                        }
                    }
                }
            }
        }
    }
}