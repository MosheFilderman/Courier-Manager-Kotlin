package com.example.couriermanagerkotlin.activities.courier

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.couriermanagerkotlin.DBUtilities
import com.example.couriermanagerkotlin.DBUtilities.Companion.deliveryAddresses
import com.example.couriermanagerkotlin.DBUtilities.Companion.getShipmentsByCourier
import com.example.couriermanagerkotlin.DBUtilities.Companion.pickupAddresses
import com.example.couriermanagerkotlin.DBUtilities.Companion.shipments
import com.example.couriermanagerkotlin.DBUtilities.Companion.updateOrderStatus
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.activities.manager.AddEmployee
import com.example.couriermanagerkotlin.activities.manager.AppSettings
import com.example.couriermanagerkotlin.eStatus
import com.example.couriermanagerkotlin.eStatus.Companion.setToNext
import com.example.couriermanagerkotlin.utilities.GoogleUtilities
import com.example.couriermanagerkotlin.utilities.GoogleUtilities.Companion.waypoints
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.PolyUtil


class CourierListView : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var shipmentsList: ListView
    lateinit var emptyListMsg: TextView

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_list_view)

        Places.initialize(applicationContext, getString(R.string.GOOGLE_API_KEY))

        val addresses = listOf("הקיבוצים 70 חיפה", "בר אילן 7 חיפה", "גורדון 4 קרית מוצקין")
        shipmentsList = findViewById(R.id.shipmentListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.listView -> {
                    startActivity(Intent(this@CourierListView, CourierListView::class.java))
                    finish()
                    true
                }

                R.id.calculateRoute -> {
                    if (pickupAddresses.size > 0){
                        launchNavigation(pickupAddresses)
                    }else{
                        launchNavigation(deliveryAddresses)
                    }
                    true
                }

                R.id.logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Exit")
                    builder.setMessage("Are you sure you wish to logout?")
                    builder.setIcon(R.drawable.baseline_close_24)
                    builder.setPositiveButton("YES") { dialogInterface, i ->
                        val editor: SharedPreferences.Editor = shrd.edit()
                        editor.clear()
                        editor.apply()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                    builder.setNegativeButton("NO") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                    true
                }

                else -> false
            }
        }

        userFullName = headerView.findViewById(R.id.userFullName)
        userEmail = headerView.findViewById(R.id.userEmail)

        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        val strUserFullName =
            "${shrd.getString("firstName", "Not")} ${shrd.getString("lastName", "Signed !")}"
        userFullName.text = strUserFullName
        userEmail.text = shrd.getString("email", "courierManager@courierManager")

        // Check if there's permission to use location, if isn't request the permission
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        shipmentsList.setOnItemClickListener { parent, view, position, id ->
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

            val strPickupName =
                "${shipments[position].pickupFirstName} ${shipments[position].pickupLastName}"
            val fullPickupAddress: String =
                "${shipments[position].pickupStreet} ${shipments[position].pickupBuild}, ${shipments[position].pickupCity}"
            val fullDeliveryAddress: String =
                "${shipments[position].deliveryStreet} ${shipments[position].deliveryBuild}, ${shipments[position].deliveryCity}"

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

            pickupName.text = strPickupName
            pickupPhone.text = shipments[position].pickupPhone
            pickupEmail.text = shipments[position].pickupEmail
            pickupAddress.text = fullPickupAddress
            deliveryName.text = shipments[position].deliveryName
            deliveryPhone.text = shipments[position].deliveryPhone
            deliveryEmail.text = shipments[position].deliveryEmail
            deliveryAddress.text = fullDeliveryAddress
            status.text = shipments[position].status.name
            comment.text = shipments[position].comment

            builder.setPositiveButton("Change Status") { dialogInterface, i ->
                shipments[position].status = setToNext(shipments[position].status)
                status.text = shipments[position].status.name
                updateOrderStatus(
                    this@CourierListView,
                    shipments[position].orderId,
                    shipments[position].status
                )
                Toast.makeText(
                    this@CourierListView,
                    "We working on update your order",
                    Toast.LENGTH_SHORT
                ).show()
                Thread.sleep(1000)
                getShipmentsByCourier(
                    this,
                    shrd.getString("email", "none").toString(),
                    shipmentsList,
                    emptyListMsg
                )
            }

            builder.setNeutralButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            builder.show()
        }





        getShipmentsByCourier(
            this,
            shrd.getString("email", "none").toString(),
            shipmentsList,
            emptyListMsg
        )



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun launchNavigation(addresses: ArrayList<String>) {
//        waypoints.clear()
//
//        if (addresses.size < 2) {
//            Toast.makeText(this@CourierListView, "Not enough addresses", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val geocoder = Geocoder(this@CourierListView)
//
//        for (address in addresses) {
//            val results: List<Address>? = geocoder.getFromLocationName(address, 1)
//            if (results != null && results.isNotEmpty()) {
//                val location = results[0]
//                Log.i("street geocoder", location.toString())
//                val latLng = LatLng(location.latitude, location.longitude)
//                Log.i("Lat Lng",latLng.toString())
//                waypoints.add(latLng)
//            }
//        }
//
//        if (waypoints.size < 2) {
//            Toast.makeText(this@CourierListView, "Not enough way points", Toast.LENGTH_SHORT).show()
//            return
//        }
//        Toast.makeText(this@CourierListView, "before if.", Toast.LENGTH_SHORT).show()
//        if (waypoints.size > 0) {
//            Toast.makeText(this@CourierListView, "true", Toast.LENGTH_SHORT).show()
//            val intentUri = Uri.parse("google.navigation:q=${waypoints.last().latitude},${waypoints.last().longitude}")
//            val intent = Intent(Intent.ACTION_VIEW, intentUri)
//            intent.setPackage("com.google.android.apps.maps") // Use the package name of Google Maps
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivity(intent)
//            } else {
//                Toast.makeText(this@CourierListView, "Google Maps app not installed.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
private fun launchNavigation(addresses: ArrayList<String>) {
    if (addresses.size < 2) {
        Toast.makeText(this, "Not enough valid addresses for navigation.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiKey = getString(R.string.GOOGLE_API_KEY)
    val origin = addresses.first()
    val destination = addresses.last()
    val waypoints = addresses.subList(1, addresses.size - 1).joinToString("|")

    val intentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&waypoints=$waypoints&key=$apiKey")
    val intent = Intent(Intent.ACTION_VIEW, intentUri)

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Google Maps app not installed.", Toast.LENGTH_SHORT).show()
    }

}

}




