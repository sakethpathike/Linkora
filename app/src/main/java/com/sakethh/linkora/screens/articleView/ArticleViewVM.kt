package com.sakethh.linkora.screens.articleView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ArticleViewVM : ViewModel() {
    val articleData = mutableStateOf(ArticleDataRetriever.ArticleData())
    val articleURL = mutableStateOf("")
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable -> throwable.printStackTrace() }

    suspend fun retrieveInfo() {
        viewModelScope.launch(coroutineExceptionHandler) {
            articleData.value = ArticleDataRetriever.retrieveArticleData(
                webURL = articleURL.value,
                coroutineScope = viewModelScope
            )
        }/*.invokeOnCompletion {
            viewModelScope.cancel("cancelled")
        }*/
    }
}