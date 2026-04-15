package com.vcc.ticket.utils.extension

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Date and Time Pattern                Result
-----------------------------        ---------------------------------
"yyyy.MM.dd G 'at' HH:mm:ss z"       2001.07.04 AD at 12:08:56 PDT
"EEE, MMM d, ''yy"                   Wed, Jul 4, '01
"h:mm a"                             12:08 PM
"hh 'o''clock' a, zzzz"              12 o'clock PM, Pacific Daylight Time
"K:mm a, z"                          0:08 PM, PDT
"yyyyy.MMMMM.dd GGG hh:mm aaa"       02001.July.04 AD 12:08 PM
"EEE, d MMM yyyy HH:mm:ss Z"         Wed, 4 Jul 2001 12:08:56 -0700
"yyMMddHHmmssZ"                      010704120856-0700
"yyyy-MM-dd'T'HH:mm:ss.SSSZ"         2001-07-04T12:08:56.235-0700
"yyyy-MM-dd'T'HH:mm:ss.SSSXXX"       2001-07-04T12:08:56.235-07:00
"YYYY-'W'ww-u"                       2001-W27-3
 */

fun String.getStringDate(
    initialFormat: String, requiredFormat: String, locale: Locale = Locale.getDefault()
): String {
    return try {
        this.toDate(initialFormat, locale).toString(requiredFormat)
    } catch (e: Exception) {
        this
    }
}

fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date {
    return try {
        val formatter = SimpleDateFormat(format, locale)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(this) ?: Date()
    } catch (ex: NumberFormatException) {
        Date()
    }
}

fun String.convertTimeTest(locale: Locale): Triple<String, String, String> {
    if (canConvertToLong(this)) {
        // Convert timestamp to Date
        val date = Date(this.toLong() * 1000)

        val dayOfMonthFormat = SimpleDateFormat("dd", locale)
        val monthFormat = SimpleDateFormat("MMMM", locale)
        val dayOfWeekFormat = SimpleDateFormat("EEEE", locale)

        val dayOfMonth = dayOfMonthFormat.format(date)
        val month = monthFormat.format(date).uppercase(locale) // Convert month to uppercase
        val dayOfWeekFull = dayOfWeekFormat.format(date)

        val dayOfWeekConvert = when (dayOfWeekFull.lowercase()) {
            "Chủ Nhật".lowercase() -> "CN"
            "Thứ Hai".lowercase() -> "T2"
            "Thứ Ba".lowercase() -> "T3"
            "Thứ Tư".lowercase() -> "T4"
            "Thứ Năm".lowercase() -> "T5"
            "Thứ Sáu".lowercase() -> "T6"
            "Thứ Bảy".lowercase() -> "T7"
            else -> ""
        }

        // Return the desired format
//        return "$dayOfMonth $month $dayOfWeekFull"
        return Triple(dayOfMonth, month, dayOfWeekConvert)

    } else return Triple("", "", "")

}

