package com.example.confapp.event

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.confapp.model.CEvent
import com.example.confapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EventViewModel: ViewModel() {

    private var m_currentEvent = MutableLiveData<CEvent>()
    val currentEvent: LiveData<CEvent>
        get() = m_currentEvent

    private var m_evtName = MutableLiveData<String>()
    val evtName: LiveData<String>
        get() = m_evtName

    private var m_evtTypeIcon = MutableLiveData<Int>()
    val evtTypeIcon: LiveData<Int>
        get() = m_evtTypeIcon

    private var m_evtTypeText = MutableLiveData<String>()
    val evtTypeText: LiveData<String>
        get() = m_evtTypeText

    private var m_evtDateText = MutableLiveData<String>()
    val evtDateText: LiveData<String>
        get() = m_evtDateText

    private var m_evtTimeText = MutableLiveData<String>()
    val evtTimeText: LiveData<String>
        get() = m_evtTimeText

    private var m_evtLocationText = MutableLiveData<String>()
    val evtLocationText: LiveData<String>
        get() = m_evtLocationText

    private var m_evtAboutText = MutableLiveData<String>()
    val evtAboutText: LiveData<String>
        get() = m_evtAboutText

    private var m_currentFavoriteIcon = MutableLiveData<Int>()
    val currentFavoriteIcon: LiveData<Int>
        get() = m_currentFavoriteIcon

    private var m_currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    fun updateCurrentEvent(evt: CEvent){
        m_currentEvent.value = evt

        when(evt.type){
            CEvent.EventType.WORKSHOP.value -> {
                m_evtTypeIcon.value = R.drawable.ic_tag_workshop
                m_evtTypeText.value = CEvent.EventType.WORKSHOP.value.capitalize()
            }
            CEvent.EventType.PRESENTATION.value -> {
                m_evtTypeIcon.value = R.drawable.ic_tag_presentation
                m_evtTypeText.value = CEvent.EventType.PRESENTATION.value.capitalize()
            }
            CEvent.EventType.QA.value -> {
                m_evtTypeIcon.value = R.drawable.ic_tag_qa
                m_evtTypeText.value = CEvent.EventType.PRESENTATION.value.capitalize()
            }
        }

        m_evtLocationText.value = "Hall: " + m_currentEvent.value!!.hall

        m_evtTimeText.value = m_currentEvent.value!!.getTimeString()

        m_evtDateText.value = m_currentEvent.value!!.getDateString()

        m_evtName.value = m_currentEvent.value!!.name

        m_evtAboutText.value = m_currentEvent.value!!.about
    }

    fun isUserLoggedIn(): Boolean{
        return m_currentUser != null
    }

    fun toggleFavoriteEvent(){
        // TODO: login activity ne smije otvarati novi main activity, nego ili posloziti login/register u fragmente, ili kod mijenjanja activitija ne dodavati prethodni na backstack
        // Check if current event id is in current user id's favorites

        // If yes: remove it and change icon


        // If not: add it and change icon
        if(m_currentFavoriteIcon.value == R.drawable.ic_hearth_light_pink){
            m_currentFavoriteIcon.value = R.drawable.ic_hearth_lightest_blue
        }else{
            m_currentFavoriteIcon.value = R.drawable.ic_hearth_light_pink
        }
    }

}