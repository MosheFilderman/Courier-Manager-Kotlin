package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView

class Customer : AppCompatActivity() {
    /* main activity variables */
    lateinit var navigationBar: BottomNavigationView
    lateinit var selected: Fragment
    /* add order variables */
    lateinit var contactName: EditText
    lateinit var contactPhone: EditText
    lateinit var contactEmail: EditText
    lateinit var pickupAddress: EditText
    lateinit var deliveryAddress: EditText
    lateinit var errorMassage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        supportFragmentManager.beginTransaction().replace(R.id.frameContainer, CustomerOrderListFragment()).commit()

        navigationBar = findViewById(R.id.navigationBar)
        navigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newOrder -> {
                    selected = CustomerNewOrderFragment()
                    true
                }
                R.id.orderList -> {
                    selected = CustomerOrderListFragment()
                    true
                }
                R.id.logout -> {
                    startActivity(Intent(this@Customer, Login::class.java))
                    finish()
                    true
                } else -> false
            }
            supportFragmentManager.beginTransaction().replace(R.id.frameContainer, selected).commit()
            true
        }
    }

    fun newOrderField() {
        /* Catch the new order fragment fields */
        contactName = findViewById(R.id.contactName)
        contactPhone = findViewById(R.id.contactPhone)
        contactEmail = findViewById(R.id.contactEmail)
        pickupAddress = findViewById(R.id.pickupAddress)
        deliveryAddress = findViewById(R.id.deliveryAddress)

        /* Confirm that all the fields are filled */
        if (contactName!!.length() == 0) {
            contactName!!.error = "Full name is required"
        }
        if (contactPhone!!.length() == 0) {
            contactPhone!!.error = "Phone is required"
        }
        if (contactEmail!!.length() == 0) {
            contactEmail!!.error = "Email is required"
        }
        if (pickupAddress!!.length() != 10) {
            pickupAddress!!.error = "Pickup address is required"
        }
        if (deliveryAddress!!.length() == 0) {
            deliveryAddress!!.error = "Delivery address is required"
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
                startActivity(Intent(this@Customer, Login::class.java))
                finish()
            }){
            override fun getParams(): Map<String,String>{
                val params:MutableMap<String,String> = HashMap()
                params["contactName"]=contactName.text.toString().trim()
                params["contactPhone"]=contactPhone.text.toString().trim()
                params["contactEmail"] = contactEmail.text.toString().trim()
                params["pickupAddress"] = pickupAddress.text.toString().trim()
                params["deliveryAddress"] = deliveryAddress.text.toString().trim()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}