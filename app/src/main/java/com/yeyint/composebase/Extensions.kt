package com.yeyint.composebase

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.yeyint.composebase.uitls.LOCALE_EN
import com.yeyint.composebase.uitls.LOCALE_MM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.sql.Time
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil
import kotlin.math.roundToInt

fun Intent.navigate(context: Context) {
    context.startActivity(this)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Int.isSuccessResponse(): Boolean {
    return this in 200..299
}

fun View.setVisible(isVisible: Boolean) {
    if (isVisible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}


//this is myanmar
@SuppressLint("SimpleDateFormat")
fun getCurrentPlusOneDayISODateTime(): String {
    // Define the date format in ISO 8601 format with timezone offset
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Yangon") // Set the time zone to Myanmar

    // Get the current date and time in Myanmar time zone
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yangon"))
    calendar.time = Date()

    // Add one day to the current date
    calendar.add(Calendar.DAY_OF_YEAR, 1)

    // Get the date one day later in Myanmar time
    val nextDayDate = calendar.time

    // Format the date in ISO 8601 format with timezone offset
    return dateFormat.format(nextDayDate)
}

@SuppressLint("SimpleDateFormat")
fun getCurrentISODateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val calendar = Calendar.getInstance()
    calendar.time = Date() // Set current date and time

    val nextDayDate = calendar.time
    return dateFormat.format(nextDayDate)
}

//for myanmar time
@SuppressLint("SimpleDateFormat")
fun getCurrentMinusThirtyDaysISODateTime(): String {
    // Define the date format in ISO 8601 format
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Yangon") // Set the time zone to Myanmar

    // Get the current date and time
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yangon"))
    calendar.time = Date()

    // Subtract thirty days from the current date
    calendar.add(Calendar.DAY_OF_YEAR, -30)

    // Get the date 30 days ago in Myanmar time
    val minusThirtyDaysDate = calendar.time

    // Format the date in ISO 8601 format
    return dateFormat.format(minusThirtyDaysDate)
}

@SuppressLint("SimpleDateFormat")
fun getCurrentAndMinusThirtyDaysISODateTime(): Pair<String, String> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val currentDate = Calendar.getInstance().time
    val currentDateString = dateFormat.format(currentDate)

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -30)
    val pastDate = calendar.time
    val pastDateString = dateFormat.format(pastDate)

    return Pair(currentDateString, pastDateString)
}

fun extractFileExtensions(input: String): String {
    // Define the regex to find file extensions
    val regex = "\\.(pdf|docx?|xlsx?|txt?)\\b".toRegex(RegexOption.IGNORE_CASE)

    // Find matches and join results
    return regex.findAll(input)
        .map { it.value.removePrefix(".").uppercase() } // Remove "." and convert to uppercase
        .joinToString(", ")
}

fun isoToUnixTimestamp(isoDate: String): Long {
    if (isoDate.isEmpty()) return System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC time zone
    val date = dateFormat.parse(isoDate)
    return date?.time ?: 0L
}

fun convertUtcToLocalTime(serverDate: String): Time? {
    if (serverDate.isEmpty()) {
        return null
    } else {
        // Parse the UTC date string
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val parsedDate = utcFormat.parse(serverDate) ?: return null

        // Convert parsedDate to local time in milliseconds
        val localCalendar = Calendar.getInstance().apply {
            time = parsedDate
            timeZone = TimeZone.getDefault()
        }

        // Return the local time as java.sql.Time
        return Time(localCalendar.timeInMillis)
    }
}

fun convertDateToLocalTime(serverDate: String?): Time? {
    if (serverDate.isNullOrEmpty()) {
        return null
    } else {
        if (serverDate.isEmpty()) return null

        // Parse the UTC date string
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val parsedDate = utcFormat.parse(serverDate) ?: return null

        // Convert parsedDate to local time in milliseconds
        val localCalendar = Calendar.getInstance().apply {
            time = parsedDate
            timeZone = TimeZone.getDefault()
        }

        // Return the local time as java.sql.Time
        return Time(localCalendar.timeInMillis)
    }
}

