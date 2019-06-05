package com.example.confapp.model

class CUser(
    val uid: String = "",
    val avatar_url: String = "",
    val mail: String = "",
    val name: String = "",
    val password: String = "",
    val subscribedEvents: MutableList<String> = mutableListOf(),
    val level: Int = 9,
    val date_joined: String = ""
) {




}