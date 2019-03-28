package com.example.confapp.model

import android.util.Log
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.*

@IgnoreExtraProperties
class CEvent (
    val about: String = ""
    , val date: String = ""
    , val durationInMinutes: Int = -1
    , val hall: String = ""
    , val id: Int = -1
    , val name: String = ""
    , val presenters: List<Int> = emptyList()
    //, val time: String = ""
    , val type: String = ""
){
    fun getCalendarDate(): Calendar?{

        var ret: Calendar? = null

        if(date != "")
        {
            Log.d("CEvent", "Current event string is: $date")
            val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            ret = Calendar.getInstance()
            ret.time = dateFormat.parse(date)
            Log.d("CEvent", "After parsing day of year is: ${ret.get(Calendar.DAY_OF_YEAR)}")
        }
        return ret
    }

}