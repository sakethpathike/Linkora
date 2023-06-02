package com.sakethh.linkora.screens.articleView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.string.RichTextString
import com.halilibo.richtext.ui.string.Text
import com.sakethh.linkora.screens.CoilImage
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleViewScreen() {
    val articleViewVM: ArticleViewVM = viewModel()
    LinkoraTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
        ) {
            RichText(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(start = 15.dp, end = 15.dp)
            ) {
                Column {
                    androidx.compose.material3.Text(
                        text = articleViewVM.articleData.value.titleOfTheArticle.value,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(top = 75.dp)
                    )
                    androidx.compose.material3.Text(
                        text = "by ${articleViewVM.articleData.value.authorOfTheArticle.value} | ${articleViewVM.articleData.value.baseURLOfTheArticle.value}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    CoilImage(
                        modifier = Modifier
                            .padding(top = 30.dp, start = 5.dp, end = 5.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(10.dp)),
                        imgURL = articleViewVM.articleData.value.imgURLOfTheArticle.value
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Markdown(content = articleViewVM.articleData.value.markDownText.value)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}