package com.yeyint.composebase.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.neutral_border_disable
import com.yeyint.composebase.ui.theme.neutral_text
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.semiboldBodyXLarge

@Composable
fun LanguageBottomSheet(
    onClickLanguage: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Change language",
            style = MaterialTheme.typography.semiboldBodyXLarge,
            color = neutral_text
        )
        Spacer(modifier = Modifier.height(16.dp))
        LanguageItem(
            language = "English",
            isSelected = true,
            onItemSelected = { onClickLanguage("en") },
            flagResId = R.drawable.ic_english
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = neutral_border_disable,
        )
        LanguageItem(
            language = "မြန်မာ",
            isSelected = false,
            onItemSelected = { onClickLanguage("my") },
            flagResId = R.drawable.ic_myanmar
        )
    }
}

@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    language: String,
    isSelected: Boolean,
    onItemSelected: () -> Unit,
    flagResId: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemSelected() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = language,
                style = MaterialTheme.typography.regularBodyMedium,
                modifier = Modifier.padding(start = 16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else neutral_text
            )
        }
        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageBottomSheetPreview() {
    LanguageBottomSheet()
}
