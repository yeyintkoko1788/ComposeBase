package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.ui.theme.Purple80
import com.yeyint.composebase.ui.theme.mediumBodyLarge
import com.yeyint.composebase.ui.theme.text_dark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.mediumBodyLarge,
                color = text_dark
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple80
        ),
        modifier = Modifier.shadow(1.dp)
    )
}