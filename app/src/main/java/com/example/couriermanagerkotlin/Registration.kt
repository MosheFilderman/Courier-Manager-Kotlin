package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

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
        if (checkAllFields()) {
            Toast.makeText(this, "All field's filled successfully.", Toast.LENGTH_LONG).show()
            registerUser()
            startActivity(Intent(this@Registration, Login::class.java))
        } else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun registerUser() {
        val url: String = "http://10.0.0.7/courier_project/registration.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                errorMassage.text = response
            },
            Response.ErrorListener { error ->
                errorMassage.text = error.toString()
                startActivity(Intent(this@Registration, Login::class.java))
                finish()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["firstName"] = firstName.text.toString().trim()
                params["lastName"] = lastName.text.toString().trim()
                params["email"] = email.text.toString().trim()
                params["phone"] = "+972" + phone.text.toString().trim().substring(1)
                params["eRole"] = eRole.CUSTOMER.name
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    // function which checks all the text fields
    // are filled or not by the user.
    // when user clicks on the PROCEED button
    // this function is triggered.
    private fun checkAllFields(): Boolean {
        if (firstName!!.length() == 0) {
            firstName!!.error = "This field is required"
            return false
        }
        if (lastName!!.length() == 0) {
            lastName!!.error = "This field is required"
            return false
        }
        if (email!!.length() == 0) {
            email!!.error = "Email is required"
            return false
        }
        if (phone!!.length() != 10) {
            phone!!.error = "phone should be 10 digits"
            return false
        }
        // after all validation return true.
        return true
    }
}


