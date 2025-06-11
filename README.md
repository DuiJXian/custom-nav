# ✨ CustomNav —— Jetpack Compose 自定义导航系统

## 🚀 快速开始

### 1️⃣ 定义路由类型

```kotlin
sealed interface MyRoutes : RouteWithArgs {
    data object ArticleList : MyRoutes
    data class ArticleDetail(val article: Article) : MyRoutes
}
```

### 2️⃣ 创建导航图

```kotlin
val navController = remember { NavController() }

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
```

### 3️⃣ 页面跳转与回退

```kotlin
navController.navigate(MyRoutes.ArticleDetail(article)) // 跳转（默认左进右出）
navController.popBack()                                // 回退一层
navController.popBackDest<MyRoutes.ArticleList>()      // 回退到指定页面
```
