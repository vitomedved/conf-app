package com.example.confapp.model

import android.util.Log
import com.google.firebase.database.*
import java.io.Serializable
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Semaphore

@IgnoreExtraProperties
class CEvent (
    val about: String = ""
    , val date: String = ""
    , val durationInMinutes: Int = -1
    , val hall: String = ""
    , var id: String = ""
    , val name: String = ""
    , val presenters: List<String> = emptyList()
    //, val time: String = ""
    , val type: String = ""
) : Serializable{
    enum class EventType(val value: String) {
        WORKSHOP("workshop"),
        PRESENTATION("presentation"),
        QA("q&a")
    }


    private fun getDayOfMonthSuffix(n: Int): String {
        if (n in 11..13) {
            return "th"
        }
        when (n % 10) {
            1 -> return "st"
            2 -> return "nd"
            3 -> return "rd"
            else -> return "th"
        }
    }

    @Exclude
    fun getDateString(): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currDate: Calendar = Calendar.getInstance()
        currDate.time = dateFormat.parse(date)

        val dateSuffix = getDayOfMonthSuffix(currDate.get(Calendar.DAY_OF_MONTH))

        val sdf = SimpleDateFormat("MMMM, dd")

        val stringDate: String = sdf.format(currDate.time) + dateSuffix

        return stringDate
    }

    @Exclude
    fun getTimeString(): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currTime: Calendar = Calendar.getInstance()
        currTime.time = dateFormat.parse(date)
        currTime.add(Calendar.MINUTE, durationInMinutes)

        val sdf = SimpleDateFormat("HH:mm")
        val formattedEndTime: String = sdf.format(currTime.time)

        val formattedTime: String = date.substring(11) + " - " + formattedEndTime

        return formattedTime
    }

    @Exclude
    fun getDateTimeCalendar(): Calendar{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currDate: Calendar = Calendar.getInstance()
        currDate.time = dateFormat.parse(date)

        return currDate
    }
}