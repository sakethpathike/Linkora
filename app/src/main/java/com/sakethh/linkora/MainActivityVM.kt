package com.sakethh.linkora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.LocalizedStrings.archivedFoldersDataMigratedSuccessfully
import com.sakethh.linkora.LocalizedStrings.rootFoldersDataMigratedSuccessfully
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.restore.ImportRepo
import com.sakethh.linkora.data.remote.localization.LocalizationRepo
import com.sakethh.linkora.data.remote.localization.LocalizationResult
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.utils.linkoraLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityVM @Inject constructor(
    private val foldersRepo: FoldersRepo,
    private val importRepo: ImportRepo,
    private val localizationRepo: LocalizationRepo
) : ViewModel() {

    init {
        viewModelScope.launch {
            when (val data = localizationRepo.retrieveLanguageStrings("en")) {
                is LocalizationResult.Failure -> TODO()
                is LocalizationResult.Success -> {
                    data.data.forEach {
                        linkoraLog(it.toString())
                    }
                    linkoraLog(data.data.size.toString())
                }
            }
        }
    }

    private val _channelEvent = Channel<CommonUiEvent>()
    val channelEvent = _channelEvent.receiveAsFlow()

    fun migrateArchiveFoldersV9toV10() {
        viewModelScope.launch {
            if (foldersRepo.getAllArchiveFoldersV9List()
                    .isNotEmpty()
            ) {
                importRepo.migrateArchiveFoldersV9toV10()
                pushUIEvent(CommonUiEvent.ShowToast(archivedFoldersDataMigratedSuccessfully.value))
            }
        }
    }

    fun migrateRegularFoldersLinksDataFromV9toV10() {
        viewModelScope.launch {
            if (foldersRepo.getAllRootFoldersList()
                    .isNotEmpty()
            ) {
                importRepo.migrateRegularFoldersLinksDataFromV9toV10()
            }
            pushUIEvent(CommonUiEvent.ShowToast(rootFoldersDataMigratedSuccessfully.value))
        }
    }

    private suspend fun pushUIEvent(commonUiEvent: CommonUiEvent) {
        _channelEvent.send(commonUiEvent)
    }

}