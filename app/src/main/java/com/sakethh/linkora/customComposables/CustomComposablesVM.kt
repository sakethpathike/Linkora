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
                if (updateBothNameAndNoteParam.renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) {
                    fun updateFolderTitle() {
                        viewModelScope.launch {
                            val doesFolderExists = async {
                                localDB.readDao().doesThisFolderExists(
                                    folderName = updateBothNameAndNoteParam.newFolderOrTitleName,
                                    parentFolderID = updateBothNameAndNoteParam.parentFolderID
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
                            updateFolderTitle()
                        }

                        else -> {
                            viewModelScope.launch {
                                awaitAll(async {
                                    localDB.updateDao().renameAFolderNote(
                                        folderID = updateBothNameAndNoteParam.renameDialogBoxParam.currentFolderID,
                                        newNote = updateBothNameAndNoteParam.newNote
                                    )
                                }, async { updateFolderTitle() })
                            }
                        }
                    }
                } else {
                    when (updateBothNameAndNoteParam.newNote) {
                        "" -> {
                            updateBothNameAndNoteParam.renameDialogBoxParam.webURLForTitle?.let {
                                updateBothNameAndNoteParam.renameDialogBoxParam.onTitleChangeClickForLinks?.let { it1 ->
                                    it1(
                                        it, updateBothNameAndNoteParam.newFolderOrTitleName
                                    )
                                }
                            }
                        }

                        else -> {
                            updateBothNameAndNoteParam.renameDialogBoxParam.onTitleChangeClickForLinks
                            updateBothNameAndNoteParam.renameDialogBoxParam.webURLForTitle?.let {
                                updateBothNameAndNoteParam.renameDialogBoxParam.onNoteChangeClickForLinks?.let { it1 ->
                                    it1(
                                        it, updateBothNameAndNoteParam.newNote
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}