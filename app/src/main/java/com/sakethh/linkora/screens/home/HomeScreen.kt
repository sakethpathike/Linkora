package com.sakethh.linkora.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.home.composables.GeneralCard
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun HomeScreen() {
    LinkoraTheme {
        val homeScreenVM: HomeScreenVM = viewModel()
        val currentPhaseOfTheDay =
            rememberSaveable(inputs = arrayOf(homeScreenVM.currentPhaseOfTheDay.value)) {
                homeScreenVM.currentPhaseOfTheDay.value
            }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            item {
                Text(
                    text = currentPhaseOfTheDay,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 30.dp)
                )
            }

            item {
                Text(
                    text = "Recent Saves",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 45.dp)
                )
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    item {
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    items(8) {
                        GeneralCard(
                            title = "",
                            webBaseURL = "",
                            imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
    }
}