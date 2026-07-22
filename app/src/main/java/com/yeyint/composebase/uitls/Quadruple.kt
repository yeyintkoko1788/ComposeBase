package com.yeyint.composebase.uitls

/** Like [kotlin.Pair]/[kotlin.Triple], but for four values. */
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

operator fun <A, B, C, D> Quadruple<A, B, C, D>.component1() = first
operator fun <A, B, C, D> Quadruple<A, B, C, D>.component2() = second
operator fun <A, B, C, D> Quadruple<A, B, C, D>.component3() = third
operator fun <A, B, C, D> Quadruple<A, B, C, D>.component4() = fourth
