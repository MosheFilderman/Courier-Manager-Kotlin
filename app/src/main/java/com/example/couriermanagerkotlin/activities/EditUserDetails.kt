package com.example.couriermanagerkotlin.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.utilities.DBUtilities
import com.example.couriermanagerkotlin.utilities.Validations

class EditUserDetails : AppCompatActivity() {

    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: TextView
    lateinit var errorMessage: TextView
    lateinit var areaCode: Spinner
    lateinit var strAreaCode: String
    lateinit var phoneNumber: EditText
    lateinit var shrd: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_details)

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phoneNumber = findViewById(R.id.phoneNumber)
        errorMessage = findViewById(R.id.errorMessage)

        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName.hint = shrd.getString("firstName", "None")
        lastName.hint = shrd.getString("lastName", "None")
        email.text = shrd.getString("email", "None").toString()
        val currentPhone = shrd.getString("phone", "").toString()
        phoneNumber.hint = currentPhone.substring(3)

        /* Area code spinner */
        areaCode = findViewById(R.id.spinnerAreaCode)
        val areaCodeArrayAdapter = ArrayAdapter(
            this@EditUserDetails,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.areaCodes)
        )
        areaCode.adapter = areaCodeArrayAdapter

        areaCode.setSelection(0, false)

        when (currentPhone.substring(0, 3)) {
            "050" -> areaCode.setSelection(1)
            "051" -> areaCode.setSelection(2)
            "052" -> areaCode.setSelection(3)
            "053" -> areaCode.setSelection(4)
            "054" -> areaCode.setSelection(5)
            "055" -> areaCode.setSelection(6)
            "058" -> areaCode.setSelection(7)
            else -> areaCode.setSelection(0)
        }

        areaCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strAreaCode = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@EditUserDetails, "Area code must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun updateUserDetails(view: View) {
        if (Validations.isEmpty(firstName) && Validations.isEmpty(lastName) && Validations.isEmpty(
                phoneNumber
            )
        ) {
            Toast.makeText(this, "All field's filled successfully.", Toast.LENGTH_LONG).show()

            val editor: SharedPreferences.Editor = shrd.edit()
            editor.putString("firstName", firstName.text.toString())
            editor.putString("lastName", lastName.text.toString())
            editor.apply()
            
            DBUtilities.updateUserDetail(
                this@EditUserDetails,
                firstName.text.toString().trim(),
                lastName.text.toString().trim(),
                shrd.getString("email", "None").toString(),
                strAreaCode + phoneNumber.text.toString().trim(),
                errorMessage
            )
        } else {
            errorMessage.visibility = View.VISIBLE
            errorMessage.text = "All field's must be filled!"
        }
    }
}