package com.example.randomuser

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun getFormattedDate(date: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val toFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val longDate = formatter.parse(date)
    return if (longDate != null) toFormat.format(longDate) else ""
}

fun getDateForCalendar(date: String): Calendar {
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val mDate = formatter.parse(date)!!
    val calendar = Calendar.getInstance()
    calendar.time = mDate
    return calendar
}

fun getStringFromPicked(year: Int, month: Int, dayOfMonth: Int): String {
    val mDayOfMonth: String = if (dayOfMonth.toString().length == 1) {
        "0$dayOfMonth"
    } else {
        "$dayOfMonth"
    }
    val dateString = "$year-$month-$mDayOfMonth"
    val fromDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val toFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val date = fromDate.parse(dateString)
    return toFormat.format(date)
}