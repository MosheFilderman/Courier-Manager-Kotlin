package com.example.couriermanagerkotlin.activities.manager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.User
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.assignOrdersToCourierByPickupCity
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.availablePickupCities

class ManagerAssignOrder : AppCompatActivity() {

    lateinit var titleWithCourierName: TextView
    lateinit var pickupCitySpinner: Spinner

    private lateinit var chosenCourier: User
    var strChosenPickupCity: String? = null
    var strCurrentShipmentLimit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_assign_order)

        chosenCourier = (intent.getSerializableExtra("chosenCourier") as? User)!!
        strCurrentShipmentLimit = intent.extras?.get("shipmentLimit") as String

        titleWithCourierName = findViewById(R.id.pageTitle)
        pickupCitySpinner = findViewById(R.id.pickupCitySpinner)

        val strTitleWithCourierName =
            "Assign order's to ${chosenCourier.lastName} ${chosenCourier.firstName}"
        titleWithCourierName.text = strTitleWithCourierName

        /* Pickup city spinner initialise */
        val deliveryCityArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, availablePickupCities
        )

        pickupCitySpinner.adapter = deliveryCityArrayAdapter

        pickupCitySpinner.setSelection(0, false)

        pickupCitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    strChosenPickupCity = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun assignOrders(view: View) {
        assignOrdersToCourierByPickupCity(this@ManagerAssignOrder, chosenCourier.email, strChosenPickupCity.toString(), strCurrentShipmentLimit.toString())
        startActivity(Intent(this@ManagerAssignOrder, ManagerCouriersView::class.java))
        this@ManagerAssignOrder.finish()
    }
}