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
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class Login : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var userInputCode: EditText
    lateinit var shrd: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)


        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor = shrd!!.edit()
        editor.clear()

        if (shrd.getBoolean("connected", false)) {
            when (shrd.getString("eRole", "none")) {
                "CUSTOMER" -> {
                    startActivity(Intent(this@Login, CustomerOrderList::class.java))
                    finish()
                }

                "COURIER" -> {
                    startActivity(Intent(this@Login, CourierListView::class.java))
                    finish()
                }

                "MANAGER" -> {
                    startActivity(Intent(this@Login, Manager::class.java))
                    finish()
                }
            }
        }
    }

    fun registration(view: View) {
        startActivity(Intent(this, Registration::class.java))
        finish()
    }

    fun sendSMS(phone: String, code: String) {
        // on the below line we are creating a try and catch block
        try {
            // on below line we are initializing sms manager.
            // as after android 10 the getDefault function no longer works
            // so we have to check that if our android version is greater
            // than or equal to android version 6.0 i.e SDK 23
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                // if SDK is greater that or equal to 23 then
                // this is how we will initialize the SmsManager
                smsManager = this.getSystemService(SmsManager::class.java)
            } else {
                // if user's SDK is less than 23 then
                // SmsManager will be initialized like this
                smsManager = SmsManager.getDefault()
            }
            // on below line we are sending text message.
            smsManager.sendTextMessage(phone, null, code, null, null)
            // on below line we are displaying a toast message for message send,
            // Toast.makeText(applicationContext, phone.substring(1), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // on catch block we are displaying toast message for error.
            Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun verifySMS(code: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.sms_verification, null)

        userInputCode = dialogLayout.findViewById(R.id.userInputCode)

        builder.setView(dialogLayout)

        builder.setPositiveButton("Verify") { dialogInterface, i ->
            if (userInputCode.text.toString().compareTo(code) == 0) {
                when (shrd.getString("eRole", "none")) {
                    "CUSTOMER" -> {
                        startActivity(Intent(this@Login, CustomerOrderList::class.java))
                        finish()
                    }

                    "COURIER" -> {
                        startActivity(Intent(this@Login, CourierListView::class.java))
                        finish()
                    }

                    "MANAGER" -> {
                        startActivity(Intent(this@Login, Manager::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(this@Login, "Invalid Code", Toast.LENGTH_SHORT).show()
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun login(view: View) {

        if (Validations.isEmpty(email) && Validations.isEmpty(phone)) {
            DButilities.login(
                this@Login,
                email.text.toString().trim(),
                phone.text.toString().trim(),
                shrd
            )
            if (shrd.getBoolean("connected", false)){
                val temporaryCode = (1000..9999).random().toString()
                sendSMS(shrd.getString("phone","none").toString(), temporaryCode)
                verifySMS(temporaryCode)
            }



        }

    }


}