package com.example.confapp

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class EventViewModel: ViewModel() {
    var event = MutableLiveData<CEvent>()

    fun setEvent(evt: CEvent) {
        event.value = evt
    }
}