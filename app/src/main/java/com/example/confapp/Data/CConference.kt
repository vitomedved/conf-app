package com.example.confapp.Data

class CConference (
    val id: Int = -1
    , val name: String = ""
    , val startDate: String = ""
    , val endDate: String = ""
    , val location: String = ""
    , val about: String = ""
    , val eventIds: List<Int> = emptyList()
    , val exhibitors: List<Int> = emptyList()
)
{
    val m_id: Int = id
    val m_name: String = name
    val m_endDate: String = endDate
    val m_location: String = location
    val m_about: String = about
    val m_events: List<Int> = eventIds
    val m_exhibitors: List<Int> = exhibitors
}