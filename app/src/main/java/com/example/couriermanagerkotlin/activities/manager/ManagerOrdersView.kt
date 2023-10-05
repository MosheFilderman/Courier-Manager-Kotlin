package com.example.couriermanagerkotlin.activities.manager

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.eNums.eStatus
import com.example.couriermanagerkotlin.listViewAdapters.OrdersAdapter
import com.example.couriermanagerkotlin.listViewAdapters.ShipmentsAdapter
import com.example.couriermanagerkotlin.objects.Shipment
import com.example.couriermanagerkotlin.utilities.DBUtilities
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.shipments
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ManagerOrdersView : AppCompatActivity() {

    lateinit var shipmentsListView: ListView
    lateinit var emptyListTextView: TextView
    lateinit var search: SearchView
    lateinit var statusSpinner: Spinner

    val searchShipmentsList = ArrayList<Shipment>()
    val statusesNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_orders_view)

        shipmentsListView = findViewById(R.id.shipmentsListView)
        statusSpinner = findViewById(R.id.statusSpinner)

        statusesNames.add("SORT BY: STATUS")

        eStatus.values().forEach { status ->
            statusesNames.add(status.name)
        }

        val statusArrayAdapter = ArrayAdapter(
            this@ManagerOrdersView,
            android.R.layout.simple_spinner_item,
            statusesNames
        )
        statusSpinner.adapter = statusArrayAdapter

        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent != null) {
                    if(position != 0) {
                        val statusSortBy = parent.getItemAtPosition(position).toString()

                        searchShipmentsList.clear()
                        for (tmpShipment in shipments) {
                            if (
                                tmpShipment.status.name.compareTo(statusSortBy) == 0
                            ) {
                                searchShipmentsList.add(tmpShipment)
                            }
                        }
                        shipmentsListView.adapter = ShipmentsAdapter(this@ManagerOrdersView, searchShipmentsList)
                        shipmentsListView.setOnItemClickListener { parent, view, position, id ->
                            showShipmentFullInfo(searchShipmentsList[position])
                        }
                    } else {
                        resetStatusSpinner()
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        shipmentsListView.adapter = ShipmentsAdapter(this, shipments)

        shipmentsListView.setOnItemClickListener { parent, view, position, id ->
            showShipmentFullInfo(shipments[position])
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        // Set up search view listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Toast.makeText(this@ManagerOrdersView, searchShipmentsList.isEmpty().toString(), Toast.LENGTH_SHORT).show()
                if(searchShipmentsList.isEmpty()) {
                    for (tmpShipment in shipments) {
                        if (p0 != null) {
                            if (
                                tmpShipment.pickupPhone.startsWith(p0) ||
                                tmpShipment.deliveryPhone.startsWith(p0) ||
                                tmpShipment.deliveryEmail.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.pickupEmail.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.deliveryName.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.pickupName.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.deliveryStreet.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.pickupStreet.lowercase().startsWith(p0.lowercase())
                            ) {
                                searchShipmentsList.add(tmpShipment)
                            }
                        }
                    }
                    shipmentsListView.adapter =
                        ShipmentsAdapter(this@ManagerOrdersView, searchShipmentsList)
                    shipmentsListView.setOnItemClickListener { parent, view, position, id ->
                        showShipmentFullInfo(searchShipmentsList[position])
                    }
                } else {
                    val tmpSearchShipments = ArrayList<Shipment>()

                    for (tmpShipment in searchShipmentsList) {
                        if (p0 != null) {
                            if (
                                tmpShipment.pickupPhone.startsWith(p0) ||
                                tmpShipment.deliveryPhone.startsWith(p0) ||
                                tmpShipment.deliveryEmail.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.pickupEmail.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.deliveryName.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.pickupName.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.deliveryStreet.lowercase().startsWith(p0.lowercase()) ||
                                tmpShipment.pickupStreet.lowercase().startsWith(p0.lowercase())
                            ) {
                                tmpSearchShipments.add(tmpShipment)
                            }
                        }
                    }
                    shipmentsListView.adapter = ShipmentsAdapter(this@ManagerOrdersView, tmpSearchShipments)
                    shipmentsListView.setOnItemClickListener { parent, view, position, id ->
                        showShipmentFullInfo(tmpSearchShipments[position])
                    }
                }

                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle home button press
                if (!search.isIconified) {
                    search.isIconified = true
                } else {
                    // Handle other home navigation logic here
                }
                return true
            }
            R.id.qr -> {
                startQRCodeScanning()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun showShipmentFullInfo(shipment: Shipment) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.courier_shipment_full_info, null)

        builder.setView(dialogLayout)

        var pickupName: TextView? = null
        var pickupPhone: TextView? = null
        var pickupEmail: TextView? = null
        var pickupAddress: TextView? = null
        var deliveryName: TextView? = null
        var deliveryPhone: TextView? = null
        var deliveryEmail: TextView? = null
        var deliveryAddress: TextView? = null
        var status: TextView? = null
        var comment: TextView? = null


        val fullPickupAddress: String =
            "${shipment.pickupStreet} ${shipment.pickupBuild}, ${shipment.pickupCity}"
        val fullDeliveryAddress: String =
            "${shipment.deliveryStreet} ${shipment.deliveryBuild}, ${shipment.deliveryCity}"

        pickupName = dialogLayout.findViewById(R.id.pickupName)
        pickupPhone = dialogLayout.findViewById(R.id.pickupPhone)
        Linkify.addLinks(pickupPhone, Linkify.PHONE_NUMBERS)
        pickupEmail = dialogLayout.findViewById(R.id.pickupEmail)
        pickupAddress = dialogLayout.findViewById(R.id.pickupAddress)
        deliveryName = dialogLayout.findViewById(R.id.deliveryName)
        deliveryPhone = dialogLayout.findViewById(R.id.deliveryPhone)
        Linkify.addLinks(deliveryPhone, Linkify.PHONE_NUMBERS)
        deliveryEmail = dialogLayout.findViewById(R.id.deliveryEmail)
        deliveryAddress = dialogLayout.findViewById(R.id.deliveryAddress)
        status = dialogLayout.findViewById(R.id.orderStatus)
        comment = dialogLayout.findViewById(R.id.comment)

        pickupName.text = shipment.pickupName
        pickupPhone.text = shipment.pickupPhone
        pickupEmail.text = shipment.pickupEmail
        pickupAddress.text = fullPickupAddress
        deliveryName.text = shipment.deliveryName
        deliveryPhone.text = shipment.deliveryPhone
        deliveryEmail.text = shipment.deliveryEmail
        deliveryAddress.text = fullDeliveryAddress
        status.text = shipment.status.name
        comment.text = shipment.comment

        builder.setNeutralButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun startQRCodeScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan Order QR Code")
        integrator.setCameraId(0)  // Use the back camera
        integrator.setBeepEnabled(true) // Beep when a QR code is scanned
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents != null) {
            var scannedContent = result.contents
            searchShipmentsList.clear()
            for (shipment in shipments) {
                if (shipment.orderId == scannedContent) {
                    showShipmentFullInfo(shipment)
                    return
                }
            }
            Toast.makeText(this@ManagerOrdersView, "No matching order id", Toast.LENGTH_SHORT).show()
        }
    }

    fun resetStatusSpinner() {
        searchShipmentsList.clear()
        shipmentsListView.adapter = ShipmentsAdapter(this@ManagerOrdersView, shipments)
        shipmentsListView.setOnItemClickListener { parent, view, position, id ->
            showShipmentFullInfo(shipments[position])
        }
    }
}