package com.xz.customnav.ui.nav

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


enum class DirectionType {
    LEFT, RIGHT;
}

class DestinationLayerState {
    private var offsetX by mutableFloatStateOf(0f)
    private var _lastDirection by mutableStateOf(DirectionType.LEFT)
    val lastDirection get() = _lastDirection

    var zIndexA by mutableFloatStateOf(0f)
    var zIndexB by mutableFloatStateOf(1f)

    var destinationA by mutableStateOf<NavDestination?>(null)
    var destinationB by mutableStateOf<NavDestination?>(null)

    fun updateLastDirection(value: DirectionType) {
        _lastDirection = value
    }

    fun updateOffset(value: Float) {
        offsetX = value
    }

    fun assignToUpperLayer(dest: NavDestination) {
        if (zIndexA < zIndexB) {
            destinationA = dest
            zIndexA += 2
        } else {
            destinationB = dest
            zIndexB += 2
        }
    }

    fun assignToLowerLayer(dest: NavDestination) {
        if (zIndexA > zIndexB) {
            destinationA = dest
            zIndexA -= 2
        } else {
            destinationB = dest
            zIndexB -= 2
        }
    }

    fun assignToTopLayer(dest: NavDestination) {
        if (zIndexA > zIndexB) {
            destinationA = dest
        } else {
            destinationB = dest
        }
    }

    fun assignToBottomLayer(dest: NavDestination) {
        if (zIndexA < zIndexB) {
            destinationA = dest
        } else {
            destinationB = dest
        }
    }

    fun isNeedAnimate(): Boolean {
        return destinationA != null && destinationB != null
    }

    fun offsetForA(): IntOffset {
        return IntOffset(if (zIndexA > zIndexB) offsetX.toInt() else 0, 0)
    }

    fun offsetForB(): IntOffset {
        return IntOffset(if (zIndexB > zIndexA) offsetX.toInt() else 0, 0)
    }
}


@Composable
fun AnimateDestination(
    destination: NavDestination?
) {

    if (destination == null) return

    val width = LocalConfiguration.current.screenWidthDp
    val widthPx = with(LocalDensity.current) { width.dp.toPx() }

    val layerState = remember { DestinationLayerState() }

    val saveableHolder = rememberSaveableStateHolder()

    LaunchedEffect(destination.id) {
        when (destination.direction) {
            DirectionType.LEFT -> {
                if (layerState.lastDirection == DirectionType.LEFT) {
                    layerState.assignToUpperLayer(destination)
                } else {
                    layerState.assignToTopLayer(destination)
                }
            }

            DirectionType.RIGHT -> {
                if (layerState.lastDirection == DirectionType.RIGHT) {
                    layerState.assignToLowerLayer(destination)
                } else {
                    layerState.assignToBottomLayer(destination)
                }
            }
        }
        layerState.updateLastDirection(destination.direction)
        if (layerState.isNeedAnimate()) {
            when (destination.direction) {
                DirectionType.LEFT -> destinationAnimate(
                    start = widthPx,
                    end = 0f,
                    easing = CubicBezierEasing(0.4f, 1f, 0.9f, 1f)
                ) { layerState.updateOffset(it) }

                DirectionType.RIGHT -> destinationAnimate(
                    start = 0f,
                    end = widthPx,
                    frame = 90,
                    easing = CubicBezierEasing(0.4f, 0.8f, 0.9f, 1f)
                ) { layerState.updateOffset(it) }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        layerState.destinationA?.let {
            DestinationContent(
                modifier = Modifier
                    .zIndex(layerState.zIndexA)
                    .offset { layerState.offsetForA() },
                destination = it,
                saveableHolder = saveableHolder
            )
        }

        layerState.destinationB?.let {
            DestinationContent(
                modifier = Modifier
                    .zIndex(layerState.zIndexB)
                    .offset { layerState.offsetForB() },
                destination = it,
                saveableHolder = saveableHolder
            )
        }
    }

}


@Composable
fun DestinationContent(
    modifier: Modifier,
    saveableHolder: SaveableStateHolder,
    destination: NavDestination,
) {
    Box(modifier) {
        destination.let {
            saveableHolder.SaveableStateProvider(it.id) {
                destination.content(destination.arguments)
            }
        }
    }
}

suspend fun destinationAnimate(
    start: Float,
    end: Float,
    easing: Easing = LinearEasing,
    frame: Int = 120,
    onUpdate: (Float) -> Unit
) = withContext(Dispatchers.Default) {
    val valueRange = end - start
    repeat(frame) { i ->
        val fraction = i.toFloat() / (frame - 1)
        val transform = easing.transform(fraction)
        val value = start + valueRange * transform
        onUpdate(value)
        delay(2)
    }
}
