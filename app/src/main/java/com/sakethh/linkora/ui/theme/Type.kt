package com.sakethh.linkora.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.sakethh.linkora.R

val poppinsFontFamily = FontFamily(
    Font(R.font.semibold, weight = FontWeight.SemiBold),
    Font(R.font.medium, weight = FontWeight.Medium),
    Font(R.font.regular, weight = FontWeight.Normal)
)

val playWriteITTradFontFamily = FontFamily(
    Font(R.font.playwriteittrad, weight = FontWeight.Normal)
)

val Typography = Typography(
    titleLarge = TextStyle(fontFamily = poppinsFontFamily, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontFamily = poppinsFontFamily, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontFamily = poppinsFontFamily, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontFamily = playWriteITTradFontFamily, fontWeight = FontWeight.Normal)
)