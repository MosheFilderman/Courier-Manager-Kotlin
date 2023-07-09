package com.example.couriermanagerkotlin

import android.widget.EditText

open class Validations {
    companion object {
        fun checkPhoneLength(view: EditText): Boolean {
            if (view.text.toString().length != 10) {
                view.error = "phone should be 10 digits"
                return false
            }
            return true
        }

        fun isEmpty(view: EditText): Boolean {
            if (view.length() == 0) {
                view.error = "This field is required"
                return false
            }
            return true
        }

        fun checkOrderMeasures(packageHeight :EditText, packageWidth :EditText, packageLength :EditText, packageWeight : EditText  ): Boolean {
            /* Package measures field's */
            if (packageHeight.length() == 0 || Integer.parseInt(packageHeight.text.toString()) > 50) {
                packageHeight.error = "Package height must be filled & less then 50cm"
                return false
            }
            if (packageWidth.length() == 0 || Integer.parseInt(packageWidth.text.toString()) > 50) {
                packageWidth.error = "Package width must be filled & less then 50cm"
                return false
            }
            if (packageLength.length() == 0 || Integer.parseInt(packageLength.text.toString()) > 50) {
                packageLength.error = "Package length must be filled & less then 50cm"
                return false
            }
            if (packageWeight.length() == 0 || Integer.parseInt(packageWeight.text.toString()) > 11) {
                packageWeight.error = "Package weight must be filled & less then 10kg"
                return false
            }
            // after all validation return true.
            return true
        }
    }
}