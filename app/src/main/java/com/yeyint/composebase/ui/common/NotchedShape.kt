package com.yeyint.composebase.ui.common

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** A rounded-rectangle [Shape] with a small circular notch cut out near the top center. */
class NotchedShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val cornerRadius = with(density) { 16.dp.toPx() }
        val holeRadius = with(density) { 4.dp.toPx() }
        val holeTopPadding = with(density) { 5.dp.toPx() }

        val path = Path().apply {

            // Main rounded rectangle
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, 0f, size.width, size.height),
                    cornerRadius = CornerRadius(cornerRadius)
                )
            )

            // Inner circular notch
            addOval(
                Rect(
                    left = size.width / 2 - holeRadius,
                    top = holeTopPadding,
                    right = size.width / 2 + holeRadius,
                    bottom = holeTopPadding + holeRadius * 2
                )
            )

            fillType = PathFillType.EvenOdd
        }

        return Outline.Generic(path)
    }
}
