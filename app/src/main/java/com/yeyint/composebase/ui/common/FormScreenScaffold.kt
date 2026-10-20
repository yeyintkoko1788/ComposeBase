package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.ui.theme.surface_bg
import com.yeyint.composebase.ui.theme.surface_default

/**
 * Fixed [ScreenHeader] + scrollable body + fixed bottom action bar — the shape shared by any
 * screen that's a form collecting input before a primary/secondary action pair (originally
 * [PickupConfirmationScreen][com.yeyint.composebase.ui.screens.orderdetail.PickupConfirmationScreen],
 * also meant for the upcoming payment-collection screen).
 *
 * Two nested layers do two different jobs, deliberately kept separate:
 * - The OUTER scroll container gets `weight(1f)` with no background of its own — this is what
 *   guarantees [footer] can never get squeezed off-screen by tall content (the same pattern
 *   [EditProfileScreen][com.yeyint.composebase.ui.screens.profile.EditProfileScreen] uses).
 *   Compose measures unweighted siblings independently of each other, so an unweighted scrollable
 *   body only clamps to its own constraint, not to "whatever's left after the footer" — weighting
 *   it is what actually reserves the footer's space first.
 * - The INNER content [Column] carries the `surface_default` background and hugs only its own
 *   content height (no weight, no `fillMaxSize`) — so the "card" only extends as far as the form
 *   actually needs. Any leftover space in the outer weighted container (when content is shorter
 *   than the screen) stays transparent, letting the screen's own `surface_bg` show through as a
 *   gap above the footer, rather than one continuous `surface_default` panel from header to footer.
 */
@Composable
fun FormScreenScaffold(
    title: String,
    onBack: () -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface_bg)
    ) {
        ScreenHeader(title = title, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .background(surface_default)
                    .padding(16.dp),
                content = content
            )
        }
        Surface(shadowElevation = 12.dp, color = surface_default) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                content = footer
            )
        }
    }
}

/**
 * Full-size invisible layer that swallows every touch beneath it. Drawn over a form once its
 * submit has succeeded and the screen is only waiting out a confirmation toast before leaving —
 * disabling the submit button alone leaves the fields, pickers and back arrow live for that gap.
 */
@Composable
fun TouchBlocker() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            }
    )
}
