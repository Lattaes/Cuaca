package com.lattae.theweather.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val local = Locale.forLanguageTag("en-US")
private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", local)

fun LocalDate.toFullDate(): String = this.format(fullDateFormatter)

fun LocalDate.toDayName(): String = this.dayOfWeek.getDisplayName(TextStyle.FULL, local)