package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class CustomerNewOrder : AppCompatActivity() {
    /* add order variables */
    lateinit var contFirstName: EditText
    lateinit var contLastName: EditText
    lateinit var areaCode: Spinner
    lateinit var contPhoneNumber: EditText
    lateinit var contEmail: EditText
    lateinit var deliveryCity: Spinner
    lateinit var deliveryStreet: EditText
    lateinit var deliveryBuild: EditText
    lateinit var pickupCity: Spinner
    lateinit var pickupStreet: EditText
    lateinit var pickupBuild: EditText
    lateinit var packageHeight: EditText
    lateinit var packageWidth: EditText
    lateinit var packageLength: EditText
    lateinit var packageWeight: EditText
    lateinit var errorMassage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_new_order)

        /* Area code spinner */
        areaCode = findViewById(R.id.spinnerAreaCode)
        val areaCodeArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.areaCodes))
        areaCode.adapter = areaCodeArrayAdapter

        areaCode.setSelection(0, false)
    }

    fun newOrderField(view: View) {
        /* Catch the new order fields */

        /* Contact full name */
        contFirstName = findViewById(R.id.contFirstName)
        contLastName = findViewById(R.id.contLastName)
        /* Contact phone number */
        areaCode = findViewById(R.id.spinnerAreaCode)
        contPhoneNumber = findViewById(R.id.contPhoneNumber)
        /* Contact email */
        contEmail = findViewById(R.id.contEmail)
        /* Delivery address */
        deliveryCity = findViewById(R.id.deliveryCity)
        deliveryStreet = findViewById(R.id.deliveryStreet)
        deliveryBuild = findViewById(R.id.deliveryBuild)
        /* Pickup address */
        pickupCity = findViewById(R.id.pickupCity)
        pickupStreet = findViewById(R.id.pickupStreet)
        pickupBuild = findViewById(R.id.pickupBuild)
        /* Package measure's */
        packageHeight = findViewById(R.id.packageHeight)
        packageWidth = findViewById(R.id.packageWidth)
        packageLength = findViewById(R.id.packageLength)
        packageWeight = findViewById(R.id.packageWeight)

        /* Confirm that all the fields are filled */

        /* Contact's full name fields filled */
        if (contFirstName!!.length() == 0) {
            contFirstName!!.error = "Contact's first name is required"
        }
        if (contLastName!!.length() == 0) {
            contLastName!!.error = "Contact's last name is required"
        }
        /* Contact's phone fields filled */
        if (contPhoneNumber!!.length() == 0) {
            contPhoneNumber!!.error = "Contact's last name is required"
        }

        /* only if all the fields are filled, new order will be added to the DB */
        createOrder()
    }

    private fun createOrder() {
        val url: String =  "http://10.100.102.234/courier_project/registration.php"
        val stringRequest : StringRequest = object : StringRequest(
            Method.POST,url,
            Response.Listener { response ->
                errorMassage.text = response
            },
            Response.ErrorListener { error->
                errorMassage.text = error.toString()
                startActivity(Intent(this@CustomerNewOrder, Login::class.java))
                finish()
            }){
            override fun getParams(): Map<String,String>{
                val params:MutableMap<String,String> = HashMap()
//                params["contactName"]=contactName.text.toString().trim()
//                params["contactPhone"]=contactPhone.text.toString().trim()
//                params["contactEmail"] = contactEmail.text.toString().trim()
//                params["pickupAddress"] = pickupAddress.text.toString().trim()
//                params["deliveryAddress"] = deliveryAddress.text.toString().trim()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}