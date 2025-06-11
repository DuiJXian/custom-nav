package com.xz.customnav.ui.nav


import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


interface RouteWithArgs

data class NavScreen(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isSingle: Boolean = true,
    val content: @Composable (RouteWithArgs?) -> Unit
)

data class NavDestination(
    val id: String = UUID.randomUUID().toString(),
    val createTime: String = System.currentTimeMillis().toString(),
    val name: String,
    val direction: DirectionType = DirectionType.LEFT,
    val arguments: RouteWithArgs? = null,
    val viewModelStore: ViewModelStore = ViewModelStore(),
    val content: @Composable (RouteWithArgs?) -> Unit
)

@Composable
fun CreateNavGraph(
    startRoute: RouteWithArgs,
    navController: NavController,
    builder: ScreenBuilder.() -> Unit
) {
    val navGraph = viewModel<NavGraphViewModel>(
        factory = NavGraphViewModel.providerFactory(startRoute, builder)
    )
    navGraph.bindNavController(navController)
    ScreenEntrance(navGraph, navController)
}

@Composable
fun ScreenEntrance(navGraph: NavGraphViewModel, navController: NavController) {
    val currentDestination =
        navGraph.currentDestination.collectAsStateWithLifecycle().value ?: return
    val context = LocalContext.current

    BackHandler {
        if (navController.popBack()) {
            if ((context as? Activity)?.isTaskRoot == true) {
                context.moveTaskToBack(true)
            } else {
                (context as? Activity)?.finish()
            }

        }
    }
    val viewModelStoreOwner =
        remember(currentDestination.id + "-" + currentDestination.viewModelStore) {
            CustomViewModelStoreOwner(currentDestination.viewModelStore)
        }
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides viewModelStoreOwner
    ) {
        AnimateDestination(destination = currentDestination)
    }
}

class CustomViewModelStoreOwner(
    override val viewModelStore: ViewModelStore
) : ViewModelStoreOwner


