package com.example.confapp.event

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.confapp.model.CEvent

class EventViewModel: ViewModel() {
    var event = MutableLiveData<CEvent>()

    fun setEvent(evt: CEvent) {
        event.value = evt
    }
}