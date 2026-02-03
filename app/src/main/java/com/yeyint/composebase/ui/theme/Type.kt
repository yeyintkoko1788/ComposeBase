package com.yeyint.composebase.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.R

private val mukaMaheeFamily = FontFamily(
    Font(R.font.mukta_mahee_regular , FontWeight.Normal) ,
    Font(R.font.mukta_mahee_medium , FontWeight.Medium) ,
    Font(R.font.mukta_mahee_semi_bold , FontWeight.SemiBold) ,
    Font(R.font.mukta_mahee_bold , FontWeight.Bold) ,
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 18.sp ,
        lineHeight = 24.sp ,
        letterSpacing = 0.sp
    ) ,
    bodyMedium = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 16.sp ,
        lineHeight = 24.sp ,
        letterSpacing = 0.sp
    ) ,
    bodySmall = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 14.sp ,
        lineHeight = 22.sp ,
        letterSpacing = 0.sp
    )
)

val Typography.boldHeading3 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 32.sp ,
        lineHeight = 38.sp ,
        letterSpacing = 0.sp
    )
val Typography.boldHeading4 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 30.sp ,
        lineHeight = 38.sp ,
        letterSpacing = 0.sp
    )
val Typography.boldHeading6 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 24.sp ,
        lineHeight = 36.sp ,
        letterSpacing = 0.sp
    )
val Typography.boldBodyXLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 20.sp ,
        lineHeight = 28.sp ,
        letterSpacing = 0.sp
    )
val Typography.boldBodyLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 18.sp ,
        lineHeight = 26.sp ,
        letterSpacing = 0.sp
    )
val Typography.boldBodyMedium : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 16.sp ,
        lineHeight = 24.sp ,
        letterSpacing = 0.sp
    )
val Typography.boldBodySmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 14.sp ,
        lineHeight = 22.sp ,
        letterSpacing = 0.sp
    )
val Typography.boldBodyXSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Bold ,
        fontSize = 12.sp ,
        lineHeight = 20.sp ,
        letterSpacing = 0.sp
    )

val Typography.semiboldHeading3 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 44.sp ,
        lineHeight = 48.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldHeading4 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 32.sp ,
        lineHeight = 38.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldHeading5 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 28.sp ,
        lineHeight = 40.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldHeading6 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 24.sp ,
        lineHeight = 36.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldHeading7 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 22.sp ,
        lineHeight = 36.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldBodyXLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 20.sp ,
        lineHeight = 28.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldBodyLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 18.sp ,
        lineHeight = 26.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldBodyMedium : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 16.sp ,
        lineHeight = 24.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldBodySmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 14.sp ,
        lineHeight = 22.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldBodyXSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 12.sp ,
        lineHeight = 20.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldBody2XSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 10.sp ,
        lineHeight = 18.sp ,
        letterSpacing = 0.sp
    )
val Typography.semiboldBody3XSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.SemiBold ,
        fontSize = 9.sp ,
        lineHeight = 18.sp ,
        letterSpacing = 0.sp
    )

val Typography.mediumHeading6 : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 24.sp ,
        lineHeight = 36.sp ,
        letterSpacing = 0.sp
    )
val Typography.mediumBodyXLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 20.sp ,
        lineHeight = 28.sp ,
        letterSpacing = 0.sp
    )
val Typography.mediumBodyLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 18.sp ,
        lineHeight = 26.sp ,
        letterSpacing = 0.sp
    )
val Typography.mediumBodyMedium : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 16.sp ,
        lineHeight = 24.sp ,
        letterSpacing = 0.sp
    )

val Typography.mediumBodyMediumTextField : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 16.sp ,
        letterSpacing = 0.sp
    )
val Typography.mediumBodySmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 14.sp ,
        lineHeight = 22.sp ,
        letterSpacing = 0.sp
    )
val Typography.mediumBodyXSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 12.sp ,
        lineHeight = 20.sp ,
        letterSpacing = 0.sp
    )
val Typography.mediumBody2XSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize = 10.sp ,
        lineHeight = 18.sp ,
        letterSpacing = 0.sp
    )
val Typography.mediumBody3XSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Medium ,
        fontSize =9.sp ,
        lineHeight = 18.sp ,
        letterSpacing = 0.sp
    )

val Typography.regularBodyXLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 20.sp ,
        lineHeight = 28.sp ,
        letterSpacing = 0.sp
    )
