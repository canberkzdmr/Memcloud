package com.cbo.memcloud.core.ui.cards

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    swipeType: CardSwipeType = CardSwipeType.NONE,
    swipeThreshold: Float = 100f,
    maxSwipeOffset: Float = 150f,
    dragSensitivity: Float = 0.5f,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    swipeBackground: @Composable (showLeftAction: Boolean, showRightAction: Boolean) -> Unit = { left, right ->
        DefaultSwipeBackground(swipeType, left, right)
    },
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // Function to trigger vibration
    fun vibrate(duration: Long = 50) {
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    if (swipeType == CardSwipeType.NONE) {
        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            content()
        }
    } else {
        var offsetX by remember { mutableStateOf(0f) }
        var showLeftAction by remember { mutableStateOf(false) }
        var showRightAction by remember { mutableStateOf(false) }
        var hasVibratedLeft by remember { mutableStateOf(false) }
        var hasVibratedRight by remember { mutableStateOf(false) }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            swipeBackground(showLeftAction, showRightAction)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = offsetX.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX > swipeThreshold && swipeType == CardSwipeType.BOTH -> {
//                                        vibrate(100) // Longer vibration on completion
                                        onSwipeRight()
                                    }
                                    offsetX < -swipeThreshold -> {
//                                        vibrate(100) // Longer vibration on completion
                                        onSwipeLeft()
                                    }
                                }
                                offsetX = 0f
                                showLeftAction = false
                                showRightAction = false
                                hasVibratedLeft = false
                                hasVibratedRight = false
                            },
                            onDragCancel = {
                                offsetX = 0f
                                showLeftAction = false
                                showRightAction = false
                                hasVibratedLeft = false
                                hasVibratedRight = false
                            }
                        ) { _, dragAmount ->
                            offsetX += dragAmount * dragSensitivity
                            offsetX = when (swipeType) {
                                CardSwipeType.BOTH -> offsetX.coerceIn(-maxSwipeOffset, maxSwipeOffset)
                                CardSwipeType.LEFT -> offsetX.coerceIn(-maxSwipeOffset, 0f)
                                CardSwipeType.NONE -> 0f
                            }
                            val newShowLeft = offsetX < -swipeThreshold
                            val newShowRight = offsetX > swipeThreshold  && swipeType == CardSwipeType.BOTH

                            // Vibrate when crossing threshold (short feedback)
                            if (newShowLeft && !hasVibratedLeft && !showLeftAction) {
                                vibrate(50)
                                hasVibratedLeft = true
                            } else if (!newShowLeft && hasVibratedLeft) {
                                hasVibratedLeft = false
                            }

                            if (newShowRight && !hasVibratedRight && !showRightAction) {
                                vibrate(50)
                                hasVibratedRight = true
                            } else if (!newShowRight && hasVibratedRight) {
                                hasVibratedRight = false
                            }

                            showLeftAction = newShowLeft
                            showRightAction = newShowRight
                        }
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DefaultSwipeBackground(
    swipeType: CardSwipeType,
    showLeftAction: Boolean,
    showRightAction: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    showLeftAction -> Color.Red.copy(alpha = 0.2f)
                    showRightAction && swipeType == CardSwipeType.BOTH -> Color.Green.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (swipeType == CardSwipeType.BOTH) {
            Icon(
                imageVector = Icons.Default.Restore,
                contentDescription = "Restore",
                tint = if (showRightAction) Color.Green else Color.Gray,
                modifier = Modifier.padding(start = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = if (showLeftAction) Color.Red else Color.Gray,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

enum class CardSwipeType {
    NONE, LEFT, BOTH
}
