package com.sakethh.linkora.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.localDB.RecentlyVisited
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeScreenVM : ViewModel() {
    val currentPhaseOfTheDay = mutableStateOf("")

    private val _linksData = MutableStateFlow(emptyList<LinksTable>())
    val recentlySavedLinksData = _linksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(emptyList<ImportantLinks>())
    val recentlySavedImpLinksData = _impLinksData.asStateFlow()

    private val _recentlyVisitedLinksData = MutableStateFlow(emptyList<RecentlyVisited>())
    val recentlyVisitedLinksData = _recentlyVisitedLinksData.asStateFlow()

    companion object {
        val tempImpLinkData = ImportantLinks("", "", "", "", "")
    }

    init {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..3 -> {
                currentPhaseOfTheDay.value = "Didn't slept?"
            }

            in 4..11 -> {
                currentPhaseOfTheDay.value = "Good Morning"
            }

            in 12..15 -> {
                currentPhaseOfTheDay.value = "Good Afternoon"
            }

            in 16..22 -> {
                currentPhaseOfTheDay.value = "Good Evening"
            }

            in 23 downTo 0 -> {
                currentPhaseOfTheDay.value = "Good Night?"
            }

            else -> {
                currentPhaseOfTheDay.value = "Hey, hi\uD83D\uDC4B"
            }
        }

        viewModelScope.launch {
            CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllImpLinks().collect {
                _impLinksData.emit(it.reversed().take(8))
            }
        }

        viewModelScope.launch {
            CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllSavedLinks().collect {
                _linksData.emit(it.reversed().take(8))
            }
        }

        viewModelScope.launch {
            CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllRecentlyVisitedLinks()
                .collect {
                    _recentlyVisitedLinksData.emit(it.reversed())
                }
        }
    }
}