fun convertLocalTimeToUtcString(localTime: Time?): String? {
    if (localTime == null) return null

    return try {
        // Get the time in milliseconds from the local Time object
        val localMillis = localTime.time

        // Create a Calendar for local time
        val localCalendar = Calendar.getInstance().apply {
            timeInMillis = localMillis
        }

        // Convert to UTC
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = localCalendar.timeInMillis
        }

        // Define the UTC date format
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        // Format the time in UTC
        utcFormat.format(utcCalendar.time)
    } catch (e: Exception) {
        // Handle any exceptions
        e.printStackTrace()
        null
    }
}

fun formatWithUSLocale(context: Context, resId: Int, vararg args: Any): String {
    return String.format(Locale.US, context.getString(resId), *args)
}

//this is utc
fun convertUtcToLocal(serverDate: String?): String {
    if (serverDate.isNullOrEmpty()) {
        return "12-11-2022"
    } else {
        // Define the server date format
        val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        serverDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Define the desired date format
        val desiredDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        return try {
            // Parse the server date
            val date = serverDateFormat.parse(serverDate)
            // Format the date to the desired format
            desiredDateFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}

fun convertToLocalTime(serverDate: String): String {
    // Define the server date format
    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    serverDateFormat.timeZone = TimeZone.getTimeZone("UTC")

    // Define the desired date format
    val desiredDateFormat = SimpleDateFormat("MMM dd", Locale.US)

    return try {
        // Parse the server date
        val date = serverDateFormat.parse(serverDate)
        // Format the date to the desired format
        desiredDateFormat.format(date!!)
    } catch (e: Exception) {
        ""
    }
}

fun getDateTimeAfterMinutes(minutesToAdd: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, minutesToAdd)  // Add minutes to the current time
    val sdf = SimpleDateFormat("MM/dd/yyyy, h:mm:ss a", Locale.getDefault())
    return sdf.format(calendar.time)
}

fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
    val formattedDate = currentDate.format(formatter)
    return formattedDate
}

fun getCurrentTime(): String {
    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US) // Using Locale.US for consistency
    return currentTime.format(formatter)
}

fun getCurrentDateUTC(): String {
    // Use ZonedDateTime to get the current date and time in UTC
    val currentDateTime = ZonedDateTime.now(ZoneOffset.UTC)
    // Adjust the formatter to include both date and time
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)
    // Format the date-time
    return currentDateTime.format(formatter)
}

fun formatDate(serverDate: String): String {
    if (serverDate.isNullOrEmpty()) {
        return "12-11-2022"
    } else {
        // Define the server date format
        val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        serverDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Define the desired date format
        val desiredDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

        return try {
            // Parse the server date
            val date = serverDateFormat.parse(serverDate)
            // Format the date to the desired format
            desiredDateFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}

fun formatDateMonthDay(serverDate: String): String {
    // Define the server date format
    val serverDateFormat =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

    // Define the desired date format
    val desiredDateFormat = DateTimeFormatter.ofPattern("MMM dd", Locale.US)

    // Parse the input date string to a ZonedDateTime object
    val date = ZonedDateTime.parse(serverDate, serverDateFormat)

    // Format the ZonedDateTime object to the desired output format
    return date.format(desiredDateFormat)
}

fun formatSearchDateMonthDay(serverDate: String): String {
    if (serverDate.isEmpty()) {
        return "Invalid date"
    }
    return try {
        // Parse the input date string as UTC
        val utcDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val parsedDate = utcDateFormat.parse(serverDate)

        // Get the user's local time zone
        val userTimeZone = TimeZone.getDefault()

        // Format the parsed date in the user's local time zone
        val monthDayFormat = SimpleDateFormat("MMM dd", Locale.ENGLISH)
        monthDayFormat.timeZone = userTimeZone
        monthDayFormat.format(parsedDate!!)
    } catch (e: Exception) {
        "Invalid date format"
    }
}


fun getThumbnailFromVideoUrl(videoUrl: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoUrl, HashMap()) // Set the data source
        retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST) // Get frame at time 0ms
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release() // Release the retriever
    }
}

fun formatDuration(durationInSeconds: Double): String {
    val totalSeconds = ceil(durationInSeconds).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
}

fun formatFileSize(bytes: Double): String {
    println("Input size in bytes: $bytes") // Debug log
    if (bytes < 1024) return String.format(Locale.ENGLISH, "%.1f B", bytes) // Less than 1 KB
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")
    var size = bytes
    var unitIndex = 0
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
        println("Size: $size ${units[unitIndex]}") // Debug log
    }
    return String.format(Locale.ENGLISH, "%.1f %s", size, units[unitIndex])
}


