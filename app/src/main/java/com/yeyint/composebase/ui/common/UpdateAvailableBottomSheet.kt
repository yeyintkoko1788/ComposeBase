@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeyint.composebase.ui.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.locale.AppLanguageScope
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.surface_brand_light
import com.yeyint.composebase.ui.theme.regularBodySmall
import com.yeyint.composebase.ui.theme.semiboldBodySmall
import com.yeyint.composebase.ui.theme.semiboldBodyXLarge
import com.yeyint.composebase.ui.theme.surface_brand
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.text_light

/**
 * Shown app-wide (from `MainActivity`) after the background version check finds a newer build.
 * [isForceUpdate] blocks every way out except "Update Now" — swipe-down, tap-outside and back all
 * do nothing — since a forced update has no "Later" for the rider to fall back on.
 */
@Composable
fun UpdateAvailableBottomSheet(
    latestVersionLabel: String,
    releaseNotes: String,
    isForceUpdate: Boolean,
    onUpdateNow: () -> Unit,
    onLater: () -> Unit
) {
    BackHandler(enabled = isForceUpdate) {}

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue -> !(isForceUpdate && newValue == SheetValue.Hidden) }
    )
    ModalBottomSheet(
        onDismissRequest = { if (!isForceUpdate) onLater() },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = surface_default,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = !isForceUpdate)
    ) {
        // ModalBottomSheet composes in its own window, which re-provides LocalContext from the
        // Activity — see AppLanguageScope's doc comment.
        AppLanguageScope {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surface_brand_light)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_force_update),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 28.dp, bottom = 16.dp)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.version_update_latest_version_label, latestVersionLabel),
                    style = MaterialTheme.typography.semiboldBodySmall,
                    color = surface_brand
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.version_update_available_title),
                    style = MaterialTheme.typography.semiboldBodyXLarge,
                    color = text_default
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = releaseNotes,
                    style = MaterialTheme.typography.regularBodySmall,
                    color = text_light
                )
            }
            Column(modifier = Modifier.navigationBarsPadding().padding(16.dp)) {
                PrimaryButton(
                    text = stringResource(R.string.action_update_now),
                    onClick = onUpdateNow
                )
                if (!isForceUpdate) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryButton(
                        text = stringResource(R.string.action_later),
                        onClick = onLater
                    )
                }
            }
        }
        }
    }
}

@Preview(showBackground = true, name = "Soft Update")
@Composable
private fun UpdateAvailableBottomSheetSoftPreview() {
    BasePreviewWrapper {
        UpdateAvailableBottomSheet(
            latestVersionLabel = "V1.1",
            releaseNotes = "Improvements and bug fixes have been made to enhance the app " +
                "experience. Update to the latest version for better performance.",
            isForceUpdate = false,
            onUpdateNow = {},
            onLater = {}
        )
    }
}

@Preview(showBackground = true, name = "Force Update")
@Composable
private fun UpdateAvailableBottomSheetForcePreview() {
    BasePreviewWrapper {
        UpdateAvailableBottomSheet(
            latestVersionLabel = "V1.1",
            releaseNotes = "Improvements and bug fixes have been made to enhance the app " +
                "experience. Update to the latest version for better performance.",
            isForceUpdate = true,
            onUpdateNow = {},
            onLater = {}
        )
    }
}
