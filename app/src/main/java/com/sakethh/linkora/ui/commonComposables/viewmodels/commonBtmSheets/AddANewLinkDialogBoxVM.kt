package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import androidx.lifecycle.ViewModel
import com.sakethh.linkora.data.local.folders.FoldersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddANewLinkDialogBoxVM @Inject constructor(val foldersRepo: FoldersRepo) : ViewModel()