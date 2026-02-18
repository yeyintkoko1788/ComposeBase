package com.yeyint.composebase.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.boldHeading6
import com.yeyint.composebase.ui.theme.mediumBodyMedium
import com.yeyint.composebase.ui.theme.neutral_text
import com.yeyint.composebase.ui.theme.neutral_text_light

@Composable
fun EmptyViewPod(
    modifier: Modifier = Modifier,
    code : String,
    title : String,
    subtitle : String,
    onRetry : () -> Unit,
    showRetry : Boolean = true
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //change image according to error code
        val painter = when(code){
            "404" -> {
                painterResource(id = R.drawable.ic_empty)
            }
            else -> {
                painterResource(id = R.drawable.ic_empty)
            }
        }

        Image(
            painter = painter,
            contentDescription = "Empty",
            modifier = Modifier.width(200.dp)
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.boldHeading6,
            color = neutral_text,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.mediumBodyMedium,
            color = neutral_text_light,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (showRetry){
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = "Retry",
                onClick = onRetry,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }

    }
}

@Preview
@Composable
private fun EmptyViewPodPreview() {
    BasePreviewWrapper {
        EmptyViewPod(
            code = "404" ,
            title = "Page Not Found" ,
            subtitle = "The page you are looking for does not exist." ,
            onRetry = {}
        )
    }
}