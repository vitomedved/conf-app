package com.example.confapp.model

// TODO: if database throws error (cannot convert to type) -> try adding @IgnoreExtraProperties

//@IgnoreExtraProperties
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
    val m_startDate = startDate
    val m_endDate: String = endDate
    val m_location: String = location
    val m_about: String = about
    val m_events: List<Int> = eventIds
    val m_exhibitors: List<Int> = exhibitors
}