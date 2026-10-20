@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeyint.composebase.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.locale.AppLanguageScope
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.semiboldBodyLarge
import com.yeyint.composebase.ui.theme.stroke_lighter
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.text_light

private const val SheetMaxHeightFraction = 0.8f

/**
 * A pick-one list in a bottom sheet with a search field on top, for dropdown fields whose option
 * list can be long. A sheet rather than a search box inside a [androidx.compose.material3.DropdownMenu]:
 * that menu is a popup, which the keyboard simply covers, while a sheet lifts above it
 * ([imePadding]) and keeps the list scrollable. Matching is a case-insensitive "contains" on the
 * option's label. The search field is opt-in via [searchable] — a short list has nothing worth
 * searching.
 */
@Composable
fun <T> SearchableOptionSheet(
    title: String,
    options: List<T>,
    optionLabel: (T) -> String,
    selectedLabel: String,
    onSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    searchable: Boolean = false
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
            var query by remember { mutableStateOf("") }
            val visible = remember(options, query, searchable) {
                val needle = query.trim()
                if (!searchable || needle.isEmpty()) options
                else options.filter { optionLabel(it).contains(needle, ignoreCase = true) }
            }
            // A share of the screen rather than a fixed dp height, so a long list uses what the
            // device has (and a tall phone isn't stuck with a small list). The cap is on the whole
            // column — title and search included — and the list takes whatever is left, so the
            // sheet still wraps its content when there are only a few options.
            val maxSheetHeight = LocalConfiguration.current.screenHeightDp.dp * SheetMaxHeightFraction
            Column(
                modifier = Modifier
                    .imePadding()
                    .heightIn(max = maxSheetHeight)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.semiboldBodyLarge,
                    color = text_default
                )
                if (searchable) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DefaultEditText(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = stringResource(R.string.option_search_placeholder),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.cd_search),
                                tint = text_light
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (visible.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.option_search_no_results),
                            style = MaterialTheme.typography.regularBodyMedium,
                            color = text_light,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(visible) { option ->
                            val label = optionLabel(option)
                            FilterOptionRow(
                                label = label,
                                selected = label == selectedLabel,
                                onClick = { onSelected(option) }
                            )
                            if (option !== visible.last()) HorizontalDivider(color = stroke_lighter)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchableOptionSheetPreview() {
    BasePreviewWrapper {
        SearchableOptionSheet(
            title = "Item Type",
            options = listOf("Food", "Document", "Cake & Bakery", "Clothes", "Electronics", "Furniture", "Flowers"),
            optionLabel = { it },
            selectedLabel = "Document",
            onSelected = {},
            onDismiss = {},
            searchable = true
        )
    }
}
