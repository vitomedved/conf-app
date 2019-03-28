package com.example.confapp.Event

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.confapp.Data.CEvent

class EventViewModel: ViewModel() {
    var event = MutableLiveData<CEvent>()

    fun setEvent(evt: CEvent) {
        event.value = evt
    }
}