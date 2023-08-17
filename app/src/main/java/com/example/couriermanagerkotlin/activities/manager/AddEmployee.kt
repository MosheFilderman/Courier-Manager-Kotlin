package com.example.couriermanagerkotlin.activities.manager

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.couriermanagerkotlin.utilities.DBUtilities
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.registerEmployee
import com.example.couriermanagerkotlin.utilities.Validations
import com.example.couriermanagerkotlin.utilities.Validations.Companion.isEmpty

class AddEmployee : AppCompatActivity() {
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var areaCode: Spinner
    lateinit var strAreaCode: String
    lateinit var phoneNumber: EditText
    lateinit var errorMassage: TextView
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_employee)

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phoneNumber = findViewById(R.id.phoneNumber)
        errorMassage = findViewById(R.id.errorMassage)
        radioGroup = findViewById(R.id.radioGroup)

        /* Area code spinner */
        areaCode = findViewById(R.id.spinnerAreaCode)
        val areaCodeArrayAdapter = ArrayAdapter(
            this@AddEmployee,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.areaCodes)
        )
        areaCode.adapter = areaCodeArrayAdapter

        areaCode.setSelection(0, false)

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
                    this@AddEmployee, "Area code must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addEmployee(view: View) {
        errorMassage.visibility = View.GONE
        if (isEmpty(firstName) && isEmpty(lastName) && isEmpty(email) && isEmpty(phoneNumber)) {
            registerEmployee(
                this@AddEmployee,
                firstName.text.toString().trim(),
                lastName.text.toString().trim(),
                email.text.toString().trim(),
                "+972" + strAreaCode.substring(1) + phoneNumber.text.toString().trim(),
                radioButton.text.toString()
            )
        } else {
            errorMassage.visibility = View.VISIBLE
            errorMassage.text = "All field's must be filled!"
        }
    }

    fun checkRadioButton(view: View) {
        radioButton = findViewById(radioGroup.checkedRadioButtonId)
        //  Toast.makeText(this,radioButton.text,Toast.LENGTH_SHORT).show()
    }


}