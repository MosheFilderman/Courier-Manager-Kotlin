package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun login(view: View) {}


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

