package com.example.couriermanagerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.UUID

class CustomerNewOrder : AppCompatActivity() {

    lateinit var orderID: String
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

    /* String objects of the spinners */
    lateinit var strAreaCode: String
    lateinit var strDeliveryCity: String
    lateinit var strPickupCity: String

    /* Menu toolbar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.customer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.newOrder -> Toast.makeText(this, "You already at this page!", Toast.LENGTH_SHORT).show()
            R.id.orderList -> startActivity(Intent(this@CustomerNewOrder, CustomerOrderList::class.java))
            R.id.logout -> {
                startActivity(Intent(this@CustomerNewOrder, Login::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_new_order)

        /* Area code spinner */
        areaCode = findViewById(R.id.spinnerAreaCode)
        val areaCodeArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.areaCodes))
        areaCode.adapter = areaCodeArrayAdapter

        areaCode.setSelection(0, false)

        areaCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    strAreaCode = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@CustomerNewOrder, "Area code must bo chosen", Toast.LENGTH_SHORT).show()
            }
        }

        /* Delivery city Spinner */
        deliveryCity = findViewById(R.id.deliveryCity)
        val deliveryCityArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.citys))
        deliveryCity.adapter = deliveryCityArrayAdapter

        deliveryCity.setSelection(0, false)

        deliveryCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    strDeliveryCity = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@CustomerNewOrder, "Delivery city must bo chosen", Toast.LENGTH_SHORT).show()
            }
        }

        /* Delivery city Spinner */
        pickupCity = findViewById(R.id.pickupCity)
        val pickupCityArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.citys))
        pickupCity.adapter = pickupCityArrayAdapter

        pickupCity.setSelection(0, false)

        pickupCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    strPickupCity = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@CustomerNewOrder, "Pickup city must bo chosen", Toast.LENGTH_SHORT).show()
            }
        }
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
        if (contFirstName.length() == 0) {
            contFirstName.error = "Contact's first name is required"
        }
        if (contLastName.length() == 0) {
            contLastName.error = "Contact's last name is required"
        }
        /* Contact's phone fields filled */
        if (contPhoneNumber.length() != 7) {
            contPhoneNumber.error = "Phone number must contain 7 digit's."
        }
        /* Contact's eMail field filled */
        if (contEmail.length() == 0) {
            contEmail.error = "Email address must be filled!"
        }
        /* Package measures field's */
        if (packageHeight.length() == 0 && Integer.parseInt(packageHeight.text.toString()) > 50) {
            packageHeight.error = "Package height must be filled & less then 50cm"
        }
        if (packageWidth.length() == 0 && Integer.parseInt(packageWidth.text.toString()) > 50) {
            packageWidth.error = "Package width must be filled & less then 50cm"
        }
        if (packageLength.length() == 0 && Integer.parseInt(packageLength.text.toString()) > 50) {
            packageLength.error = "Package length must be filled & less then 50cm"
        }
        if (packageWeight.length() == 0 && Integer.parseInt(packageWeight.text.toString()) > 11) {
            packageWeight.error = "Package weight must be filled & less then 10kg"
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
                params["orderId"] = UUID.randomUUID().toString()
                params["contactName"] = contFirstName.text.toString().trim() + " " + contLastName.text.toString().trim()
                params["contactPhone"] = "+972" + (strAreaCode + contPhoneNumber.text.toString().trim()).substring(1)
                params["contactEmail"] = contEmail.text.toString().trim()
//              params["pickupAddress"] = pickupAddress.text.toString().trim()
//              params["deliveryAddress"] = deliveryAddress.text.toString().trim()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}