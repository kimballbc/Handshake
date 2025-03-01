package com.bck.handshake

import androidx.compose.animation.core.animateFloatAsState
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A custom slider component that implements a handshake confirmation gesture.
 * The slider features two hands that move towards each other, with the left hand being draggable.
 * When the hands meet in the middle, it triggers a confirmation action.
 *
 * Features:
 * - Draggable left hand with spring-back animation if not confirmed
 * - Mirrored right hand that moves in response to the left hand
 * - Visual feedback with color changes and text updates
 * - Snackbar message on confirmation
 *
 * @param onConfirmed Callback function triggered when the hands meet in the middle
 * @param modifier Optional modifier for customizing the component's layout
 */
@Composable
fun HandshakeSlider(
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Constants for slider dimensions
    val sliderWidth = 300.dp
    val handSize = 60.dp
    val sliderPadding = 8.dp
    val handPadding = 20.dp
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Calculate the maximum distance the left hand can move
    // We divide by 2 since the hands meet in the middle
    val maxOffset = with(LocalDensity.current) {
        (sliderWidth.toPx() - handSize.toPx() - handPadding.toPx()) / 2
    }

    // State management
    var leftOffsetX by remember { mutableStateOf(0f) }  // Position of left hand
    var hasConfirmed by remember { mutableStateOf(false) }  // Confirmation state

    // Calculate the right hand position as a mirror of the left hand
    // As left hand moves right, right hand moves left by the same amount
    val rightOffsetX = with(LocalDensity.current) {
        sliderWidth.toPx() - handSize.toPx() - handPadding.toPx() - leftOffsetX
    }

    // Animated background opacity that changes on confirmation
    val backgroundColor by animateFloatAsState(
        targetValue = if (hasConfirmed) 0.6f else 0.3f,
        label = "backgroundColor"
    )

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Status text that changes based on confirmation state
            Text(
                text = if (hasConfirmed) "Confirmed!" else "Slide to Confirm",
                color = if (hasConfirmed) Color.Green else Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main slider container
            Box(
                modifier = Modifier
                    .width(sliderWidth)
                    .height(80.dp)
                    .background(
                        if (hasConfirmed) Color.Green.copy(alpha = backgroundColor)
                        else Color.LightGray.copy(alpha = backgroundColor),
                        RoundedCornerShape(50)
                    )
                    .padding(sliderPadding)
            ) {
                // Left hand - draggable
                Box(
                    modifier = Modifier
                        .size(handSize)
                        .offset(x = with(LocalDensity.current) { leftOffsetX.toDp() })
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (!hasConfirmed && leftOffsetX < maxOffset * 0.9f) {
                                        leftOffsetX = 0f
                                    }
                                }
                            ) { _, dragAmount ->
                                val newValue = leftOffsetX + dragAmount
                                leftOffsetX = newValue.coerceIn(0f, maxOffset)

                                if (!hasConfirmed && leftOffsetX >= maxOffset * 0.9f) {
                                    hasConfirmed = true
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Confirmed!", duration = SnackbarDuration.Short)
                                        delay(250) // Wait for .25 second
                                        leftOffsetX = 0f // Reset position
                                        hasConfirmed = false
                                    }
                                    onConfirmed()
                                }
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.left_hand),
                        contentDescription = "Left Hand",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = handPadding)
                    )
                }

                // Right hand - mirrors left hand movement
                Box(
                    modifier = Modifier
                        .size(handSize)
                        .offset(x = with(LocalDensity.current) { rightOffsetX.toDp() })
                        .background(Color.Transparent)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.right_hand),
                        contentDescription = "Right Hand",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = handPadding)
                    )
                }
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Preview function for the HandshakeSliderNew component
 */
@Preview(showBackground = true)
@Composable
fun PreviewHandshakeSliderNew() {
    HandshakeSlider(onConfirmed = {})
} 