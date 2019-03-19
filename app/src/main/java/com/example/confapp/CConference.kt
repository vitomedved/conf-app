package com.example.confapp

class CConference (
    val id: Int = -1
    , val name: String = ""
    , val startDate: String = ""
    , val endDate: String = ""
    , val location: String = ""
    , val about: String = ""
    , val events: List<CEvent> = emptyList()
    , val exhibitors: List<CExhibitor> = emptyList()
)
{
    val m_id: Int = id
    val m_name: String = name
    val m_endDate: String = endDate
    val m_location: String = location
    val m_about: String = about
    val m_events: List<CEvent> = events
    val m_exhibitors: List<CExhibitor> = exhibitors
}