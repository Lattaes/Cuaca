package com.lattae.cuaca.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

val local: Locale = Locale.forLanguageTag("en-US")
val fullDateFormatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", local)

fun LocalDate.toDayName(): String = this.dayOfWeek.getDisplayName(TextStyle.FULL, local)


fun generateId(lat: Double, lon: Double): Int {
    val scale = 100
    val longRange = (360 * scale + 1)

    val latFormatted = ((lat + 90.0) * scale).roundToInt()
    val lonFormatted = ((lon + 180.0) * scale).roundToInt()

    val id = (latFormatted * longRange + lonFormatted)

    return id
}