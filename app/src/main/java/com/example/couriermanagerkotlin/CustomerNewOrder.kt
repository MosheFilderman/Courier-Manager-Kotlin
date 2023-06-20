package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.UUID

class CustomerNewOrder : AppCompatActivity() {
    lateinit var shrd: SharedPreferences

    /* add order variables */
    lateinit var contFirstName: EditText
    lateinit var contLastName: EditText
    lateinit var areaCode: Spinner
    lateinit var contPhoneNumber: EditText
    lateinit var contEmail: EditText
    lateinit var deliveryCity: Spinner
    lateinit var deliveryStreet: AutoCompleteTextView
    lateinit var deliveryBuild: EditText
    lateinit var pickupCity: Spinner
    lateinit var pickupStreet: AutoCompleteTextView
    lateinit var pickupBuild: EditText
    lateinit var packageHeight: EditText
    lateinit var packageWidth: EditText
    lateinit var packageLength: EditText
    lateinit var packageWeight: EditText
    lateinit var errorMassage: TextView
    lateinit var comment: EditText

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
        when (item.itemId) {
            R.id.newOrder -> Toast.makeText(this, "You already at this page!", Toast.LENGTH_SHORT).show()

            R.id.orderList ->{
                startActivity(Intent(this@CustomerNewOrder, CustomerOrderList::class.java))
                finish()

            }

            R.id.logout -> {
                var editor: SharedPreferences.Editor = shrd!!.edit()
                editor.putBoolean("connected", false)
                editor.putString("firstName", "")
                editor.putString("lastName", "")
                editor.putString("email", "")
                editor.putString("eRole", "")
                editor.commit()
                startActivity(Intent(this@CustomerNewOrder, Login::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_new_order)

        /* Contact full name */
        contFirstName = findViewById(R.id.contFirstName)
        contLastName = findViewById(R.id.contLastName)
        /* Contact phone number */
        contPhoneNumber = findViewById(R.id.contPhoneNumber)
        /* Contact email */
        contEmail = findViewById(R.id.contEmail)
        /* Delivery address */
        deliveryStreet = findViewById<AutoCompleteTextView>(R.id.deliveryStreet)
        deliveryBuild = findViewById(R.id.deliveryBuild)
        /* Pickup address */
        pickupStreet = findViewById<AutoCompleteTextView>(R.id.pickupStreet)
        pickupBuild = findViewById(R.id.pickupBuild)
        /* Package measure's */
        packageHeight = findViewById(R.id.packageHeight)
        packageWidth = findViewById(R.id.packageWidth)
        packageLength = findViewById(R.id.packageLength)
        packageWeight = findViewById(R.id.packageWeight)
        errorMassage = findViewById(R.id.errorMassage)
        comment = findViewById(R.id.comment)

        shrd = getSharedPreferences("savefile", Context.MODE_PRIVATE)

        /* Area code spinner */
        areaCode = findViewById(R.id.spinnerAreaCode)
        val areaCodeArrayAdapter = ArrayAdapter(
            this@CustomerNewOrder,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.areaCodes)
        )
        areaCode.adapter = areaCodeArrayAdapter

        areaCode.setSelection(0, false)

        areaCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strAreaCode = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@CustomerNewOrder, "Area code must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }

        /* Delivery city Spinner */
        deliveryCity = findViewById(R.id.deliveryCity)
        val deliveryCityArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.citys)
        )
        deliveryCity.adapter = deliveryCityArrayAdapter

        deliveryCity.setSelection(0, false)

        deliveryCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strDeliveryCity = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@CustomerNewOrder, "Delivery city must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }

        /* Pickup city Spinner */
        pickupCity = findViewById(R.id.pickupCity)
        val pickupCityArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.citys)
        )
        pickupCity.adapter = pickupCityArrayAdapter

        pickupCity.setSelection(0, false)

        pickupCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strPickupCity = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@CustomerNewOrder, "Pickup city must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun newOrder(view: View) {
        if (Validations.checkOrderMeasures(packageHeight,packageWidth,packageLength,packageHeight) && Validations.isEmpty(contFirstName) && Validations.isEmpty(contLastName) &&Validations.isEmpty(contEmail) && Validations.isEmpty(contPhoneNumber)) {
            Toast.makeText(this, "All fields filled correctly.", Toast.LENGTH_SHORT).show()
            createOrder()
            startActivity(Intent(this@CustomerNewOrder, CustomerOrderList::class.java))
            Toast.makeText(this, "New order created.", Toast.LENGTH_SHORT).show()
            errorMassage.visibility = View.VISIBLE
            finish()
        } else {
            Toast.makeText(this, "All order fields must be filled!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createOrder() {
        val url: String = "http://${DButilities.ipv4Address}/courier_project/newOrder.php"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                errorMassage.text = response
            }, Response.ErrorListener { error ->
                errorMassage.visibility = View.VISIBLE
                errorMassage.text = error.toString()
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["orderId"] = UUID.randomUUID().toString()
                    params["email"] = shrd.getString("email", "none").toString()
                    params["contactName"] =
                        contFirstName.text.toString().trim() + " " + contLastName.text.toString()
                            .trim()
                    params["contactPhone"] =
                        "+972" + strAreaCode.substring(1) + contPhoneNumber.text.toString().trim()
                    params["contactEmail"] = contEmail.text.toString().trim()
                    params["eStatus"] = eStatus.NEW.name
                    params["pickUpCity"] = strPickupCity
                    params["pickupStreet"] = pickupStreet.toString().trim()
                    params["pickupBuild"] = pickupBuild.toString().trim()
                    params["deliveryCity"] = strDeliveryCity
                    params["deliveryStreet"] = deliveryStreet.toString().trim()
                    params["deliveryBuild"] = deliveryBuild.toString().trim()
                    params["comment"] = comment.toString().trim()

                    return params
                }

            }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


}