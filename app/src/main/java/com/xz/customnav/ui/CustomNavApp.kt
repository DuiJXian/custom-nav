package com.xz.customnav.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xz.customnav.ui.my.ArticleDetailScreen
import com.xz.customnav.ui.my.ArticleListScreen
import com.xz.customnav.ui.nav.CreateNavGraph
import com.xz.customnav.ui.nav.NavController
import com.xz.customnav.ui.nav.RouteWithArgs


sealed class MyRoutes : RouteWithArgs {

    data object ArticleList : MyRoutes()

    data class ArticleDetail(val article: Article) : MyRoutes()

}

@Composable
fun StartScreen(modifier: Modifier) {
    val navController = remember { NavController() }
    Column(
        modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CreateNavGraph(
            startRoute = MyRoutes.ArticleList,
            navController = navController
        ) {
            navScreen<MyRoutes.ArticleList> {
                ArticleListScreen(navController)
            }

            navScreen<MyRoutes.ArticleDetail>(isSingle = false) {
                val args = it as MyRoutes.ArticleDetail
                ArticleDetailScreen(args.article, navController)
            }
        }
    }
}