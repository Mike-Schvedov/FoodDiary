package com.mikeschvedov.fooddiary.Util

import java.text.SimpleDateFormat
import java.util.*

class TodaysDate {
    companion object{
        // Creating an official format to use
        var DAY_FORMAT = "yyyy/MM/dd"
        // Creating a Date object out of todays Calendar
        private var todaysDate: Date = Calendar.getInstance().time
        // Formatting todays Date object to become a string
        val todaysDateFormatted = SimpleDateFormat(DAY_FORMAT, Locale.getDefault()).format(todaysDate)
    }
}