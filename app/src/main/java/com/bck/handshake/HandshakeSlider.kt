/**
 * HandshakeSlider component implementation.
 * This file contains the slider component and its supporting classes for confirming actions
 * through an interactive handshake gesture.
 */
package com.bck.handshake

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bck.handshake.ui.theme.HandshakeSliderConfirmed
import com.bck.handshake.ui.theme.HandshakeSliderUnconfirmed
import com.bck.handshake.ui.theme.HandshakeSliderTextConfirmed
import com.bck.handshake.ui.theme.HandshakeSliderTextUnconfirmed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Default values and configurations for the HandshakeSlider component.
 */
private object HandshakeSliderDefaults {
    /** Width of the slider container */
    val SliderWidth = 300.dp
    /** Size of the hand icons */
    val HandSize = 60.dp
    /** Padding around the slider container */
    val SliderPadding = 8.dp
    /** Padding between hands and container edges */
    val HandPadding = 20.dp
    /** Threshold for confirming the handshake (0.9 = 90% of max distance) */
    val ConfirmationThreshold = 0.9f
    /** Delay before resetting the slider after confirmation */
    const val ResetDelay = 1000L
    /** Animation specification for background color changes */
    val BackgroundAnimationSpec: AnimationSpec<Float> = tween(300)
}

/**
 * State holder for the HandshakeSlider.
 * Manages the position of the draggable hand and the confirmation state.
 */
@Stable
class HandshakeSliderState {
    /** Current horizontal offset of the left hand */
    var leftOffsetX by mutableStateOf(0f)
    /** Whether the handshake has been confirmed */
    var hasConfirmed by mutableStateOf(false)
    
    /**
     * Resets the slider to its initial state.
     */
    fun reset() {
        leftOffsetX = 0f
        hasConfirmed = false
    }
}

/**
 * Creates and remembers a HandshakeSliderState instance.
 * @return A new or remembered HandshakeSliderState
 */
@Composable
fun rememberHandshakeSliderState(): HandshakeSliderState {
    return remember { HandshakeSliderState() }
}

/**
 * A custom slider component that implements a handshake confirmation gesture.
 * The slider features two hands that move towards each other, with the left hand being draggable.
 * When the hands meet in the middle, it triggers a confirmation action.
 *
 * Features:
 * - Draggable left hand with spring-back animation if not confirmed
 * - Mirrored right hand that moves in response to the left hand
 * - Visual feedback with color changes and text updates
 * - Customizable confirmation threshold and reset delay
 *
 * @param onConfirmed Callback function triggered when the hands meet in the middle
 * @param modifier Optional modifier for customizing the component's layout
 * @param state Optional state holder for the slider
 */
