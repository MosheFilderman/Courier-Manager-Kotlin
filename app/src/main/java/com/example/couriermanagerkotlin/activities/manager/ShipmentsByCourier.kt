package com.example.couriermanagerkotlin.activities.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.couriermanagerkotlin.Courier
import com.example.couriermanagerkotlin.DBUtilities.Companion.getShipmentsByCourier
import com.example.couriermanagerkotlin.R

class ShipmentsByCourier : AppCompatActivity() {

    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var shipmentList: ListView
    lateinit var emptyListMsg: TextView

    private lateinit var chosenCourier: Courier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipments_by_courier)

        chosenCourier = (intent.getSerializableExtra("chosenCourier") as? Courier)!!

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        shipmentList = findViewById(R.id.shipmentListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        firstName.text = chosenCourier.firstName
        lastName.text = chosenCourier.lastName

        shipmentList.setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.courier_shipment_info, null)

            builder.setView(dialogLayout)


        }

        getShipmentsByCourier(this, chosenCourier.email, shipmentList, emptyListMsg)
    }
}