val Typography.regularBodyLarge : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 18.sp ,
        lineHeight = 26.sp ,
        letterSpacing = 0.sp
    )
val Typography.regularBodyMedium : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 16.sp ,
        lineHeight = 24.sp ,
        letterSpacing = 0.sp
    )
val Typography.regularBodySmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 14.sp ,
        lineHeight = 22.sp ,
        letterSpacing = 0.sp
    )
val Typography.regularBodyXSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 12.sp ,
        lineHeight = 20.sp ,
        letterSpacing = 0.sp
    )
val Typography.regularBody2XSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 10.sp ,
        lineHeight = 18.sp ,
        letterSpacing = 0.sp
    )
val Typography.regularBody3XSmall : TextStyle
    get() = TextStyle(
        fontFamily = mukaMaheeFamily ,
        fontWeight = FontWeight.Normal ,
        fontSize = 8.sp ,
        lineHeight = 18.sp ,
        letterSpacing = 0.sp
    )

@Preview
@Composable
private fun TypographyPreview() {
    BasePreviewWrapper {
        Column(
            modifier = Modifier.fillMaxSize() ,
            horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Muka Mahee boldHeading3" ,
                style = MaterialTheme.typography.boldHeading3
            )
            Text(
                text = "Muka Mahee boldBodyXLarge" ,
                style = MaterialTheme.typography.boldBodyXLarge
            )
            Text(
                text = "Muka Mahee boldBodyLarge" ,
                style = MaterialTheme.typography.boldBodyLarge
            )
            Text(
                text = "Muka Mahee boldBodyMedium" ,
                style = MaterialTheme.typography.boldBodyMedium
            )
            Text(
                text = "Muka Mahee boldBodySmall" ,
                style = MaterialTheme.typography.boldBodySmall
            )
            Text(
                text = "Muka Mahee boldBodyXSmall" ,
                style = MaterialTheme.typography.boldBodyXSmall
            )

            Text(
                text = "Muka Mahee semiboldHeading3" ,
                style = MaterialTheme.typography.semiboldHeading3
            )
            Text(
                text = "Muka Mahee semiboldBodyXLarge" ,
                style = MaterialTheme.typography.semiboldBodyXLarge
            )
            Text(
                text = "Muka Mahee semiboldBodyLarge" ,
                style = MaterialTheme.typography.semiboldBodyLarge
            )
            Text(
                text = "Muka Mahee semiboldBodyMedium" ,
                style = MaterialTheme.typography.semiboldBodyMedium
            )
            Text(
                text = "Muka Mahee semiboldBodySmall" ,
                style = MaterialTheme.typography.semiboldBodySmall
            )
            Text(
                text = "Muka Mahee semiboldBody3XSmall" ,
                style = MaterialTheme.typography.semiboldBody3XSmall
            )

            Text(
                text = "Muka Mahee mediumHeading6" ,
                style = MaterialTheme.typography.mediumHeading6
            )
            Text(
                text = "Muka Mahee mediumBodyXLarge" ,
                style = MaterialTheme.typography.mediumBodyXLarge
            )
            Text(
                text = "Muka Mahee mediumBodyLarge" ,
                style = MaterialTheme.typography.mediumBodyLarge
            )
            Text(
                text = "Muka Mahee mediumBodyMedium" ,
                style = MaterialTheme.typography.mediumBodyMedium
            )
            Text(
                text = "Muka Mahee mediumBodySmall" ,
                style = MaterialTheme.typography.mediumBodySmall
            )
            Text(
                text = "Muka Mahee mediumBody2XSmall" ,
                style = MaterialTheme.typography.mediumBody2XSmall
            )

            Text(
                text = "Muka Mahee regularBodyXLarge" ,
                style = MaterialTheme.typography.regularBodyXLarge
            )
            Text(
                text = "Muka Mahee regularBodyLarge" ,
                style = MaterialTheme.typography.regularBodyLarge
            )
            Text(
                text = "Muka Mahee regularBodyMedium" ,
                style = MaterialTheme.typography.regularBodyMedium
            )
            Text(
                text = "Muka Mahee regularBodySmall" ,
                style = MaterialTheme.typography.regularBodySmall
            )
            Text(
                text = "Muka Mahee regularBody3XSmall" ,
                style = MaterialTheme.typography.regularBody3XSmall
            )
        }
    }
}