package com.yeyint.composebase.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.locale.AppLanguageScope
import com.yeyint.composebase.ui.theme.semiboldBodyLarge
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.text_default

/** Camera-or-gallery chooser shared by anywhere a single photo pick is needed — originally
 *  [EditProfileScreen][com.yeyint.composebase.ui.screens.profile.EditProfileScreen]'s avatar
 *  picker, now also [PickupConfirmationScreen][com.yeyint.composebase.ui.screens.orderdetail.PickupConfirmationScreen]'s
 *  package photos. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSourceBottomSheet(
    title: String,
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = surface_default
    ) {
        // ModalBottomSheet composes in its own window, which re-provides LocalContext from the
        // Activity — see AppLanguageScope's doc comment.
        AppLanguageScope {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.semiboldBodyLarge,
                    color = text_default,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                PhotoSourceRow(
                    icon = Icons.Default.CameraAlt,
                    label = stringResource(R.string.photo_source_take_photo),
                    onClick = onTakePhoto
                )
                PhotoSourceRow(
                    icon = Icons.Default.PhotoLibrary,
                    label = stringResource(R.string.photo_source_choose_from_gallery),
                    onClick = onChooseFromGallery
                )
            }
        }
    }
}

@Composable
private fun PhotoSourceRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = text_default, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.regularBodyMedium, color = text_default)
    }
}
