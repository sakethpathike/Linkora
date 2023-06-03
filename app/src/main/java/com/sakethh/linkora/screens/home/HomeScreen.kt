package com.sakethh.linkora.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun HomeScreen() {
    LinkoraTheme {
        LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
            item{
                Text(text = "Home screen", color = MaterialTheme.colorScheme.onBackground,style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}