fun formatDateMonthDayYear(serverDate: String): String {
    val serverDateFormat =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

    val currentYearFormat = DateTimeFormatter.ofPattern("MMM dd", Locale.US) // Without year
    val previousYearFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US) // With year

    return try {
        val date = ZonedDateTime.parse(serverDate, serverDateFormat)

        val currentYear = ZonedDateTime.now().year

        if (date.year == currentYear) {
            date.format(currentYearFormat) // Format without year
        } else {
            date.format(previousYearFormat) // Format with year
        }
    } catch (e: Exception) {
        "Invalid Date" // Handle parsing errors
    }
}

fun formatBlock(serverDate: String): String {
    val serverDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

    // New format with time, AM/PM, month, day, and year
    val fullFormat = DateTimeFormatter.ofPattern("hh:mm a 'on' MMM dd, yyyy", Locale.US)

    return try {
        val date = ZonedDateTime.parse(serverDate, serverDateFormat)
        date.format(fullFormat) // Format with time and full date
    } catch (e: Exception) {
        "Invalid Date" // Handle parsing errors
    }
}


fun String.toReadableDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(this)

        val outputFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)
        outputFormat.format(date ?: "")
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}

fun formatTime(inputDate: String): String {
    if (inputDate.isEmpty())
        return "-"
    // Define the input and output date formats
    val inputFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)
    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

    // Parse the input date string to a ZonedDateTime object
    val date = ZonedDateTime.parse(inputDate, inputFormatter)

    // Format the ZonedDateTime object to the desired output format
    return date.format(outputFormatter)
}

fun formatTimestampToTime(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    return formatter.format(date)
}

fun getMessageTime(inputDate: String): String {
    if (inputDate.isEmpty()) {
        return ""
    } else {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val parsedDate = utcFormat.parse(inputDate) ?: return ""

        val currentDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val inputCalendar = Calendar.getInstance().apply {
            time = parsedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return if (inputCalendar == currentDate) {
            formatTime(parsedDate) // Placeholder for your time formatting logic
        } else {
            formatDate(parsedDate) // Placeholder for your date formatting logic
        }
    }
}

// Sample format functions in English
fun formatTime(date: Date): String {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
    return timeFormat.format(date)
}

fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
    return dateFormat.format(date)
}

fun Fragment.setAppLocale(locale: Locale) {
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}


private fun getStartOfDayInUserTimeZone(dateMillis: Long): Long {
    val userTimeZone = TimeZone.getDefault()
    val calendar = Calendar.getInstance(userTimeZone)
    calendar.timeInMillis = dateMillis
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}


fun getMessageDate(inputDate: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val parsedDate = dateFormat.parse(inputDate)
    val currentDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    val inputCalendar = Calendar.getInstance().apply {
        time = parsedDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    if (inputCalendar == currentDate) {
        return "Today"
    } else {
        return convertToLocalTime(inputDate)
    }
}


fun formatAmount(amount: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(amount)
}

fun formatAmount(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(amount)
}

fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(amount)
}

fun <T> getChunk(list: List<T>, start: Int, size: Int): List<T> {
    return list.drop(start).take(size)
}

fun getPaginationDisplay(currentPage: Int, itemsPerPage: Int, totalItems: Int): String {
    val startItem = (currentPage - 1) * itemsPerPage + 1
    val endItem = minOf(currentPage * itemsPerPage, totalItems)
    return "$startItem-$endItem of $totalItems"
}

