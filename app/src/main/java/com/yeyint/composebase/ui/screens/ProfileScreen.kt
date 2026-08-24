package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.ui.common.PrimaryButton
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight

/*
* add material-icons first for the eye
* implementation("androidx.compose.material:material-icons-extended-android:1.7.8")
* */

@Composable
fun ProfileScreen(
    popBackStack: () -> Unit,
    popUpToLogin: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Hello User", fontSize = 40.sp)

        PrimaryButton(
            text = "Back",
            onClick = popBackStack,
            modifier = Modifier.padding(8.dp)
        )
        PrimaryButton(
            text = "Log Out",
            onClick = popUpToLogin,
            modifier = Modifier.padding(8.dp)
        )
        var isExpanded by remember { mutableStateOf(false) }
        var isBalanceVisible by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            WalletComponent(
                isExpanded = isExpanded,
                onToggleExpand = { isExpanded = !isExpanded },
                isBalanceVisible = isBalanceVisible,
                onToggleBalance = { isBalanceVisible = !isBalanceVisible }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            ProfileScreen(
                popBackStack = {},
                popUpToLogin = {}
            )
        }
    }
}

@Composable
fun WalletComponent(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isBalanceVisible: Boolean,
    onToggleBalance: () -> Unit
) {
    val density = LocalDensity.current

    val cardOffset1 by animateDpAsState(
        targetValue = if (isExpanded) (-260).dp else (-60).dp,
        animationSpec = spring(
            dampingRatio = if (isExpanded) Spring.DampingRatioMediumBouncy
            else Spring.DampingRatioLowBouncy,
            stiffness = if (isExpanded) 50f else Spring.StiffnessMediumLow
        ), label = "card1"
    )
    val cardOffset2 by animateDpAsState(
        targetValue = if (isExpanded) (-200).dp else (-55).dp,
        animationSpec = spring(
            dampingRatio = if (isExpanded) Spring.DampingRatioMediumBouncy
            else Spring.DampingRatioLowBouncy,
            stiffness = if (isExpanded) 75f else Spring.StiffnessMediumLow
        ), label = "card2"
    )

    val cardOffset3 by animateDpAsState(
        targetValue = if (isExpanded) (-140).dp else (-50).dp,
        animationSpec = spring(
            dampingRatio = if (isExpanded) Spring.DampingRatioMediumBouncy
            else Spring.DampingRatioLowBouncy,
            stiffness = if (isExpanded) 100f else Spring.StiffnessMediumLow
        ), label = "card3"
    )

    val pocketShape = remember(density) {
        GenericShape { size, _ ->
            with(density) {
                val cornerRadius = 48.dp.toPx()
                val pocketCutHeight = 30.dp.toPx()
                val pocketCutWidth = 70.dp.toPx()
                val midX = size.width / 2

                moveTo(cornerRadius, 0f)
                lineTo(midX - pocketCutWidth, 0f)
                quadraticTo(
                    midX - pocketCutWidth + 15.dp.toPx(),
                    0f,
                    midX - pocketCutWidth + 25.dp.toPx(),
                    pocketCutHeight
                )
                lineTo(midX + pocketCutWidth - 25.dp.toPx(), pocketCutHeight)
                quadraticTo(midX + pocketCutWidth - 15.dp.toPx(), 0f, midX + pocketCutWidth, 0f)
                lineTo(size.width - cornerRadius, 0f)
                quadraticTo(size.width, 0f, size.width, cornerRadius)
                lineTo(size.width, size.height - cornerRadius)
                quadraticTo(size.width, size.height, size.width - cornerRadius, size.height)
                lineTo(cornerRadius, size.height)
                quadraticTo(0f, size.height, 0f, size.height - cornerRadius)
                lineTo(0f, cornerRadius)
                quadraticTo(0f, 0f, cornerRadius, 0f)
                close()
            }
        }
    }

    Box(
        modifier = Modifier
            .width(320.dp)
            .height(320.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Wallet Back
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(48.dp))
                .background(Color(0xFFA45E25)) // Dark olive green 0xFF424D36
        )

        // Cards inside the wallet
        WalletCardItem(
            name = "stripe",
            amount = "$1000",
            color = Color(0xFFB1B2FF),
            isVisible = isBalanceVisible,
            offsetY = cardOffset1
        )

        WalletCardItem(
            name = "Wise",
            amount = "$200",
            color = Color(0xFFB9E589),
            isVisible = isBalanceVisible,
            offsetY = cardOffset2
        )

        WalletCardItem(
            name = "PayPal",
            amount = "$400",
            color = Color.White,
            isVisible = isBalanceVisible,
            offsetY = cardOffset3
        )

        // Wallet Front Pocket
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(4.dp)
                .clip(pocketShape)
                .background(Color(0xFF703A0E))
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)

                    val inset = 12.dp.toPx()
                    val stitchingPath = Path().apply {
                        val r = (48.dp.toPx() - inset).coerceAtLeast(0f)
                        val pocketCutHeight = 30.dp.toPx()
                        val pocketCutWidth = 70.dp.toPx()
                        val midX = size.width / 2

                        moveTo(inset + r, inset)
                        lineTo(midX - pocketCutWidth, inset)
                        quadraticTo(
                            midX - pocketCutWidth + 15.dp.toPx(),
                            inset,
                            midX - pocketCutWidth + 25.dp.toPx(),
                            inset + pocketCutHeight
                        )
                        lineTo(midX + pocketCutWidth - 25.dp.toPx(), inset + pocketCutHeight)
                        quadraticTo(
                            midX + pocketCutWidth - 15.dp.toPx(),
                            inset,
                            midX + pocketCutWidth,
                            inset
                        )
                        lineTo(size.width - inset - r, inset)
                        quadraticTo(size.width - inset, inset, size.width - inset, inset + r)
                        lineTo(size.width - inset, size.height - inset - r)
                        quadraticTo(
                            size.width - inset,
                            size.height - inset,
                            size.width - inset - r,
                            size.height - inset
                        )
                        lineTo(inset + r, size.height - inset)
                        quadraticTo(inset, size.height - inset, inset, size.height - inset - r)
                        lineTo(inset, inset + r)
                        quadraticTo(inset, inset, inset + r, inset)
                        close()
                    }

                    drawPath(
                        path = stitchingPath,
                        color = Color.White.copy(alpha = 0.2f),
                        style = Stroke(width = strokeWidth, pathEffect = dashPathEffect)
                    )
                }
                .clickable { onToggleExpand() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBalanceVisible) "1600" else "****",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total Balance",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onToggleBalance,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Balance",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WalletCardItem(
    name: String,
    amount: String,
    color: Color,
    isVisible: Boolean,
    offsetY: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .width(280.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .padding(24.dp)
    ) {
        // Logo (Simplified)
        Text(
            text = name,
            color = if (color == Color.White) Color(0xFF003087) else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
        )

        // Individual Card Balance
        Text(
            text = if (isVisible) amount else "****",
            modifier = Modifier.align(Alignment.TopEnd),
            color = if (color == Color.White) Color.Gray.copy(alpha = 0.6f) else Color.White.copy(
                alpha = 0.7f
            ),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WalletPreview() {
    MaterialTheme { //Use your own theme here

        var isExpanded by remember { mutableStateOf(false) }
        var isBalanceVisible by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            WalletComponent(
                isExpanded = isExpanded,
                onToggleExpand = { isExpanded = !isExpanded },
                isBalanceVisible = isBalanceVisible,
                onToggleBalance = { isBalanceVisible = !isBalanceVisible }
            )
        }
    }
}
