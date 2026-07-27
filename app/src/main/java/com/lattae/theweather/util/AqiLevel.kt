package com.lattae.theweather.util

enum class AqiLevel(
    val score: Int,
    val desc: String
) {
    GOOD(1, "Good"),
    FAIR(2, "Fair"),
    MODERATE(3, "Moderate"),
    POOR(4, "Poor"),
    VERY__POOR(5, "Very Poor");

    companion object {
        fun fromScore(score: Int): AqiLevel {
            return entries.find { it.score == score } ?: GOOD
        }
    }
}