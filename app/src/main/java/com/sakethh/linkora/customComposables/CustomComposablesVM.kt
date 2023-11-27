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
                if (updateBothNameAndNoteParam.renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER && !updateBothNameAndNoteParam.renameDialogBoxParam.inChildArchiveFolderScreen.value) {
                    fun updateRootFolderTitle() {
                        viewModelScope.launch {
                            val doesFolderExists = async {
                                localDB.readDao().doesThisRootFolderExists(
                                    folderName = updateBothNameAndNoteParam.newFolderOrTitleName
                                )
                            }.await()
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
                            updateRootFolderTitle()
                        }

                        else -> {
                            viewModelScope.launch {
                                awaitAll(async {
                                    localDB.updateDao().renameAFolderNote(
                                        folderID = updateBothNameAndNoteParam.renameDialogBoxParam.currentFolderID,
                                        newNote = updateBothNameAndNoteParam.newNote
                                    )
                                }, async { updateRootFolderTitle() })
                            }
                        }
                    }
                } else {
                    fun updateArchivedFolderTitle() {
                        viewModelScope.launch {
                            val doesFolderExists = async {
                                updateBothNameAndNoteParam.renameDialogBoxParam.existingFolderName?.let {
                                    localDB.readDao().doesThisArchiveFolderExists(
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
                                localDB.updateDao().renameAFolderArchiveName(
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
                                    localDB.updateDao().renameALinkInfoOfArchiveFolders(
                                        folderID = updateBothNameAndNoteParam.renameDialogBoxParam.currentFolderID,
                                        newInfo = updateBothNameAndNoteParam.newNote
                                    )
                                }, async { updateArchivedFolderTitle() })
                            }
                        }
                    }
                }
            }
        }
    }
}