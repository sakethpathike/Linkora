package com.sakethh.linkora

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberSaveableWebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.sakethh.linkora.screens.articleView.ArticleDataRetriever
import com.sakethh.linkora.screens.articleView.ArticleViewScreen
import com.sakethh.linkora.screens.articleView.ArticleViewVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val articleViewVM:ArticleViewVM= viewModel()
            LinkoraTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    TextField(modifier = Modifier
                        .fillMaxWidth(), value = articleViewVM.articleURL.value, onValueChange = {
                        articleViewVM.articleURL.value = it
                    })
                    Button(onClick = {
                        coroutineScope.launch {
                            articleViewVM.retrieveInfo()
                        }
                    }) {
                        Text(text = "fetch")
                    }
                    ArticleViewScreen()
                }
            }
        }
    }
}