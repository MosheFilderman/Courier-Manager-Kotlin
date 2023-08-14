package com.example.couriermanagerkotlin.utilities

import android.widget.EditText
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.measures

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
            if (packageHeight.length() == 0 || Integer.parseInt(packageHeight.text.toString()) > measures.height) {
                packageHeight.error = "Package height must be filled & less then ${measures.height}cm"
                return false
            }
            if (packageWidth.length() == 0 || Integer.parseInt(packageWidth.text.toString()) > measures.width) {
                packageWidth.error = "Package width must be filled & less then ${measures.width}cm"
                return false
            }
            if (packageLength.length() == 0 || Integer.parseInt(packageLength.text.toString()) > measures.length) {
                packageLength.error = "Package length must be filled & less then ${measures.length}cm"
                return false
            }
            if (packageWeight.length() == 0 || Integer.parseInt(packageWeight.text.toString()) > measures.weight) {
                packageWeight.error = "Package weight must be filled & less then ${measures.weight}kg"
                return false
            }
            // after all validation return true.
            return true
        }
    }
}