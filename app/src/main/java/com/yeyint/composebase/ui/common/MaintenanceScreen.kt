package com.yeyint.composebase.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.domain.model.MaintenanceInfo
import com.yeyint.composebase.ui.locale.LocalLanguage
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.boldHeading4
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.semiboldHeading6
import com.yeyint.composebase.ui.theme.surface_bg
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.text_light

/**
 * Whole-app takeover shown when any API call comes back HTTP 512 ("under maintenance") — see
 * [com.yeyint.composebase.network.components.MaintenanceEventBus]. Deliberately no retry: unlike a
 * device being offline, there's nothing the rider can do to make scheduled maintenance end sooner,
 * so this just sits until they reopen the app later (by which point the backend, and this new
 * process's own `MaintenanceEventBus`, start clean).
 */
@Composable
fun MaintenanceScreen(info: MaintenanceInfo = MaintenanceInfo()) {
    val languageCode = LocalLanguage.current.code
    // The backend's own wording for each part when the response had it (title from `meta.message`,
    // description from `body`); the built-in text only as a fallback — e.g. a bare proxy 503 with
    // no JSON body at all.
    val title = info.title?.forLanguage(languageCode)?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.maintenance_title)
    val description = info.description?.forLanguage(languageCode)?.takeIf { it.isNotBlank() }
    // Scrollable so a long description can't push the title off-screen; centred when it fits.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surface_bg)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_maintenance),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(280.dp)
                    .height(280.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.semiboldHeading6,
                color = text_default,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (description != null) {
                CmsWebView(html = description.asHtml(), centered = true)
            } else {
                Text(
                    text = stringResource(R.string.maintenance_subtitle),
                    style = MaterialTheme.typography.regularBodyMedium,
                    color = text_light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// A body with no tags is plain text, where a bare "\n" would collapse to a space in HTML — turned
// into <br> so its line breaks survive. Anything already containing markup is left exactly as the
// backend wrote it.
private fun String.asHtml(): String = if ('<' in this) this else replace("\n", "<br>")

@Preview(showBackground = true)
@Composable
private fun MaintenanceScreenPreview() {
    BasePreviewWrapper {
        MaintenanceScreen()
    }
}
