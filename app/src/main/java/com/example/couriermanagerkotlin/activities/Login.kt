package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.couriermanagerkotlin.activities.courier.CourierShipmentList
import com.example.couriermanagerkotlin.activities.customer.CustomerOrderList
import com.example.couriermanagerkotlin.activities.manager.Manager
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.login
import com.example.couriermanagerkotlin.utilities.Validations

class Login : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var userInputCode: EditText
    lateinit var shrd: SharedPreferences
    lateinit var message: TextView
    val PERMISSIONS_REQUEST_ACCESS_SEND_SMS = 9002


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        message = findViewById(R.id.message)


        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)

        /**
         * Redirect the logged in user to the proper activity.
         */
        if (shrd.getBoolean("connected", false)) {
            when (shrd.getString("eRole", "none")) {
                "CUSTOMER" -> {
                    startActivity(Intent(this@Login, CustomerOrderList::class.java))
                    finish()
                }

                "COURIER" -> {
                    startActivity(Intent(this@Login, CourierShipmentList::class.java))
                    finish()
                }

                "MANAGER" -> {
                    startActivity(Intent(this@Login, Manager::class.java))
                    finish()
                }
            }
        }
    }

    /**
     * open the registration activity
     */
    fun registration(view: View) {
        startActivity(Intent(this, Registration::class.java))
    }

    /**
     * Check user's SDK and send the SMS in the appropriate way
     */
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

    /**
     * Confirm that entered code is correct, if correct open the appropriate activity by the user's role
     */
    fun verifySMS(code: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.sms_verification, null)

        userInputCode = dialogLayout.findViewById(R.id.userInputCode)

        builder.setView(dialogLayout)

        builder.setPositiveButton("Verify Code") { dialogInterface, i ->
            if (userInputCode.text.toString().compareTo(code) == 0) {
                when (shrd.getString("eRole", "none")) {
                    "CUSTOMER" -> {
                        startActivity(Intent(this@Login, CustomerOrderList::class.java))
                        finish()
                    }

                    "COURIER" -> {
                        startActivity(Intent(this@Login, CourierShipmentList::class.java))
                        finish()
                    }

                    "MANAGER" -> {
                        startActivity(Intent(this@Login, Manager::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(this@Login, "Invalid Code", Toast.LENGTH_LONG).show()
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }

    /**
     * Check the user gave permission to send SMS, if not ask it,
     * confirm that user filled required fields,
     * continue the code confirmation flow.
     */
    fun login(view: View) {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS), PERMISSIONS_REQUEST_ACCESS_SEND_SMS
            )
        }
        if (Validations.isEmpty(email) && Validations.isCorrectLength(phone)) {
            login(
                this@Login,
                email.text.toString().trim(),
                phone.text.toString().trim(),
                shrd
            )
            if (shrd.getBoolean("connected", false)) {
                val temporaryCode = (1000..9999).random().toString()
                sendSMS(shrd.getString("phone", "none").toString(), temporaryCode)
                verifySMS(temporaryCode)
            }
        }
    }
}


