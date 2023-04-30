package com.example.couriermanagerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Registration : AppCompatActivity() {
    lateinit var firstName :EditText
    lateinit var lastName :EditText
    lateinit var email :EditText
    lateinit var phone :EditText
    lateinit var password :EditText
    lateinit var confirmPassword :EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
    }

    fun register(view: View) {
        registerUser()
    }

    private fun registerUser() {
        val url: String =  "http://10.100.102.234/courier_project/insert.php"

         // val emailInput: String = emailEditText.text.toString().trim()
        // val passwordInput: String = passwordEditText.text.toString().trim()

        val stringRequest : StringRequest = object : StringRequest(Method.POST,url,
            Response.Listener { response ->
            Toast.makeText(this,response,Toast.LENGTH_LONG).show()
        },
        Response.ErrorListener { error->
            Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show()
        }){
            override fun getParams(): Map<String,String>{
                val params:MutableMap<String,String> = HashMap()
                params["firstName"]=firstName.text.toString().trim()
                params["lastName"]=lastName.text.toString().trim()
                params["email"] = email.text.toString().trim()
                params["phone"] = phone.text.toString().trim()
                params["password"] = password.text.toString().trim()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}