class NavGraphViewModel(
    val startRoute: String,
    val navGraphBuilder: ScreenBuilder
) : ViewModel() {

    private val navScreens = MutableStateFlow<List<NavScreen>>(emptyList())
    private val navDestinations = MutableStateFlow<List<NavDestination>>(emptyList())


    private val nameToIdMap = mutableMapOf<String, String>()


    private var navController: NavController? = null

    private val _currentDestination = MutableStateFlow<NavDestination?>(null)
    val currentDestination: StateFlow<NavDestination?> = _currentDestination

    //实时保存最新的id用于恢复页面状态
    init {
        viewModelScope.launch {
            navDestinations.collect { list ->
                list.forEach { destination ->
                    nameToIdMap[destination.name] = destination.id
                }
            }
        }
        setNavBuilder()
    }

    //绑定路由控制者设定方法，销毁重组会导致controller被重新创建
    fun bindNavController(navController: NavController) {
        this.navController = navController
        setNavControllerNavigateFunc()
        setNavControllerPopBackFunc()
        setNavControllerPopBackDescFunc()
    }

    //设定导航构建者方法
    private fun setNavBuilder() {
        navGraphBuilder.setAddScreenFunc { name, isSingle, content ->
            val navScreen = NavScreen(name = name, isSingle = isSingle, content = content)
            val destination = NavDestination(name = name, content = navScreen.content)
            if (name == startRoute) {
                _currentDestination.update { destination }
                navDestinations.update { listOf(destination) }
            }
            navScreens.update { it + listOf(navScreen) }
        }
    }

    //导航方法
    private fun setNavControllerNavigateFunc() {
        navController?.setNavigateFunc { route, direction, restore ->
            val name = route::class.simpleName!!
            val navScreen = navScreens.value.find { it.name == name }
            if (navScreen == null) {
                throw IllegalArgumentException("找不到$route")
            }
            val id = if (restore) nameToIdMap[name] else UUID.randomUUID().toString()
            val destination = NavDestination(
                id = id ?: UUID.randomUUID().toString(),
                name = route::class.simpleName!!,
                arguments = route,
                direction = direction,
                content = navScreen.content,
            )
            val targetDestination = navDestinations.value.find { it.name == name }
            if (targetDestination != null && navScreen.isSingle) {
                val targetIndex = navDestinations.value.indexOf(targetDestination)
                navDestinations.value.subList(targetIndex, navDestinations.value.size).forEach {
                    it.viewModelStore.clear()
                }
                navDestinations.update { it.take(targetIndex) }
            }
            navDestinations.update { it + listOf(destination) }
            _currentDestination.update { destination }
        }
    }

    //回退方法
    private fun setNavControllerPopBackFunc() {
        navController?.setPopBackFunc {
            val value = navDestinations.value
            val size = value.size
            if (value.size >= 2) {
                val lastPreRoute = value[size - 2]
                value.last().viewModelStore.clear()
                navDestinations.update { it.take(it.size - 1) }
                _currentDestination.update { lastPreRoute.copy(direction = DirectionType.RIGHT) }
            }
            size == 1
        }
    }

    //回退指定页面方法
    private fun setNavControllerPopBackDescFunc() {
        navController?.setPopDestBackFunc { name ->
            val value = navDestinations.value
            val target = navDestinations.value.find { it.name == name }
            if (target == null) {
                throw IllegalArgumentException("找不到$name")
            }
            val targetIndex = value.indexOf(target)
            value.subList(targetIndex + 1, value.size).forEach {
                it.viewModelStore.clear()
            }
            navDestinations.update { it.subList(0, targetIndex + 1) }
            _currentDestination.update { target }
            navDestinations.value.size == 1
        }
    }


    companion object {
        fun providerFactory(
            startRoute: RouteWithArgs,
            builder: ScreenBuilder.() -> Unit
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Override
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val screenBuilder = ScreenBuilder()
                val navGraphBuilder = NavGraphViewModel(
                    startRoute = startRoute::class.simpleName!!,
                    navGraphBuilder = screenBuilder
                ) as T
                screenBuilder.apply(builder)
                return navGraphBuilder
            }
        }
    }
}


class ScreenBuilder() {

    var addScreenFun: ((String, Boolean, (@Composable (RouteWithArgs?) -> Unit)) -> Unit)? = null

    fun setAddScreenFunc(addFun: (String, Boolean, (@Composable (RouteWithArgs?) -> Unit)) -> Unit) {
        addScreenFun = addFun
    }

    inline fun <reified T : Any> navScreen(
        isSingle: Boolean = true,
        noinline content: @Composable (RouteWithArgs?) -> Unit
    ) {
        addScreenFun?.invoke(T::class.simpleName!!, isSingle, content)
    }

}

class NavController {

    private var navigateFun: ((RouteWithArgs, DirectionType, Boolean) -> Unit)? = null

    private var popBackFun: ((RouteWithArgs?) -> Boolean)? = null

    var popBackDestFun: ((String) -> Boolean)? = null

    fun setNavigateFunc(navigateFun: (RouteWithArgs, DirectionType, Boolean) -> Unit) {
        this.navigateFun = navigateFun
    }

    fun setPopBackFunc(popBackFun: (RouteWithArgs?) -> Boolean) {
        this.popBackFun = popBackFun
    }

    fun setPopDestBackFunc(popBackDestFun: (String) -> Boolean) {
        this.popBackDestFun = popBackDestFun
    }

    fun navigate(target: RouteWithArgs, restore: Boolean = false) {
        navigateFun?.invoke(target, DirectionType.LEFT, restore)
    }

    fun popBack(target: RouteWithArgs? = null): Boolean {
        return popBackFun?.invoke(target) == true
    }

    inline fun <reified T : RouteWithArgs> popBackDest(): Boolean {
        return popBackDestFun?.invoke(T::class.java.simpleName) == true
    }
}
