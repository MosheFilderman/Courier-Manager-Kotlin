package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.registerUser
import com.example.couriermanagerkotlin.utilities.Validations
import com.example.couriermanagerkotlin.utilities.Validations.Companion.isEmpty

class Registration : AppCompatActivity() {
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var areaCode: Spinner
    lateinit var strAreaCode: String
    lateinit var phoneNumber: EditText
    lateinit var errorMassage: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phoneNumber = findViewById(R.id.phoneNumber)
        errorMassage = findViewById(R.id.errorMassage)

        /* Area code spinner */
        areaCode = findViewById(R.id.spinnerAreaCode)
        val areaCodeArrayAdapter = ArrayAdapter(
            this@Registration,
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
                    this@Registration, "Area code must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun register(view: View) {
        if (isEmpty(firstName) && isEmpty(lastName) && isEmpty(email) && isEmpty(phoneNumber)) {
            Toast.makeText(this, "All field's filled successfully.", Toast.LENGTH_LONG).show()
            registerUser(
                this@Registration,
                firstName.text.toString().trim(),
                lastName.text.toString().trim(),
                email.text.toString().trim(),
                "+972" + strAreaCode.substring(1) + phoneNumber.text.toString().trim()
            )
            startActivity(Intent(this@Registration, Login::class.java))
            finish()
        } else {
            errorMassage.visibility = View.VISIBLE
            errorMassage.text = "All field's must be filled!"
        }
    }


}




