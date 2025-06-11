package com.xz.customnav.ui.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.xz.customnav.ui.Article
import com.xz.customnav.ui.MyRoutes
import com.xz.customnav.ui.nav.NavController
import com.xz.customnav.ui.getArticles

@Composable
fun ArticleDetailScreen(article: Article, navController: NavController) {

    Column(Modifier.background(Color.White)) {
        Row {
            Icon(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.popBack()
                    },
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Button(onClick = {
                navController.navigate(MyRoutes.ArticleDetail(getArticles().last()))
            }) {
                Text("Navigate Last")
            }
            Button(onClick = {
                navController.popBackDest<MyRoutes.ArticleList>()
            }) {
                Text("Pop back List")
            }
            Text(article.title, style = MaterialTheme.typography.titleLarge)

            Image(
                painter = rememberAsyncImagePainter(article.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Text(article.content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}