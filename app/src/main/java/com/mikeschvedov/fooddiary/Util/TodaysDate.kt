package com.mikeschvedov.fooddiary.Util

import java.text.SimpleDateFormat
import java.util.*

class TodaysDate {
    companion object{
        // Creating an official format to use
        var DAY_FORMAT = "dd/MM/yyyy"
        // Creating a Date object out of todays Calendar
        var todaysDate: Date = Calendar.getInstance().time
        // Formatting todays Date object to become a string
        val todaysDateFormatted: String = SimpleDateFormat(DAY_FORMAT, Locale.getDefault()).format(todaysDate)

        val firstDateInstalled: String = "s"

        fun daysToAdd(daysToAdd: Int, date: Date): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DATE, daysToAdd)
            return cal.time
        }
    }
}