fun Activity.lockToLandscape() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Activity.lockOrientation() {
    val currentOrientation = resources.configuration.orientation
    val rotation = windowManager.defaultDisplay.rotation

    val orientation = when (currentOrientation) {
        Configuration.ORIENTATION_LANDSCAPE -> when (rotation) {
            Surface.ROTATION_0, Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }

        Configuration.ORIENTATION_PORTRAIT -> when (rotation) {
            Surface.ROTATION_0, Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }

        else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    requestedOrientation = orientation
}

fun Activity.unlockOrientation() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

fun View.setUpPaddingBottomForEdgeToEdge() {
    this.setOnApplyWindowInsetsListener { v, insets ->
        v.updatePadding(
            bottom = insets.systemWindowInsetBottom + 102
        )
        insets
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun Long.toShortenedFormat(): String {
    return when {
        this >= 1_000_000 -> {
            val value = this / 1_000_000.0
            if (value % 1.0 == 0.0) String.format(Locale.US, "%.0fM", value) // No decimals
            else String.format(Locale.US, "%.1fM", value) // Keep one decimal if needed
        }

        this >= 1_000 -> {
            val value = this / 1_000.0
            if (value % 1.0 == 0.0) String.format(Locale.US, "%.0fK", value) // No decimals
            else String.format(Locale.US, "%.1fK", value) // Keep one decimal if needed
        }

        else -> this.toString()
    }
}

fun generateThumbnailFromVideo(videoUrl: String): String {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(videoUrl)  // Set video URL

        // Extract the frame at a specific time (e.g., at 1 second)
        val bitmap: Bitmap =
            retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST) ?: return ""

        // Compress Bitmap to PNG or JPEG
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        // Convert Bitmap to Base64 to send as a URL-friendly format (if you want to send it via URL)
        val base64Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

        // Now, base64Image can be used as the image data URL (e.g., you can upload it to a server and get a URL back)
        return "data:image/png;base64,$base64Image"

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return ""
}

fun Array<View>.setVisible(isVisible: Boolean) {

    val visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }

    this.forEach {
        it.visibility = visibility
    }
}

fun ViewGroup.inflater(): LayoutInflater {
    return LayoutInflater.from(this.context)
}

fun inflate(context: Context, parent: ViewGroup, @LayoutRes layoutID: Int) =
    LayoutInflater.from(context).inflate(layoutID, parent, false)

fun ComponentActivity.showShortToast(message: CharSequence) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun ComponentActivity.showShortToast(@StringRes resId: Int) {
    Toast.makeText(applicationContext, resId, Toast.LENGTH_SHORT).show()
}

fun ComponentActivity.showLongToast(message: CharSequence) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
}

fun ComponentActivity.showLongToast(@StringRes resId: Int) {
    Toast.makeText(applicationContext, resId, Toast.LENGTH_LONG).show()
}

fun Fragment.showShortToast(message: CharSequence) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showShortToast(@StringRes resId: Int) {
    Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: CharSequence) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showLongToast(@StringRes resId: Int) {
    Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
}

fun String.removeSpaces() = this.replace("\\s".toRegex(), "")

fun String.isNotEmptyString() = this.removeSpaces() != "" && this.removeSpaces().isNotEmpty()


@SuppressLint("ClickableViewAccessibility")
fun TextView.addPrimaryButtonTextEffext(): TextView {
    this.setOnTouchListener { _, event ->
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                this.setPadding(this.paddingLeft, 0, this.paddingRight, 0)
                this.isSelected = true
                this.performClick()
            }

            MotionEvent.ACTION_CANCEL -> {
                this.setPadding(this.paddingLeft, 0, this.paddingRight, 5)
                this.isSelected = false
            }

            MotionEvent.ACTION_UP -> {
                this.setPadding(this.paddingLeft, 0, this.paddingRight, 5)
                this.isSelected = false
            }
        }
        true
    }
    return this
}

fun getRandom(min: Double, max: Double): Double {
    return Math.random() * (max + 1 - min) + min
}

//fun Array<View>.addClickGuards() {
//    try {
//        this.forEach {
//            ClickGuard.guard(it)
//        }
//    } catch (e: IllegalStateException) {
//        showLogE(e)
//    }
//}
//
//fun Array<TextView>.addClickGuards() {
//    try {
//        this.forEach {
//            ClickGuard.guard(it)
//        }
//    } catch (e: IllegalStateException) {
//        showLogE(e)
//    }
//}

infix fun <T> List<T>.nonintersect(
    other: List<T>
): Set<T> {
    val set = this.toHashSet()
    set.removeAll(other)
    return set
}

inline fun <reified T> T.toTypeDefinedArray(): Array<T> {
    return arrayOf(this)
}

fun rotateBtn(view: View, animation: Int) {
    view.startAnimation(
        AnimationUtils.loadAnimation(
            view.context,
            animation
        )
    )
}

