package com.sakethh.linkora.screens.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen() {
    LinkoraTheme {
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(title = {
                Text(
                    text = "Collections",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp
                )
            })
        }) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp), modifier = Modifier
                            .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Icon(
                                modifier = Modifier.padding(20.dp),
                                imageVector = Icons.Outlined.StarOutline,
                                contentDescription = null
                            )
                            Text(
                                text = "Favorites",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 25.dp)
                            )
                        }
                    }
                }
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp), modifier = Modifier
                            .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row {
                            Icon(
                                modifier = Modifier.padding(20.dp),
                                imageVector = Icons.Outlined.Archive,
                                contentDescription = null
                            )
                            Text(
                                text = "Archive",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 25.dp)
                            )
                        }
                    }
                }
                item {
                    Divider(thickness = 0.5.dp, modifier = Modifier.padding(25.dp))
                }
                item {
                    Text(
                        text = "Folders",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                item{
                    Spacer(modifier = Modifier.padding(top = 15.dp))
                }
                items(20) {
                    Column{
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(75.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .size(28.dp)
                            )
                            Column(modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.80f), verticalArrangement = Arrangement.SpaceEvenly) {
                                Text(
                                    text = "",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 10.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = null,
                                    modifier =  Modifier.padding(top = 20.dp,end=8.dp)
                                )
                            }
                        }
                        Divider(thickness = 1.dp, modifier = Modifier.padding(start = 25.dp,end=25.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(65.dp))
                }
            }
        }
    }
}