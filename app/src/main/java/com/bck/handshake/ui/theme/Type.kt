package com.bck.handshake.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bck.handshake.R
// Set of Material styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val indieFlower = FontFamily(
    Font(R.font.indie_flower_regular, FontWeight.Normal)
)

val gloriaHallelujah = FontFamily(
    Font(R.font.gloria_hallelujah_regular)
)