✨ CustomNav —— Jetpack Compose 自定义导航系统
一个完全基于 Jetpack Compose 的轻量级导航解决方案，支持页面栈管理、带参数跳转、方向动画过渡、ViewModel 隔离与状态恢复。

📦 功能特性
✅ 支持任意类型参数传递（通过接口 RouteWithArgs）

✅ 支持页面栈维护与回退操作

✅ 支持类似 Activity 的单例模式（isSingle）

✅ 支持左右方向页面切换动画（自定义 DirectionType）

✅ 每个页面维护独立 ViewModelStore，确保 ViewModel 生命周期独立

✅ 支持页面状态保存与恢复（通过 rememberSaveableStateHolder）

🚀 快速开始
定义路由参数
kotlin
复制
编辑
sealed interface MyRoutes : RouteWithArgs {
    data object ArticleList : MyRoutes
    data class ArticleDetail(val article: Article) : MyRoutes
}
创建导航图
kotlin
复制
编辑
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
页面跳转与回退
kotlin
复制
编辑
navController.navigate(MyRoutes.ArticleDetail(article)) // 左侧进入
navController.popBack() // 回退
navController.popBack(MyRoutes.ArticleList) // 回退到指定页面
🧱 系统架构简介
text
复制
编辑
NavController             // 提供导航接口：navigate、popBack
   │
   ▼
NavGraphViewModel        // 管理页面栈、构建路由图、维护当前页面状态
   │
   ▼
NavDestination           // 每个页面对应一个目标，包含动画方向、参数、ViewModelStore
   │
   ▼
AnimateDestination       // 控制左右滑动动画、ViewModel 生命周期与状态恢复
💡 高级用法说明
🔁 页面复用 vs 重建
isSingle = true（默认）：

如果页面已存在于栈中，会清空其后的页面，并复用旧页面状态。

isSingle = false：

每次导航都会生成新页面，可用于详情页等多实例场景。

💥 ViewModel 隔离
每个页面使用独立的 ViewModelStore，防止多个页面间 ViewModel 冲突，配合 LocalViewModelStoreOwner 提供给 viewModel()。

📐 动画控制
使用 AnimateDestination 控制页面切换动画：

方向由 DirectionType 指定（LEFT, RIGHT）

使用 CubicBezierEasing 自定义过渡曲线

DestinationLayerState 控制层级、偏移、zIndex 动画效果

🛠️ 后续可拓展
页面返回动画支持自定义方向和速度

添加 NavResult 支持页面返回数据

页面状态保存持久化（配合 DataStore 或 SavedStateHandle）

📎 依赖项
仅依赖 Jetpack Compose 与 AndroidX Lifecycle，无任何三方库依赖。

📝 License
MIT License - 可以自由用于个人与商业项目。
