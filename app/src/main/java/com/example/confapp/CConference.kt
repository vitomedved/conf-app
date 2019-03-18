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

}