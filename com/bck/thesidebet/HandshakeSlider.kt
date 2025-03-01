package com.bck.thesidebet

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HandshakeSlider(
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sliderWidth = 300.dp
    val handSize = 60.dp
    val maxOffset = with(LocalDensity.current) { (sliderWidth.toPx() / 2) - (handSize.toPx() / 2) }
    val confirmationThreshold = 20f // Increased threshold for better UX
    
    // Spring animation specs for smooth movement
    val spring = Spring.DampingRatioMediumBouncy
    
    // Animated values for hand positions
    val leftHandAnim = remember { Animatable(-maxOffset) }
    val rightHandAnim = remember { Animatable(maxOffset) }
    
    var confirmed by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    // Alpha animation for confirmation effect
    val alpha = remember { Animatable(1f) }
    
    // Reset function
    fun reset() {
        scope.launch {
            confirmed = false
            alpha.snapTo(1f)
            leftHandAnim.animateTo(-maxOffset, spring())
            rightHandAnim.animateTo(maxOffset, spring())
        }
    }
    
    // Confirmation check with haptic feedback
    fun checkAndConfirm(leftX: Float, rightX: Float) {
        if (!confirmed && abs(leftX - rightX) < confirmationThreshold) {
            confirmed = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            scope.launch {
                // Animate hands to meet exactly in the middle
                leftHandAnim.animateTo(0f, spring())
                rightHandAnim.animateTo(0f, spring())
                
                // Fade out effect
                alpha.animateTo(0f, tween(500))
                onConfirmed()
                
                // Auto-reset after a delay
                kotlinx.coroutines.delay(1000)
                reset()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = if (!confirmed) "Slide to Confirm" else "Confirmed!",
            color = if (!confirmed) Color.Gray else Color.Green,
            modifier = Modifier.alpha(alpha.value)
        )

        Box(
            modifier = Modifier
                .width(sliderWidth)
                .height(80.dp)
                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(50))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left hand
                Image(
                    painter = painterResource(id = R.drawable.left_hand),
                    contentDescription = "Left Hand",
                    modifier = Modifier
                        .size(handSize)
                        .offset(x = with(LocalDensity.current) { leftHandAnim.value.toDp() })
                        .alpha(alpha.value)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (!confirmed) {
                                        scope.launch {
                                            leftHandAnim.animateTo(-maxOffset, spring())
                                        }
                                    }
                                }
                            ) { _, dragAmount ->
                                scope.launch {
                                    val newValue = leftHandAnim.value + dragAmount
                                    leftHandAnim.snapTo(newValue.coerceIn(-maxOffset, 0f))
                                    checkAndConfirm(leftHandAnim.value, rightHandAnim.value)
                                }
                            }
                        }
                )

                // Right hand
                Image(
                    painter = painterResource(id = R.drawable.right_hand),
                    contentDescription = "Right Hand",
                    modifier = Modifier
                        .size(handSize)
                        .offset(x = with(LocalDensity.current) { rightHandAnim.value.toDp() })
                        .alpha(alpha.value)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (!confirmed) {
                                        scope.launch {
                                            rightHandAnim.animateTo(maxOffset, spring())
                                        }
                                    }
                                }
                            ) { _, dragAmount ->
                                scope.launch {
                                    val newValue = rightHandAnim.value + dragAmount
                                    rightHandAnim.snapTo(newValue.coerceIn(0f, maxOffset))
                                    checkAndConfirm(leftHandAnim.value, rightHandAnim.value)
                                }
                            }
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHandshakeSlider() {
    HandshakeSlider(onConfirmed = {})
} 