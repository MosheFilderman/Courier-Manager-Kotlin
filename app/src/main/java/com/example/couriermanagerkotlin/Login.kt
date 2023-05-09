package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Login : AppCompatActivity() {

    lateinit var password :EditText
    lateinit var email:EditText
    lateinit var kuku: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        password = findViewById(R.id.password)
        email = findViewById(R.id.email)
        kuku = findViewById(R.id.kuku)
    }

    fun login(view: View) {

        val url: String =  "http:/172.16.3.243/courier_project/check.php"
        val stringRequest : StringRequest = object : StringRequest(Method.POST,url,
            Response.Listener { response ->
                //errorMassage.text = response
                if(response.toString().trim().equals("success")) {
                    //intent
                    Toast.makeText(this@Login,"success",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Login,response.toString(),Toast.LENGTH_SHORT).show()
                }

            },
            Response.ErrorListener { error->
               // errorMassage.text = error.toString()
                Toast.makeText(this@Login,error.toString(),Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): Map<String,String>{
                val params:MutableMap<String,String> = HashMap()
                params["email"] = email.text.toString().trim()
                params["password"] = password.text.toString().trim()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)

    }

    fun getUsers() {
        val url: String = "http://10.0.0.7/courier_project/get.php"
        val stringReq =
            StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                var strResp = response.toString()
                val jsonArray = JSONArray(strResp)
                val jsonResponse = jsonArray.getJSONObject(0)
                val jsonArray_users = jsonResponse.getJSONArray("users")

                var str_user: String = ""
                for (i in 0 until jsonArray_users.length()) {
                    var jsonInner: JSONObject = jsonArray_users.getJSONObject(i)
                    str_user = str_user + "\n" + jsonInner.get("firstName")
                    str_user = str_user + "\n" + jsonInner.get("lastName")
                    str_user = str_user + "\n" + jsonInner.get("email")
                    str_user = str_user + "\n" + jsonInner.get("phone")
                    str_user = str_user + "\n" + jsonInner.get("password")
                    str_user = str_user + "\n" + jsonInner.get("eStatus")
                    str_user = str_user + "\n" + jsonInner.get("eRole")
                }

                Toast.makeText(this, str_user, Toast.LENGTH_SHORT).show()
            },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                }
            )
        val queue = Volley.newRequestQueue(this)
        queue.add(stringReq)
    }

    fun registration(view: View) {
        startActivity(Intent(this,Registration::class.java))
        finish()

    }

}

