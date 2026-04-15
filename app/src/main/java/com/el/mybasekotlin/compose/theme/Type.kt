package com.vcc.eventapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.el.mybasekotlin.R

// Set of Material typography styles to start with

val beVietnamProFamily = FontFamily(
    Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_semi_bold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold),
    Font(R.font.be_vietnam_pro_extra_bold, FontWeight.ExtraBold)
)

val interFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold),
)

val workSansFamily = FontFamily(
    Font(R.font.work_sans_regular, FontWeight.Normal),
    Font(R.font.work_sans_medium, FontWeight.Medium),
    Font(R.font.work_sans_bold, FontWeight.Bold),
)


val CustomTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = beVietnamProFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 36.sp
    ),

//    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = beVietnamProFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 32.sp
    ),

    titleMedium = TextStyle(
        fontFamily = beVietnamProFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),

    titleSmall = TextStyle(
        fontFamily = beVietnamProFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),

    labelSmall = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    ),

    labelMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),

    labelLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 44.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = beVietnamProFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = beVietnamProFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),

    bodySmall = TextStyle(
        fontFamily = beVietnamProFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp
    ),

//    */
)
@Composable
fun customTextStyle(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontSize: TextUnit = 18.sp,
    fontStyle:FontStyle=FontStyle.Normal
): TextStyle {
    return remember(fontFamily, fontWeight, fontSize) {
        TextStyle(
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontStyle = fontStyle
        )
    }
}


@Composable
fun beVietNamRegular(fontSize: TextUnit = 18.sp) = customTextStyle(beVietnamProFamily, FontWeight.Normal, fontSize)

@Composable
fun beVietNamBold(fontSize: TextUnit = 18.sp) = customTextStyle(beVietnamProFamily, FontWeight.Bold, fontSize)

@Composable
fun beVietNamMedium(fontSize: TextUnit = 18.sp) = customTextStyle(beVietnamProFamily, FontWeight.Medium, fontSize)
@Composable
fun beVietNamSemibold(fontSize: TextUnit = 18.sp) = customTextStyle(beVietnamProFamily, FontWeight.SemiBold, fontSize)
@Composable
fun beVietNamItalic(fontSize: TextUnit = 18.sp) = customTextStyle(beVietnamProFamily, FontWeight.Normal, fontSize,fontStyle = FontStyle.Italic)
@Composable
fun beVietNamExtraBold(fontSize: TextUnit = 18.sp) = customTextStyle(beVietnamProFamily, FontWeight.ExtraBold, fontSize)
//interFamily
@Composable
fun interFamilyRegular(fontSize: TextUnit = 18.sp) = customTextStyle(interFamily, FontWeight.Normal, fontSize)

@Composable
fun interFamilyBold(fontSize: TextUnit = 18.sp) = customTextStyle(interFamily, FontWeight.Bold, fontSize)

@Composable
fun interFamilyMedium(fontSize: TextUnit = 18.sp) = customTextStyle(interFamily, FontWeight.Medium, fontSize)