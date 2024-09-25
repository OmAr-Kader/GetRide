package com.ramo.getride.android.global.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ramo.getride.android.global.base.Theme
import org.koin.compose.koinInject


enum class MultiFabState {
    COLLAPSED, EXPANDED
}

class FabItem(
    val icon: ImageVector,
    val label: String,
    val color: Color,
)

//https://github.com/khambhaytajaydip/MultipleFloatingActionButton/blob/main/composeutil/CustomFloatingButton.kt

@Composable
fun MultiFloatingActionButton(
    items: List<FabItem>,
    onClick: (Int) -> Unit,
) {
    val theme: Theme = koinInject()
    val currentState = remember { mutableStateOf(MultiFabState.COLLAPSED) }
    val stateTransition: Transition<MultiFabState> =
        updateTransition(targetState = currentState.value, label = "")
    val stateChange: () -> Unit = {
        currentState.value = if (stateTransition.currentState == MultiFabState.EXPANDED) {
            MultiFabState.COLLAPSED
        } else MultiFabState.EXPANDED
    }
    val curveColor = theme.backGreyTrans
    val rotation: Float by stateTransition.animateFloat(
        transitionSpec = {
            if (targetState == MultiFabState.EXPANDED) {
                spring(stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = ""
    ) { state ->
        if (state == MultiFabState.EXPANDED) 45f else 0f
    }
    val isEnable = currentState.value == MultiFabState.EXPANDED

    BackHandler(isEnable) {
        currentState.value = MultiFabState.COLLAPSED
    }

    val modifier = if (currentState.value == MultiFabState.EXPANDED)
        Modifier
            .fillMaxSize()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                currentState.value = MultiFabState.COLLAPSED
            } else Modifier.fillMaxSize()

    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            if (currentState.value == MultiFabState.EXPANDED) {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 2.2f
                        scaleY = 2.1f
                    }) {
                    translate(150f, top = 300f) {
                        scale(5f) {}
                        drawCircle(curveColor, radius = 200.dp.toPx())
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                items.forEachIndexed { index, item ->
                    SmallFloatingActionButtonRow(
                        item = item,
                        index = index,
                        theme = theme,
                        stateTransition = stateTransition,
                        onClick = onClick,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = theme.secondary,
                    onClick = {
                        stateChange()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = theme.textForPrimaryColor,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }

        }
    }

}


@Composable
fun SmallFloatingActionButtonRow(
    item: FabItem,
    index: Int,
    theme: Theme,
    stateTransition: Transition<MultiFabState>,
    onClick: (Int) -> Unit,
) {
    val alpha: Float by stateTransition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 50)
        }, label = ""
    ) { state ->
        if (state == MultiFabState.EXPANDED) 1f else 0f
    }
    val scale: Float by stateTransition.animateFloat(
        label = ""
    ) { state ->
        if (state == MultiFabState.EXPANDED) 1.0f else 0f
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .alpha(animateFloatAsState((alpha), label = "").value)
            .scale(animateFloatAsState(targetValue = scale, label = "").value)
    ) {
        ExtendedFloatingActionButton(
            text = { Text(text = item.label, color = theme.textForPrimaryColor) },
            onClick = {
                onClick.invoke(index)
            },
            containerColor = theme.primary,
            shape = RoundedCornerShape(15.dp),
            expanded = true,
            icon = {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = theme.textForPrimaryColor
                )
            }
        )
    }
}