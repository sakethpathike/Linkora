package com.sakethh.linkora.screens.articleView

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object ArticleDataRetriever {

    data class ArticleData(
        val titleOfTheArticle: MutableState<String> = mutableStateOf(""),
        val baseURLOfTheArticle: MutableState<String> = mutableStateOf(""),
        val imgURLOfTheArticle: MutableState<String> = mutableStateOf(""),
        val authorOfTheArticle: MutableState<String> = mutableStateOf(""),
        val markDownText: MutableState<String> = mutableStateOf(""),
    )
    suspend fun retrieveArticleData(webURL: String, coroutineScope: CoroutineScope): ArticleData {
        val articleData = ArticleData()
        val jsoupInstance = withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            Jsoup.connect(webURL).get()
        }
        val titleOfTheArticle = jsoupInstance.selectFirst("h1")?.ownText()?.trim()
        supervisorScope {
            awaitAll(async {
                articleData.titleOfTheArticle.value = if (titleOfTheArticle.isNullOrEmpty()) {
                    jsoupInstance.title()
                } else {
                    titleOfTheArticle.toString()
                }
            },
                async {
                    articleData.imgURLOfTheArticle.value =
                        jsoupInstance.body().selectFirst("article")?.selectFirst("picture")?.selectFirst("img[src]:not([src=''])")
                            ?.absUrl("src").toString()
                },
                async { articleData.baseURLOfTheArticle.value = jsoupInstance.baseUri() },
                async {
                    val htmlOfArticle = jsoupInstance.body().select("article").select("ch bg dx dy dz ea").outerHtml()
                    articleData.markDownText.value =
                        FlexmarkHtmlConverter.builder().build().convert(htmlOfArticle)
                },
                async {
                    articleData.authorOfTheArticle.value =
                        jsoupInstance.selectFirst("author")?.ownText() ?: ""
                })
        }
        return articleData
    }
}