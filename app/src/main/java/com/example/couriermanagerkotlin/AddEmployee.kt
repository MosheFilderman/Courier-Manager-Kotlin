package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class AddEmployee : AppCompatActivity() {
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var errorMassage: TextView
    lateinit var radioGroup : RadioGroup
    lateinit var radioButton : RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_employee)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        errorMassage = findViewById(R.id.errorMassage)
        radioGroup = findViewById(R.id.radioGroup)
        radioButton  = findViewById(radioGroup.checkedRadioButtonId)
    }

    fun AddEmployee(view: View) {
        registerEmployee()
    }
    fun checkRadioButton(view: View) {}
    private fun registerEmployee() {
        val url: String = "http://10.100.102.234/courier_project/registration.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                errorMassage.text = response
            },
            Response.ErrorListener { error ->
                errorMassage.text = error.toString()
                startActivity(Intent(this@AddEmployee, Login::class.java))
                finish()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["firstName"] = firstName.text.toString().trim()
                params["lastName"] = lastName.text.toString().trim()
                params["email"] = email.text.toString().trim()
                params["phone"] = "+972" + phone.text.toString().trim().substring(1)
                params["eRole"] = radioButton.text.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}