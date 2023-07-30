package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.couriermanagerkotlin.DBUtilities.Companion.registerUser

class Registration : AppCompatActivity() {
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var errorMassage: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        errorMassage = findViewById(R.id.errorMassage)
    }

    fun register(view: View) {
        if (Validations.isEmpty(firstName) && Validations.isEmpty(lastName) && Validations.isEmpty(email) && Validations.checkPhoneLength(phone)) {
            Toast.makeText(this, "All field's filled successfully.", Toast.LENGTH_LONG).show()
            registerUser(this@Registration,firstName.text.toString().trim(),lastName.text.toString().trim(),email.text.toString().trim(),phone.text.toString().trim())
            startActivity(Intent(this@Registration, Login::class.java))
            finish()
        } else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
    }







}