fun convertToDP(context: Context, unit: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        unit.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Int.percentOf(value: Int): Int {
    return (this * 100) / value
}

fun getScreenWidth(context: Context, percentage: Double): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return (displayMetrics.widthPixels * percentage).toInt()
}

fun View.setEnable(enabled: Boolean) {
    this.isEnabled = enabled
}

@NonNull
inline fun <reified T> Gson.fromJson(json: String): T =
    this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!


fun convertToTimeStringWithUnit(durationInSecond: Long): String {
    val numberFormatter = DecimalFormat("###.#")
    val minute = durationInSecond / 60
    val hour = (minute / 60.0)
    return if (hour > 0.0) {
        "${numberFormatter.format(hour)} hours"
    } else
        "$minute minutes"
}

fun calculateTextSize(width: Int): Float {
    // Adjust the multiplier to control the rate of text size decrease.
    // Smaller values will make the text size decrease more rapidly.
    val multiplier = 0.0345f

    // Calculate the new text size based on the width.
    // For example, this code decreases the text size as the width decreases.
    return 12f.coerceAtLeast(width * multiplier)
}

fun calculateTextSizeMM(width: Int): Float {
    return 11f.coerceAtLeast(50f - width / 10f)
}

fun convertToMinutes(durationInSecond: Long): String {
    val minute = durationInSecond / 60
    val seconds = durationInSecond - (60 * minute)
    return "$minute min left"
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
        .toInt()
}


fun convertPxToDp(px: Int): Int {
    return (
            px / (
                    Resources.getSystem()
                        .displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT
                    )
            ).roundToInt()
}

fun spToPx(sp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        context.resources.displayMetrics
    )
        .toInt()
}

fun dpToSp(dp: Float, context: Context): Int {
    return (dpToPx(dp, context) / context.resources.displayMetrics.scaledDensity).toInt()
}

fun TextView.underline(content: String) {
    val content = SpannableString(content)
    content.setSpan(UnderlineSpan(), 0, content.length, 0)
    this.text = content
}

fun getColorWrapper(context: Context, id: Int): Int {
    return ContextCompat.getColor(context, id)
}

fun Int.isOdd(): Boolean {
    return this % 2 != 0
}

fun Int.isEven(): Boolean {
    return this % 2 == 0
}

