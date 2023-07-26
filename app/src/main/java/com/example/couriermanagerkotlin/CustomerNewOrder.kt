package com.example.couriermanagerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import androidx.appcompat.app.AlertDialog
import com.example.couriermanagerkotlin.DBUtilities.Companion.streets
import java.io.IOException
import java.util.UUID
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult


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
    //  private val geocoder: Geocoder = Geocoder(this, Locale.getDefault())


    /* String objects of the spinners */
    lateinit var strAreaCode: String
    lateinit var strDeliveryCity: String
    var strDeliveryStreet: String? = null
    lateinit var strPickupCity: String
    var strPickupStreet: String? = null


    /* Menu toolbar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.customer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newOrder -> Toast.makeText(this, "You already at this page!", Toast.LENGTH_SHORT)
                .show()

            R.id.orderList -> {
                startActivity(Intent(this@CustomerNewOrder, CustomerOrderList::class.java))
                finish()

            }

            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Exit")
                builder.setMessage("Are you sure you wish to logout?")
                builder.setIcon(R.drawable.baseline_close_24)
                builder.setPositiveButton("YES") { dialogInterface, _ ->
                    val editor: SharedPreferences.Editor = shrd.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this@CustomerNewOrder, Login::class.java))
                    finish()
                }
                builder.setNegativeButton("NO") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_new_order)


//        val validator = AddressValidator(this)
//        val validatedAddress: Address? = validator.validateAddress("גורדון 8 קרית מוצקין")
//        val apiKey = R.string.GOOGLE_API_KEY
//        try {
//            val geoApiContext = GeoApiContext.Builder().apiKey(apiKey.toString()).build()
//            // Rest of your code using the geoApiContext
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//           val geoApiContext: GeoApiContext = GeoApiContext.Builder().apiKey(apiKey.toString()).build()

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

        errorMessage = findViewById(R.id.errorMassage)

//        errorMessage.text = validateAddressTest("גורדון 8 קרית ים",geoApiContext).toString()
        comment = findViewById(R.id.comment)

        /* Get Shared Preference */
        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)

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
                    DBUtilities.getStreetByCity(this@CustomerNewOrder, strDeliveryCity)

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

        /* Pickup street AutoComplete */
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
                    DBUtilities.getStreetByCity(this@CustomerNewOrder, strPickupCity)

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
        if (strDeliveryStreet.isNullOrEmpty() || strPickupStreet.isNullOrEmpty()) {
            deliveryStreet.error = "Must choose street from the option's."
            return
        }
        if (Validations.checkOrderMeasures(
                packageHeight,
                packageWidth,
                packageLength,
                packageWeight
            ) && Validations.isEmpty(contFirstName) && Validations.isEmpty(contLastName) && Validations.isEmpty(
                contEmail
            ) && Validations.isEmpty(contPhoneNumber)
        ) {
            Toast.makeText(this, "All fields filled correctly.", Toast.LENGTH_SHORT).show()
            val orderToAdd = Order(
                UUID.randomUUID().toString(),
                contFirstName.text.toString().trim() + " " + contLastName.text.toString().trim(),
                "+972" + strAreaCode.substring(1) + contPhoneNumber.text.toString().trim(),
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
            Log.d("Order object", orderToAdd.toString())
            DBUtilities.createOrder(
                this@CustomerNewOrder,
                orderToAdd,
                shrd.getString("email", "none").toString(),
                errorMessage
            )
            startActivity(Intent(this@CustomerNewOrder, CustomerOrderList::class.java))
            Toast.makeText(this, "New order created.", Toast.LENGTH_SHORT).show()
            errorMessage.visibility = View.VISIBLE
            finish()
        } else {
            Toast.makeText(this, "All order fields must be filled!", Toast.LENGTH_SHORT).show()
        }

    }

    fun validateAddressTest(address: String, geoApiContext: GeoApiContext): Boolean {
        try {
            val results: Array<GeocodingResult> =
                GeocodingApi.geocode(geoApiContext, address).await()
            if (results.isNotEmpty()) {
                val formattedAddress = results[0].formattedAddress
                // Compare the formatted address with the original input
                return formattedAddress.equals(address, ignoreCase = true)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

}