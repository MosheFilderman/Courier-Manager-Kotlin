package com.example.couriermanagerkotlin

import android.view.View
import android.widget.EditText

 class Validations {


    fun checkFields(view: EditText): Boolean {
        if (view!!.text.toString().length == 0) {
            view!!.error = "This field is required"
            return false
        }
//        if (lastName!!.length() == 0) {
//            lastName!!.error = "This field is required"
//            return false
//        }
//        if (email!!.length() == 0) {
//            email!!.error = "Email is required"
//            return false
//        }
//        if (phone!!.length() != 10) {
//            phone!!.error = "phone should be 10 digits"
//            return false
//        }
        // after all validation return true.
        return true
    }


    protected fun checkPhoneLength(view: EditText): Boolean {
        if (view.text.toString()!!.length != 10) {
            view!!.error = "phone should be 10 digits"
            return false
        }
        return true
    }

    companion object


}