@Composable
fun HandshakeSlider(
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
    state: HandshakeSliderState = rememberHandshakeSliderState()
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Calculate the maximum offset once and remember it
    val maxOffset = remember(density) {
        with(density) {
            (HandshakeSliderDefaults.SliderWidth.toPx() - 
             HandshakeSliderDefaults.HandSize.toPx() - 
             HandshakeSliderDefaults.HandPadding.toPx()) / 2
        }
    }

    // Calculate the right hand position as a mirror of the left hand
    val rightOffsetX = remember(state.leftOffsetX, density) {
        with(density) {
            HandshakeSliderDefaults.SliderWidth.toPx() - 
            HandshakeSliderDefaults.HandSize.toPx() - 
            HandshakeSliderDefaults.HandPadding.toPx() - 
            state.leftOffsetX
        }
    }

    // Animated background opacity
    val backgroundColor by animateFloatAsState(
        targetValue = if (state.hasConfirmed) 0.6f else 0.3f,
        animationSpec = HandshakeSliderDefaults.BackgroundAnimationSpec,
        label = "backgroundColor"
    )

    Box(modifier = modifier.padding(16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status text - only show when not confirmed
            if (!state.hasConfirmed) {
                Text(
                    text = "Slide to Confirm",
                    color = HandshakeSliderTextUnconfirmed
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Main slider container
            Box(
                modifier = Modifier
                    .width(HandshakeSliderDefaults.SliderWidth)
                    .height(80.dp)
                    .background(
                        if (state.hasConfirmed) 
                            HandshakeSliderConfirmed.copy(alpha = backgroundColor)
                        else HandshakeSliderUnconfirmed.copy(alpha = backgroundColor),
                        RoundedCornerShape(50)
                    )
                    .padding(HandshakeSliderDefaults.SliderPadding)
            ) {
                // Only show hands if not confirmed
                if (!state.hasConfirmed) {
                    // Left hand - draggable
                    DraggableHand(
                        offsetX = state.leftOffsetX,
                        maxOffset = maxOffset,
                        isLeft = true,
                        onDragEnd = {
                            if (!state.hasConfirmed && state.leftOffsetX < maxOffset * HandshakeSliderDefaults.ConfirmationThreshold) {
                                state.leftOffsetX = 0f
                            }
                        },
                        onDrag = { dragAmount ->
                            val newValue = state.leftOffsetX + dragAmount
                            state.leftOffsetX = newValue.coerceIn(0f, maxOffset)

                            if (!state.hasConfirmed && state.leftOffsetX >= maxOffset * HandshakeSliderDefaults.ConfirmationThreshold) {
                                state.hasConfirmed = true
                                onConfirmed()
                            }
                        }
                    )

                    // Right hand - mirrors left hand movement
                    StaticHand(
                        offsetX = rightOffsetX,
                        isLeft = false
                    )
                } else {
                    // Show confirmed text when confirmed
                    Text(
                        text = "Confirmed",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

/**
 * Composable for the draggable hand in the HandshakeSlider.
 * Handles drag gestures and updates the hand's position.
 *
 * @param offsetX Current horizontal offset of the hand
 * @param maxOffset Maximum allowed offset for the hand
 * @param isLeft Whether this is the left hand (true) or right hand (false)
 * @param onDragEnd Callback triggered when drag gesture ends
 * @param onDrag Callback triggered during drag with the drag amount
 */
@Composable
private fun DraggableHand(
    offsetX: Float,
    maxOffset: Float,
    isLeft: Boolean,
    onDragEnd: () -> Unit,
    onDrag: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .size(HandshakeSliderDefaults.HandSize)
            .offset(x = with(LocalDensity.current) { offsetX.toDp() })
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = { onDragEnd() }
                ) { _, dragAmount -> onDrag(dragAmount) }
            }
    ) {
        Image(
            painter = painterResource(
                id = if (isLeft) R.drawable.left_hand else R.drawable.right_hand
            ),
            contentDescription = if (isLeft) "Left Hand" else "Right Hand",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    end = if (isLeft) HandshakeSliderDefaults.HandPadding else 0.dp,
                    start = if (!isLeft) HandshakeSliderDefaults.HandPadding else 0.dp
                )
        )
    }
}

/**
 * Composable for the static (non-draggable) hand in the HandshakeSlider.
 * Mirrors the movement of the draggable hand.
 *
 * @param offsetX Current horizontal offset of the hand
 * @param isLeft Whether this is the left hand (true) or right hand (false)
 */
@Composable
private fun StaticHand(
    offsetX: Float,
    isLeft: Boolean
) {
    Box(
        modifier = Modifier
            .size(HandshakeSliderDefaults.HandSize)
            .offset(x = with(LocalDensity.current) { offsetX.toDp() })
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(
                id = if (isLeft) R.drawable.left_hand else R.drawable.right_hand
            ),
            contentDescription = if (isLeft) "Left Hand" else "Right Hand",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    end = if (isLeft) HandshakeSliderDefaults.HandPadding else 0.dp,
                    start = if (!isLeft) HandshakeSliderDefaults.HandPadding else 0.dp
                )
        )
    }
}

/**
 * Preview function for the HandshakeSlider component.
 * Displays the slider in its default state.
 */
@Preview(showBackground = true)
@Composable
private fun PreviewHandshakeSlider() {
    HandshakeSlider(onConfirmed = {})
} 