package com.sakethh.linkora.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.Calendar

class HomeScreenVM : ViewModel() {
    val currentPhaseOfTheDay = mutableStateOf("")

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
    }
}