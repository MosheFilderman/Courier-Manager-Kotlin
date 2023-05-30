package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class Login : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var kuku: TextView
    lateinit var kuku1: TextView
    lateinit var userInputCode: EditText

    var shrd: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        kuku = findViewById(R.id.kuku)
        kuku1 = findViewById(R.id.kuku1)

        shrd = getSharedPreferences("savefile", Context.MODE_PRIVATE)
        //var savedEmail = shrd!!.getString("email", "none")
        //var flag = shrd!!.getBoolean("connected", false)


    }

    fun login(view: View) {
        val url: String = "http://10.100.102.234/courier_project/login.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->

                if (!response.toString().trim().equals("error")) {
                    kuku.text = response.toString()
                    val strRes = response.toString()
                    val jsonArray = JSONArray(strRes)
                    val jsonResponse = jsonArray.getJSONObject(0)
                    val jsonArray_user = jsonResponse.getJSONArray("users")
                    var jsonInner: JSONObject = jsonArray_user.getJSONObject(0)
                    kuku1.text = jsonInner.get("firstName").toString()
                    val temporaryCode = (1000..9999).random().toString()
                    Toast.makeText(this@Login, phone.text.toString(), Toast.LENGTH_SHORT).show()
                    sendSMS("+972" + phone.text.toString().substring(1), temporaryCode)
                    if (verifySMS(temporaryCode)) {

                        var editor: SharedPreferences.Editor = shrd!!.edit()
                        editor.putString("firstName", jsonInner.get("firstName").toString())
                        editor.putString("lastName", jsonInner.get("lastName").toString())
                        editor.putString("email", jsonInner.get("email").toString())
                        editor.putString("eRole", jsonInner.get("eRole").toString())
                        editor.putBoolean("connected", true)
                        editor.commit()
                        when (shrd!!.getString("eRole", "none")) {
                            "0" -> startActivity(Intent(this@Login, Customer::class.java))
                            "1" -> startActivity(Intent(this@Login, Courier::class.java))
                            "2" -> startActivity(Intent(this@Login, Manager::class.java))
                        }
                    }
                } else {
                    //   Toast.makeText(this@Login, response.toString(), Toast.LENGTH_SHORT).show()
                    kuku.text = response.toString()
                }
            },
            Response.ErrorListener { error ->
                kuku.text = error.toString()
                //Toast.makeText(this@Login, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email.text.toString().trim()
                params["phone"] = phone.text.toString().trim()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

//    fun getUsers() {
//        val url: String = "http://10.0.0.7/courier_project/get.php"
//        val stringReq =
//            StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
//                var strResp = response.toString()
//                val jsonArray = JSONArray(strResp)
//                val jsonResponse = jsonArray.getJSONObject(0)
//                val jsonArray_users = jsonResponse.getJSONArray("users")
//
//                var str_user: String = ""
//                for (i in 0 until jsonArray_users.length()) {
//                    var jsonInner: JSONObject = jsonArray_users.getJSONObject(i)
//                    str_user = str_user + "\n" + jsonInner.get("firstName")
//                    str_user = str_user + "\n" + jsonInner.get("lastName")
//                    str_user = str_user + "\n" + jsonInner.get("email")
//                    str_user = str_user + "\n" + jsonInner.get("phone")
//                    str_user = str_user + "\n" + jsonInner.get("password")
//                    str_user = str_user + "\n" + jsonInner.get("eStatus")
//                    str_user = str_user + "\n" + jsonInner.get("eRole")
//                }
//
//                Toast.makeText(this, str_user, Toast.LENGTH_SHORT).show()
//            },
//                Response.ErrorListener { error ->
//                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
//                }
//            )
//        val queue = Volley.newRequestQueue(this)
//        queue.add(stringReq)
//    }

    fun registration(view: View) {
        startActivity(Intent(this, Registration::class.java))
        finish()

    }


    fun sendSMS(phone: String, code: String) {
        // on the below line we are creating a try and catch block
        try {

            // on below line we are initializing sms manager.
            //as after android 10 the getDefault function no longer works
            //so we have to check that if our android version is greater
            //than or equal to android version 6.0 i.e SDK 23
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                //if SDK is greater that or equal to 23 then
                //this is how we will initialize the SmsManager
                smsManager = this.getSystemService(SmsManager::class.java)
            } else {
                //if user's SDK is less than 23 then
                //SmsManager will be initialized like this
                smsManager = SmsManager.getDefault()
            }

            // on below line we are sending text message.
            smsManager.sendTextMessage(phone, null, code, null, null)

            // on below line we are displaying a toast message for message send,
            Toast.makeText(applicationContext, phone.substring(1), Toast.LENGTH_LONG).show()

        } catch (e: Exception) {

            // on catch block we are displaying toast message for error.
            Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun verifySMS(code: String): Boolean {
        var flag = false
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.sms_verification, null)
        userInputCode = dialogLayout.findViewById(R.id.userInputCode)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Verify") { dialogInterface, i ->
            if (userInputCode.text.toString().equals(code)) {

                flag = true
            }
            dialogInterface.dismiss()
        }
        builder.show()
        return flag
    }

}