@SuppressLint("SimpleDateFormat")
fun String.formatDate(
    initDateFormat: String?, endDateFormat: String?
): String? {
    val initDate = SimpleDateFormat(initDateFormat).parse(this)
    val formatter = SimpleDateFormat(endDateFormat)
    return initDate?.let { formatter.format(it) }
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun Long.toTimeStamp(format: String, locale: Locale = Locale.getDefault()): String {
    return try {
        val formatter = SimpleDateFormat(format, locale)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(this * 1000)
    } catch (ex: NumberFormatException) {
        ""
    }
}

fun String.formatDateToHours(): String? {
//    val formatter_from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val formatter_from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val formatter_to = SimpleDateFormat("HH:mm")
    var hour = "00:00"
    try {
        formatter_from.timeZone = TimeZone.getTimeZone("UTC")

        val d = formatter_from.parse(this)
        hour = formatter_to.format(d)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return hour
}

fun calendarFormatFromString(time: String): Calendar {
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(time)
    df.timeZone = TimeZone.getTimeZone("GMT+7")


    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    cal.time = sdf.parse(df.format(date)) // all done

    return cal
}

fun convertTimeStampToDate(time: Long, outPattern: String = "dd-MM-yyyy"): String? {
    val date = Date(time)
    val dateFormat = SimpleDateFormat(outPattern, Locale.getDefault())
    return dateFormat.format(date)
}

fun convertTimeStampToHour(time: Long): String? {
    var hour = ""
    val formatter = SimpleDateFormat("h:mm a");
    hour = formatter.format(Date(time))
    return hour
}

fun String.convertHourToAmPm(
    fromPattern: String = "HH:mm:ss", toPattern: String = "hh:mm a", isLowercase: Boolean = true
): String {
    try {
        if (this.isEmpty()) return ""
        val regex = Regex("^(\\d):")
        val input = regex.replace(this) { matchResult ->
            "0${matchResult.groupValues[1]}:"
        }
        val time =
            LocalTime.parse(input, DateTimeFormatter.ofPattern(fromPattern, Locale.getDefault()))
        val formatter = DateTimeFormatter.ofPattern(toPattern, Locale.getDefault())
        return if (isLowercase) time.format(formatter).lowercase() else time.format(formatter)
            .uppercase()
    } catch (e: Exception) {
        return this
    }
}

fun String.convertTimestampSecondsToAMPM(): String {
    if (canConvertToLong(this)) {
        val seconds = this.toLong()
        val timestampMillis = seconds * 1000
        val date = Date(timestampMillis)
        val dateFormatSymbols = DateFormatSymbols(Locale.forLanguageTag("vi-VN"))
        dateFormatSymbols.amPmStrings = arrayOf("SA", "CH")
        val dateFormat = SimpleDateFormat("hh:mm a - dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
        dateFormat.dateFormatSymbols = dateFormatSymbols
        return dateFormat.format(date)
    } else {
        return this
    }
}

fun convertTimestampSecondsToAMPMOnlyTime(input: String): String {
    if (canConvertToLong(input)) {
        val seconds = input.toLong()
        val timestampMillis = seconds * 1000
        val date = Date(timestampMillis)
        val dateFormatSymbols = DateFormatSymbols(Locale.forLanguageTag("vi-VN"))
        dateFormatSymbols.amPmStrings = arrayOf("SA", "CH")
        val dateFormat = SimpleDateFormat("h:mm a", Locale.forLanguageTag("vi-VN"))
        dateFormat.dateFormatSymbols = dateFormatSymbols
        return dateFormat.format(date)
    } else {
        return input
    }
}

fun convertTimestampSecondsToAMPMDate(input: String): String {
    if (canConvertToLong(input)) {
        val seconds = input.toLong()
        val timestampMillis = seconds * 1000
        val date = Date(timestampMillis)
        val dateFormatSymbols = DateFormatSymbols(Locale.forLanguageTag("vi-VN"))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
        dateFormat.dateFormatSymbols = dateFormatSymbols
        return dateFormat.format(date)
    } else {
        return input
    }
}

fun canConvertToLong(input: String): Boolean {
    return input.toLongOrNull() != null
}

fun String.convertTimeToFormat(format: String): String {
    if (canConvertToLong(this)) {
        val seconds = this.toLong()
        val timestampMillis = seconds * 1000
        val date = Date(timestampMillis)
        val dateFormat = SimpleDateFormat(format, Locale.forLanguageTag("vi-VN"))
        return dateFormat.format(date)
    } else {
        return this
    }
}

fun Long.convertToNotificationTimeStamp(): String {
    val diff = abs(System.currentTimeMillis() - this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        seconds < 60 -> "$seconds giây trước"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days < 7 -> "$days ngày trước"
        days < 30 -> "${days / 7} tuần trước"
        days < 365 -> "${days / 30} tháng trước"
        else -> "${days / 365} năm trước"
    }
}

fun Int.formatDuration(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60

    return when {
        hours > 0 -> "$hours:$minutes:$remainingSeconds"
        minutes > 0 -> "$minutes:$remainingSeconds"
        else -> "$remainingSeconds"
    }
}

fun String.toTimeAgo(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date: Date = dateFormat.parse(this) ?: return "Invalid date"

    val now = System.currentTimeMillis()
    val diffInMillis = now - date.time
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    return when {
        seconds < 60 -> "$seconds giây trước"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        else -> "$days ngày trước"
    }
}

fun String.convertScheduleDateTime(): ZonedDateTime? {
    val dateTimeValue = this.split("-")
    if (dateTimeValue.size < 3) return null
    return try {
        ZonedDateTime.of(
            dateTimeValue[0].toInt(),
            dateTimeValue[1].toInt(),
            dateTimeValue[2].toInt(),
            0,
            0,
            0,
            0,
            ZoneId.systemDefault()
        )
    } catch (e: NumberFormatException) {
        null
    }
}
/**
 * make filter date start toay to 7 days
 */
//private fun makeDataDateFilter() {
//    val calendar: Calendar = Calendar.getInstance()
//    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
//    for (i in 1..7) {
//        if (i != 1) calendar.add(Calendar.DAY_OF_YEAR, 1)//next day add + 1
//        val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
//        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
//        val month: Int = calendar.get(Calendar.MONTH) + 1
//        val date: Date = calendar.time
//        val fullDateFormat: String = dateFormat.format(date)
////            val weekday: String = DateFormatSymbols(Locale.getDefault()).shortWeekdays[dayOfWeek]
////            val weekday: String = DateFormatSymbols(Locale("vi","")).shortWeekdays[dayOfWeek]
//        val locale: Locale = Resources.getSystem().configuration.locales.get(0)
//        val weekday: String = DateFormatSymbols(locale).shortWeekdays[dayOfWeek]
//
//        listDateFilter.add(
//            DateFilter(
//                if (i == 1 && locale.language.equals("vi")) getString(R.string.tvTodayFilter) else weekday,
//                day.toString(),
//                month.toString(),
//                fullDateFormat,
//                i == 1
//            )
//        )
//
//        Logger.d("QuangDV full info:$weekday/$day/$month and full date for call API = $fullDateFormat")
//
//
////            //TODO change day if convert string, don't remove code
////            when (dayOfWeek) {
////                Calendar.SUNDAY -> {}
////                Calendar.MONDAY -> {}
////                Calendar.TUESDAY -> {}
////                Calendar.WEDNESDAY -> {}
////                Calendar.THURSDAY -> {}
////                Calendar.FRIDAY -> {}
////                Calendar.SATURDAY -> {}
////            }
//
//    }
//    date = listDateFilter[0].fullDate.toString()
//}