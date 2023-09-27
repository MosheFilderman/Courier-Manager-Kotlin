package com.example.couriermanagerkotlin.activities.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.couriermanagerkotlin.User
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getShipmentsByCourier
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.shipments
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.availablePickupCities
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.suspendUser
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ShipmentsByCourier : AppCompatActivity() {

    private val maxShipmentAmount = 20

    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var currentShipmentAmountView: TextView
    lateinit var shipmentList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var floatingAssignButton: FloatingActionButton

    private lateinit var chosenCourier: User
    private var currentShipmentAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipments_by_courier)

        chosenCourier = (intent.getSerializableExtra("chosenCourier") as? User)!!

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        currentShipmentAmountView = findViewById(R.id.currentShipmentAmount)
        shipmentList = findViewById(R.id.shipmentListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)
        floatingAssignButton = findViewById(R.id.floatingAssignButton)

        firstName.text = chosenCourier.firstName
        lastName.text = chosenCourier.lastName

        currentShipmentAmount = if(shipments.isEmpty()) {
            0
        } else {
            shipments.size
        }

        currentShipmentAmountView.text = currentShipmentAmount.toString()

        if(maxShipmentAmount <= currentShipmentAmount || availablePickupCities[0].equals("No NEW order's yet")) {
            floatingAssignButton.visibility = View.GONE
        } else {
            floatingAssignButton.visibility = View.VISIBLE
        }

        shipmentList.setOnItemClickListener { parent, view, position, id ->
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


            val fullPickupAddress: String = "${shipments[position].pickupStreet} ${shipments[position].pickupBuild}, ${shipments[position].pickupCity}"
            val fullDeliveryAddress: String = "${shipments[position].deliveryStreet} ${shipments[position].deliveryBuild}, ${shipments[position].deliveryCity}"

            pickupName = dialogLayout.findViewById(R.id.pickupName)
            pickupPhone = dialogLayout.findViewById(R.id.pickupPhone)
            pickupEmail = dialogLayout.findViewById(R.id.pickupEmail)
            pickupAddress = dialogLayout.findViewById(R.id.pickupAddress)
            deliveryName = dialogLayout.findViewById(R.id.deliveryName)
            deliveryPhone = dialogLayout.findViewById(R.id.deliveryPhone)
            deliveryEmail = dialogLayout.findViewById(R.id.deliveryEmail)
            deliveryAddress = dialogLayout.findViewById(R.id.deliveryAddress)
            status = dialogLayout.findViewById(R.id.orderStatus)
            comment = dialogLayout.findViewById(R.id.comment)

            pickupName.text = shipments[position].pickupName
            pickupPhone.text = shipments[position].pickupPhone
            pickupEmail.text = shipments[position].pickupEmail
            pickupAddress.text = fullPickupAddress
            deliveryName.text = shipments[position].deliveryName
            deliveryPhone.text = shipments[position].deliveryPhone
            deliveryEmail.text = shipments[position].deliveryEmail
            deliveryAddress.text = fullDeliveryAddress
            status.text = shipments[position].status.name
            comment.text = shipments[position].comment

            builder.setNeutralButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            builder.show()
        }

        getShipmentsByCourier(this, chosenCourier.email, shipmentList, currentShipmentAmountView, emptyListMsg)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.suspend_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.suspendUser -> {
                if(shipments.size == 0){
                    suspendUser(this@ShipmentsByCourier, chosenCourier.email, chosenCourier.phone)
                    startActivity(Intent(this@ShipmentsByCourier, Manager::class.java))
                } else {
                    Toast.makeText(this@ShipmentsByCourier, "Courier still have undelivered shipment's", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun assignOrders(view: View) {
        intent = Intent(this@ShipmentsByCourier, ManagerAssignOrder::class.java)
        intent.putExtra("chosenCourier", chosenCourier)
        intent.putExtra("shipmentLimit", (maxShipmentAmount - currentShipmentAmount).toString())
        startActivity(intent)
    }
}