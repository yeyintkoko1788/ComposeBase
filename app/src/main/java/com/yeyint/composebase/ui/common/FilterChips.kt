package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.neutral_surface
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.stroke_lighter
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.semiboldBodySmall
import com.yeyint.composebase.ui.theme.surface_brand

/** Single pill from the All / Unread pair used on Inbox. */
@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        style = MaterialTheme.typography.semiboldBodySmall,
        color = if (isSelected) surface_brand else text_default,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) neutral_surface else surface_default)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) neutral_surface else stroke_lighter,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun FilterChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { index, label ->
            FilterChip(
                label = label,
                isSelected = index == selectedIndex,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Preview
@Composable
private fun FilterChipRowPreview() {
    BasePreviewWrapper {
        FilterChipRow(options = listOf("All", "Unread"), selectedIndex = 0, onSelect = {})
    }
}
