package com.example.confapp.event.favourite

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class FavouriteEventViewModel : ViewModel() {
    private var isLoading = MutableLiveData<Boolean>()
    val isProgressBarLoading: LiveData<Boolean>
    get() = isLoading

    init{
        isLoading.value = false
    }
}