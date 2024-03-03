package com.example.couriermanagerkotlin.activities.customer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.eNums.eStatus
import com.example.couriermanagerkotlin.objects.Order
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.createOrder
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getMeasures
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getStreetByCity
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.streets
import com.example.couriermanagerkotlin.utilities.Validations.Companion.checkOrderMeasures
import com.example.couriermanagerkotlin.utilities.Validations.Companion.isEmpty
import com.example.couriermanagerkotlin.utilities.Validations.Companion.isPhoneNumberNoAreaCode
import com.example.couriermanagerkotlin.utilities.Validations.Companion.validateStreetName
import java.util.UUID

class CustomerNewOrder : AppCompatActivity() {
    lateinit var shrd: SharedPreferences

    /* add order variables */
    lateinit var contFirstName: EditText
    lateinit var contLastName: EditText
    lateinit var areaCode: Spinner
    lateinit var contPhoneNumber: EditText
    lateinit var contEmail: EditText

    lateinit var pickupCity: Spinner
    lateinit var pickupStreet: AutoCompleteTextView
    lateinit var pickupBuild: EditText

    lateinit var deliveryCity: Spinner
    lateinit var deliveryStreet: AutoCompleteTextView
    lateinit var deliveryBuild: EditText

    lateinit var packageHeight: EditText
    lateinit var packageWidth: EditText
    lateinit var packageLength: EditText
    lateinit var packageWeight: EditText
    lateinit var errorMessage: TextView
    lateinit var comment: EditText


    /* String objects of the spinners and autofill */
    lateinit var strAreaCode: String
    lateinit var strPickupCity: String
    var strPickupStreet: String? = null
    lateinit var strDeliveryCity: String
    var strDeliveryStreet: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_new_order)
        errorMessage = findViewById(R.id.errorMassage)

        /* Get Shared Preference */
        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)

        /* Contact full name */
        contFirstName = findViewById(R.id.contFirstName)
        contLastName = findViewById(R.id.contLastName)

        /* Contact phone number */
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

        comment = findViewById(R.id.comment)


        /* Get the current valid measures from Database */
        getMeasures(
            this@CustomerNewOrder,
            packageHeight,
            packageWidth,
            packageLength,
            packageWeight
        )

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
            }
        }

        /* Delivery city Spinner */
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
                    getStreetByCity(this@CustomerNewOrder, strDeliveryCity)

                    /* Reassign the autocomplete values */
                    val arrayAdapter = ArrayAdapter(
                        this@CustomerNewOrder,
                        android.R.layout.simple_list_item_1,
                        streets
                    )
                    deliveryStreet.setAdapter(arrayAdapter)

                    deliveryStreet.threshold = 1

                    deliveryStreet.setOnItemClickListener { parent, view, position, id ->
                        strDeliveryStreet = parent.getItemAtPosition(position).toString()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@CustomerNewOrder, "Delivery street must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }

        /* Delivery street AutoComplete */
        val deliveryStreetArrayAdapter = ArrayAdapter(
            this@CustomerNewOrder,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.streetStarting)
        )
        deliveryStreet.setAdapter(deliveryStreetArrayAdapter)

        deliveryStreet.threshold = 1

        deliveryStreet.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(
                this@CustomerNewOrder,
                parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT
            ).show()
        }

        /* Pickup city Spinner */
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
                    getStreetByCity(this@CustomerNewOrder, strPickupCity)
                    /* Reassign the autocomplete values */
                    val arrayAdapter = ArrayAdapter(
                        this@CustomerNewOrder,
                        android.R.layout.simple_list_item_1,
                        streets
                    )

                    pickupStreet.setAdapter(arrayAdapter)

                    pickupStreet.threshold = 1

                    pickupStreet.setOnItemClickListener { parent, view, position, id ->
                        strPickupStreet = parent.getItemAtPosition(position).toString()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@CustomerNewOrder, "Pickup city must bo chosen", Toast.LENGTH_SHORT
                ).show()
            }
        }

        /* Pickup street AutoComplete */
        val pickupStreetArrayAdapter = ArrayAdapter(
            this@CustomerNewOrder,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.streetStarting)
        )
        pickupStreet.setAdapter(pickupStreetArrayAdapter)

        pickupStreet.threshold = 1

        pickupStreet.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(
                this@CustomerNewOrder,
                parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun newOrder(view: View) {
        if (isEmpty(contFirstName)
            && isEmpty(contLastName)
            && isPhoneNumberNoAreaCode(contPhoneNumber)
            && isEmpty(contEmail)
        ) {
            if (strPickupStreet.isNullOrBlank()) {
                pickupStreet.error = "Must choose street from the option's."
                return
            }
            if (strDeliveryStreet.isNullOrBlank()) {
                deliveryStreet.error = "Must choose street from the option's."
                return
            }
            if (validateStreetName(strPickupStreet!!, pickupStreet)
                && validateStreetName(strDeliveryStreet!!, deliveryStreet)
                && checkOrderMeasures(packageHeight, packageWidth, packageLength, packageWeight)
            ) {
                Toast.makeText(
                    this,
                    "All fields have been filled in correctly, creating your order",
                    Toast.LENGTH_SHORT
                ).show()

                val orderToAdd = Order(
                    UUID.randomUUID().toString(),
                    contFirstName.text.toString().trim() + " " + contLastName.text.toString()
                        .trim(),
                     strAreaCode + contPhoneNumber.text.toString().trim(),
                    contEmail.text.toString().trim(),
                    eStatus.NEW,
                    strPickupCity,
                    strPickupStreet!!,
                    pickupBuild.text.toString().trim(),
                    strDeliveryCity,
                    strDeliveryStreet!!,
                    deliveryBuild.text.toString().trim(),
                    comment.text.toString().trim()
                )

                createOrder(
                    this@CustomerNewOrder,
                    orderToAdd,
                    shrd.getString("email", "none").toString(),
                    errorMessage
                )
                Thread.sleep(1000)
                Toast.makeText(this, "New order created", Toast.LENGTH_SHORT).show()
                errorMessage.visibility = View.VISIBLE
                startActivity(Intent(this@CustomerNewOrder, CustomerOrderList::class.java))
                finish()
            }

        } else {
            Toast.makeText(this, "All order fields must be filled!", Toast.LENGTH_SHORT).show()
        }
    }
}