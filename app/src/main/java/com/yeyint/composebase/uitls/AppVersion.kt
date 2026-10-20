package com.yeyint.composebase.uitls

/**
 * Dot-separated version comparison ("1.0", "1.2.3", "2") — pads missing segments with 0 so
 * differently-sized version strings still compare correctly (a `versionName` like "1.0" is a
 * two-part string, so a strict major.minor.patch parser would reject it).
 */
data class AppVersion(val parts: List<Int>) : Comparable<AppVersion> {

    override fun compareTo(other: AppVersion): Int {
        val size = maxOf(parts.size, other.parts.size)
        for (i in 0 until size) {
            val a = parts.getOrElse(i) { 0 }
            val b = other.parts.getOrElse(i) { 0 }
            if (a != b) return a.compareTo(b)
        }
        return 0
    }

    override fun toString(): String = parts.joinToString(".")

    companion object {
        fun parse(raw: String): AppVersion? {
            val segments = raw.trim().trimStart('v', 'V').split(".").map { it.toIntOrNull() }
            if (segments.isEmpty() || segments.any { it == null }) return null
            return AppVersion(segments.filterNotNull())
        }
    }
}
