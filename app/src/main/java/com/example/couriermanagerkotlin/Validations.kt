package com.example.couriermanagerkotlin

import android.view.View
import android.widget.EditText

 open class Validations {
     fun checkPhoneLength(view: EditText): Boolean {
        if (view.text.toString()!!.length != 10) {
            view!!.error = "phone should be 10 digits"
            return false
        }
        return true
    }

}