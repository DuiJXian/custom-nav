package com.xz.customnav.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xz.customnav.ui.navigation.ArticleDetailScreen
import com.xz.customnav.ui.navigation.ArticleListScreen
import com.xz.customnav.ui.ui.theme.CustomNavTheme

class NavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomNavTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    Box(Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {

                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "list"
                        ) {
                            composable(
                                route = "list",
                                enterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                exitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { -it/2 },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                popEnterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { 0 },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                popExitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                }
                            ) {
                                ArticleListScreen(navController)
                            }

                            composable(
                                route = "detail/{id}",
                                enterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                exitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { -it/2 },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                popEnterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { 0 },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                popExitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(
                                            durationMillis = 250,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                arguments = listOf(
                                    navArgument("id") { type = NavType.IntType }
                                )
                            ) {
                                val id = it.arguments?.getInt("id")
                                val article = getArticles()[id!!]
                                ArticleDetailScreen(article, navController)
                            }
                        }
                    }
                }

            }
        }
    }
}
