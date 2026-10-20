@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeyint.composebase.ui.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.locale.AppLanguageScope
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.boldBodyXLarge
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.text_light

/**
 * Shown app-wide (from `MainActivity`) once the session can no longer be refreshed — the session
 * has already ended by then; this explains it before the shell moves to Login. "Log Out" is the
 * only way out — swipe-down, tap-outside and back all do nothing — since there's no valid session
 * left to fall back to. Same shape as the other single-action notice sheets
 * ([com.yeyint.composebase.ui.screens.home.WarningBottomSheet]), just not dismissible.
 */
@Composable
fun SessionExpiredBottomSheet(onLogOut: () -> Unit) {
    BackHandler {}

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue -> newValue != SheetValue.Hidden }
    )
    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState,
        containerColor = surface_default,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false)
    ) {
        // ModalBottomSheet composes in its own window, which re-provides LocalContext from the
        // Activity — see AppLanguageScope's doc comment.
        AppLanguageScope {
            SessionExpiredBottomSheetContent(onLogOut = onLogOut)
        }
    }
}

@Composable
private fun SessionExpiredBottomSheetContent(onLogOut: () -> Unit) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_warning),
            contentDescription = null,
            modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.session_expired_title),
            style = MaterialTheme.typography.boldBodyXLarge,
            color = text_default,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.session_expired_message),
            style = MaterialTheme.typography.regularBodyMedium,
            color = text_light,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = stringResource(R.string.action_log_out),
            onClick = onLogOut
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionExpiredBottomSheetPreview() {
    BasePreviewWrapper {
        SessionExpiredBottomSheetContent(onLogOut = {})
    }
}
