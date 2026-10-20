package com.yeyint.composebase.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.locale.AppLanguageScope
import com.yeyint.composebase.ui.locale.LocalLanguage
import com.yeyint.composebase.ui.locale.SupportedLanguage
import com.yeyint.composebase.ui.theme.BasePreviewWrapper

/**
 * Sample language switcher: a button showing the current language that opens [LanguageDialog].
 * Reads and writes the selection through [LocalLanguage], so it needs no ViewModel and works on any
 * screen — including pre-login ones. Copy it, restyle it, or delete it; the switching itself lives
 * in `ui/locale/AppLanguage.kt`.
 */
@Composable
fun LanguagePicker(modifier: Modifier = Modifier) {
    val language = LocalLanguage.current
    var open by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { open = true }, modifier = modifier) {
        Text(stringResource(SupportedLanguage.fromCode(language.code).labelRes))
    }

    if (open) {
        LanguageDialog(
            selectedCode = language.code,
            onSelect = {
                language.onSelect(it)
                open = false
            },
            onDismiss = { open = false }
        )
    }
}

@Composable
private fun LanguageDialog(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        // A Dialog composes in its own window, which re-derives LocalContext from the Activity and
        // discards the app-wide language scope — without this, the title below would stay in the
        // launch language after a switch. See AppLanguageScope's doc.
        AppLanguageScope {
            Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(
                        text = stringResource(R.string.language_picker_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                    SupportedLanguage.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(option.localCode) }
                                .padding(horizontal = 24.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(option.labelRes))
                            if (option.localCode == selectedCode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LanguagePickerPreview() {
    BasePreviewWrapper {
        LanguagePicker()
    }
}
