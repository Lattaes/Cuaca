package com.lattae.theweather.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val local = Locale.forLanguageTag("en-US")
private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", local)

fun LocalDate.toDayName(): String = this.dayOfWeek.getDisplayName(TextStyle.FULL, local)

fun toFullDate(dateTime: String?, timezone: String?): String {
    val dateString = dateTime?.toLongOrNull() ?: return ""
    val timeZoneOffSet = timezone?.toIntOrNull() ?: return ""

    return Instant.ofEpochSecond(dateString)
        .atZone(ZoneOffset.ofTotalSeconds(timeZoneOffSet))
        .format(fullDateFormatter)
}

fun toTimeString(time: String?, timezone: String?): String {
    val epochSecond = time?.toLongOrNull() ?: return ""
    val timeZoneOffSet = timezone?.toIntOrNull() ?: return ""
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", local)

    return Instant.ofEpochSecond(epochSecond)
        .atZone(ZoneOffset.ofTotalSeconds(timeZoneOffSet))
        .format(formatter)
}