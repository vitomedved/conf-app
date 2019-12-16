package com.example.confapp.event

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

@SuppressLint("ValidFragment")
class AddPresentersFragmentDialog (private var presenters: Array<String>, private var checkedPresenters: BooleanArray) : DialogFragment() {

    private lateinit var presenterSelectedCallback: PresenterSelected

    fun setPresenterSelectedCallback(callback: PresenterSelected) {
        presenterSelectedCallback = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        builder.apply {
            setTitle("Select presenters")
            setMultiChoiceItems(presenters, checkedPresenters) { dialog: DialogInterface, which: Int, isChecked: Boolean ->
                checkedPresenters[which] = isChecked
            }
            setCancelable(false)
            setPositiveButton("Done") {dialog, which ->
                presenterSelectedCallback.onPositiveClicked(checkedPresenters)
            }
            /*setNegativeButton("Cancel") {dialog, which ->
                presenterSelectedCallback.onNegativeClicked()
            }*/
        }
        return builder.create()
    }

    interface PresenterSelected {
        fun onPositiveClicked(listOfCheckedPresenters: BooleanArray)
        fun onNegativeClicked()
    }
}