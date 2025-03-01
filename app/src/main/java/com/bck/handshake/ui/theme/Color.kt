/**
 * Color definitions for the Handshake application.
 * This file contains all color values used throughout the app to maintain consistency
 * and enable easy theming.
 */
package com.bck.handshake.ui.theme

import androidx.compose.ui.graphics.Color

// Material Theme Colors
/** Primary purple color for light theme */
val Purple80 = Color(0xFFD0BCFF)
/** Secondary purple-grey color for light theme */
val PurpleGrey80 = Color(0xFFCCC2DC)
/** Accent pink color for light theme */
val Pink80 = Color(0xFFEFB8C8)

/** Primary purple color for dark theme */
val Purple40 = Color(0xFF6650a4)
/** Secondary purple-grey color for dark theme */
val PurpleGrey40 = Color(0xFF625b71)
/** Accent pink color for dark theme */
val Pink40 = Color(0xFF7D5260)

// Neutral Colors
/** Pure black color for text and borders */
val Black = Color(0xFF000000)
/** Dark grey color for surfaces and backgrounds */
val DarkGrey = Color(0xFF1C1B1F)
/** Medium grey color for secondary elements */
val MedGrey = Color(0xFF625B71)

// HandshakeSlider Colors
/** Background color for the unconfirmed state of the HandshakeSlider */
val HandshakeSliderUnconfirmed = Color.LightGray
/** Background color for the confirmed state of the HandshakeSlider */
val HandshakeSliderConfirmed = Color.Green
/** Text color for the unconfirmed state of the HandshakeSlider */
val HandshakeSliderTextUnconfirmed = Color.Gray
/** Text color for the confirmed state of the HandshakeSlider */
val HandshakeSliderTextConfirmed = Color.Green