fun Long.unixToSecond(): String {
    val date = Date(this * 1000)
    val jdf = SimpleDateFormat("ss", Locale.ENGLISH)
    jdf.timeZone = TimeZone.getTimeZone("GMT-4")
    return jdf.format(date)
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT > 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun digitalClockTime(timeInMilliSeconds: Int): String {
    val totalSeconds = timeInMilliSeconds / 1000
    val hours = totalSeconds / (60 * 60)
    val minutes = (totalSeconds - hours * 60 * 60) / 60
    val seconds = totalSeconds - hours * 60 * 60 - minutes * 60
    var timeThumb = ""

    if (hours > 0) {
        timeThumb += if (hours == 1) {
            "$hours hour "
        } else {
            "$hours hours "
        }
    }

    timeThumb += if (minutes > 0) {
        if (minutes == 1) {
            "$minutes minute "
        } else {
            "$minutes minutes "
        }
    } else {// less than 0
        ""
    }

    timeThumb += if (seconds > 0) {
        if (seconds == 1) {
            "$seconds second "
        } else {
            "$seconds seconds "
        }
    } else {// less than 0
        ""
    }
    return timeThumb
}

fun setColorSpannable(text: String, color: Int, startIndex: Int, endIndex: Int): SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        ForegroundColorSpan(color),
        startIndex,
        endIndex,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

fun setBoldSpannable(text: String, startIndex: Int, endIndex: Int): SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        StyleSpan(Typeface.BOLD),
        startIndex,
        endIndex,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

//@SuppressLint("DefaultLocale")
//fun Int.formatSubscriberCount(): String {
//    return when {
//        this < 1_000 -> "$this subscriber${if (this > 1) "s" else ""}"
//        this < 1_000_000 -> String.format("%.1fk subscribers", this / 1_000.0)
//        this < 1_000_000_000 -> String.format("%.1fM subscribers", this / 1_000_000.0)
//        else -> String.format("%.1fB subscribers", this / 1_000_000_000.0)
//    }
//}

@SuppressLint("DefaultLocale")
fun Int.formatToShortForm(): String {
    return when {
        this < 1_000 -> String.format(Locale.US, "%d", this)
        this < 1_000_000 -> {
            val formatted = this / 1_000.0
            val formattedString = String.format(Locale.US, "%.1fk", formatted)
            // Remove ".0" if it exists
            formattedString.replace(".0k", "k")
        }

        this < 1_000_000_000 -> {
            val formatted = this / 1_000_000.0
            val formattedString = String.format(Locale.US, "%.1fm", formatted)
            // Remove ".0" if it exists
            formattedString.replace(".0m", "m")
        }

        else -> {
            val formatted = this / 1_000_000_000.0
            val formattedString = String.format(Locale.US, "%.1fb", formatted)
            // Remove ".0" if it exists
            formattedString.replace(".0b", "b")
        }
    }
}

@SuppressLint("DefaultLocale")
fun Long.formatToShortForm(): String {
    return when {
        this < 1_000 -> String.format(Locale.US, "%d", this)
        this < 1_000_000 -> {
            val formatted = (this / 1_000.0)
            if (formatted.toInt().toDouble() == formatted) {
                String.format(Locale.US, "%dk", formatted.toInt())
            } else {
                String.format(Locale.US, "%.1fk", formatted)
            }

        }

        this < 1_000_000_000 -> {
            val formatted = this / 1_000_000.0
            if (formatted.toInt().toDouble() == formatted) {
                String.format(Locale.US, "%dm", formatted.toInt())
            } else {
                String.format(Locale.US, "%.1fm", formatted)
            }
        }

        else -> {
            val formatted = this / 1_000_000_000.0
            if (formatted.toInt().toDouble() == formatted) {
                String.format(Locale.US, "%db", formatted.toInt())
            } else {
                String.format(Locale.US, "%.1fb", formatted)
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun Int.formatTimesCount(locale: String): String {
    val result = this.formatToShortForm()
    return when (locale) {
        LOCALE_EN -> "$result time${if (this != 1) "s" else ""}"
        LOCALE_MM -> "$result ခါ"
        else -> "$result 度"
    }
}

fun Int.formatItemsCount(): String {
    val result = this.formatToShortForm()
    return "$result item${if (this > 1) "s" else ""}"
}

fun String.setColorSpannable(
    context: Context,
    color: Int,
    startIndex: Int,
    endIndex: Int,
    styleResId: Int? = null
): Spannable {
    val spannableString = SpannableString(this)

    // Apply color span
    spannableString.setSpan(
        ForegroundColorSpan(color),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    // Optionally apply style span (e.g., TextAppearance)
    styleResId?.let {
        spannableString.setSpan(
            TextAppearanceSpan(context, it),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return spannableString
}

fun formatToMinutesSeconds(input: Long): String {
    val normalizedSeconds = (input / 5500).toInt() // Adjust scale factor as needed
    val minutes = normalizedSeconds / 60
    val seconds = normalizedSeconds % 60
    return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds)
}

fun Long.formatTime(): String {
    val dateFormat = SimpleDateFormat("h:mm a", Locale.US)
    val date = Date(this)
    return dateFormat.format(date)
}

fun Long.formatDateTime(): String {
    val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.US)
    val date = Date(this)
    return dateFormat.format(date)
}

private val gson: Gson = Gson()
fun <T> convertToDataType(json: String, type: Class<T>): T {
    return gson.fromJson(json, type)
}

fun <T> convertToListDataType(json: String, clazz: Class<T>): List<T> {
    val gson = Gson()
    val type = TypeToken.getParameterized(List::class.java, clazz).type
    return gson.fromJson(json, type)
}

fun View.slideInView() {
    val parentHeight = (this.parent as View).height
    this.apply {
        visibility = View.VISIBLE
        translationY = -parentHeight.toFloat()  // Start above the view's position
        animate()
            .translationY(0f)  // Animate it back to its original position
            .setDuration(500)
            .start()
    }
}

fun View.slideOutView() {
    val parentHeight = (this.parent as View).height
    this.apply {
        animate()
            .translationY(-parentHeight.toFloat())  // Slide the view up, out of the screen
            .setDuration(500)
            .withEndAction {
                visibility = View.INVISIBLE  // Set the view to GONE after animation completes
            }
            .start()
    }
}

fun String.isOnlyEmoji(): Boolean {
    if (this.isEmpty()) return false

    // Regex to match custom emojis like [cool]
    val customEmojiPattern = """\[(\w+(-\w+)*)\]""".toRegex()

    // Regex to match default system emojis (including newer Unicode emojis)
    val systemEmojiPattern =
        """[\uD83C-\uDBFF\uDC00-\uDFFF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u2300-\u23FF]|[\u2B50]|[\u2B55]|[\u200D]|[\uFE0F]""".toRegex()

    // Remove all custom emojis from the string
    val stringWithoutCustomEmojis = this.replace(customEmojiPattern, "").replace(" ", "")

    // Check if the remaining string contains only system emojis or is empty
    val isSystemEmoji = stringWithoutCustomEmojis.all { char ->
        systemEmojiPattern.matches(char.toString())
    }

    // Check if the original string contains only custom emojis, system emojis, or a mix of both
    return (customEmojiPattern.matches(this) || customEmojiPattern.findAll(this)
        .joinToString("") == this) || isSystemEmoji
}


fun Uri.isImage(context: Context): Boolean {
    val mimeType = context.contentResolver.getType(this)
    return mimeType?.startsWith("image/") == true
}

fun Uri.isVideo(context: Context): Boolean {
    val mimeType = context.contentResolver.getType(this)
    return mimeType?.startsWith("video/") == true
}

fun Long.toFileSize(): String {
    val mb = this / (1024.0 * 1024.0)
    return if (mb >= 1) {
        String.format(Locale.ENGLISH, "%.2f MB", mb)
    } else {
        val kb = this / 1024.0
        String.format(Locale.ENGLISH, "%.2f KB", kb)
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun EditText.showKeyboard() {
    this.requestFocus()
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    this.requestFocus()
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Float.toPlayTime(): String {
    val totalSeconds = ceil(this).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format(Locale.US, "%02d:%02d", minutes, seconds)
        else -> String.format(Locale.US, "00:%02d", seconds)
    }
}

fun createBitmapFromText(text: String, size: Int, backgroundColor: Int, textColor: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    canvas.drawColor(backgroundColor)

    val paint = Paint().apply {
        color = textColor
        textSize = size / 2f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    val xPos = canvas.width / 2
    val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
    canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)

    return bitmap
}

fun EditText.onSearch(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            callback.invoke()
            return@setOnEditorActionListener false
        }
        false
    }
}

fun String.removeSpecialCharacter() : String {
    return this.replace(Regex("[^\\p{L}\\p{M}\\p{N}.]"), "")
}

fun String.toMaskedPhone(): String {
    return if (this.startsWith("+959") && this.length >= 9) {
        val visiblePrefix = this.substring(0, 4) // "+9597"
        val visibleSuffix = this.takeLast(3)     // "789"
        "$visiblePrefix******$visibleSuffix"
    } else {
        this // Return original if it doesn't match expected format
    }
}

fun getTotalRow(totalItems: Int): Int {
    return when (totalItems) {
        in 4..6 -> 2
        in 7..9 -> 3
        10 -> 4
        else -> 1
    }
}

fun getCurrentRowAndPreviousRowItems(position: Int, totalItems: Int): Pair<Int, Int> {
    return when (totalItems) {
        4, 5 -> if (position <= 1) Pair(1, 0) else Pair(2, 2)
        6 -> if (position <= 2) Pair(1, 0) else Pair(2, 3)
        7 -> when {
            position <= 1 -> Pair(1, 0)
            position <= 3 -> Pair(2, 2)
            else -> Pair(3, 4)
        }
        8 -> when {
            position <= 2 -> Pair(1, 0)
            position <= 4 -> Pair(2, 3)
            else -> Pair(3, 5)
        }
        9 -> when {
            position <= 2 -> Pair(1, 0)
            position <= 5 -> Pair(2, 3)
            else -> Pair(3, 6)
        }
        10 -> when {
            position <= 1 -> Pair(1, 0)
            position <= 3 -> Pair(2, 2)
            position <= 6 -> Pair(3, 4)
            else -> Pair(4, 7)
        }
        else -> Pair(1, 0)
    }
}

fun Int.toPriceRange() : String{
    val priceRange = StringBuilder()
    for (i in 1..this) {
        priceRange.append("$")
    }
    return priceRange.toString()
}

