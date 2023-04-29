package com.example.couriermanagerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }

    fun register(view: View) {}

    fun registerUser() {
        val url: String =  "http://172.16.0.40/courier_project/insert.php"

         // val emailInput: String = emailEditText.text.toString().trim()
        // val passwordInput: String = passwordEditText.text.toString().trim()

        val stringRequest : StringRequest= object:StringRequest(Method.POST,url,
            Response.Listener { response ->
            Toast.makeText(this,response,Toast.LENGTH_LONG).show()
        },
        Response.ErrorListener { error->
            Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show()
        }){
            override fun getParams():Map<String,String>{
                /*val params:mutableMapw<String,String> = HashMap()
                params["userName"]=username
                params["password"]=password */
                val params = HashMap<String,String>()
                params.put("username", "muhamad")
                params.put("email", "muhamad@gmail.com")
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(StringRequest)
    }
}
