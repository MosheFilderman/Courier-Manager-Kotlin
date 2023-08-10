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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.couriermanagerkotlin.DBUtilities.Companion.getShipmentsByCourier
import com.example.couriermanagerkotlin.DBUtilities.Companion.shipments
import com.example.couriermanagerkotlin.DBUtilities.Companion.updateOrderStatus
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.eStatus.Companion.setToNext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.maps.android.PolyUtil


class CourierListView : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var shipmentsList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var navigationView: BottomNavigationView
    lateinit var mapView: MapView

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003

    /* Top Right corner logout button */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Exit")
                builder.setMessage("Are you sure you wish to logout?")
                builder.setIcon(R.drawable.baseline_close_24)
                builder.setPositiveButton("YES") { dialogInterface, i ->
                    val shrd: SharedPreferences = getSharedPreferences("shola", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = shrd.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this@CourierListView, Login::class.java))
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
        setContentView(R.layout.activity_courier_list_view)
        Places.initialize(applicationContext, getString(R.string.GOOGLE_API_KEY))
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        shipmentsList = findViewById(R.id.shipmentListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)


        shrd = getSharedPreferences("shola", Context.MODE_PRIVATE)
        firstName.text = shrd.getString("firstName", "Not")
        lastName.text = shrd.getString("lastName", "Signed!")

        navigationView = findViewById(R.id.nav)
        navigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.listView ->{
                    true
                }
                R.id.mapView ->{
                    startActivity(Intent(this@CourierListView, CourierMapView::class.java))
                    finish()
                    true
                }
                R.id.calculateRoute -> {
                    // Add the function which calculate the route
                    true
                }
                else -> false
            }
            true
        }
        navigationView.selectedItemId = R.id.listView

        // Check if there's permission to use location, if isn't request the permission
        if(ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
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

            val strPickupName = "${shipments[position].pickupFirstName} ${shipments[position].pickupLastName}"
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
                updateOrderStatus(this@CourierListView, shipments[position].orderId, shipments[position].status)
                Toast.makeText(this@CourierListView, "We working on update your order", Toast.LENGTH_SHORT).show()
                Thread.sleep(1000)
                getShipmentsByCourier(this, shrd.getString("email", "none").toString(), shipmentsList, emptyListMsg)
                println(shipments)
            }

            builder.setNeutralButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            builder.show()
        }

        getShipmentsByCourier(this, shrd.getString("email", "none").toString(), shipmentsList, emptyListMsg)

    }



//    fun showShortestRouteOnGoogleMaps(addresses: List<String>, context: Context) {
//        if (addresses.size < 2) {
//            return
//        }
//
//        val geocoder = Geocoder(context)
//        val waypoints = mutableListOf<LatLng>()
//
//        for (address in addresses) {
//            val results: List<Address>? = geocoder.getFromLocationName(address, 1)
//            if (results != null && results.isNotEmpty()) {
//                val location = results[0]
//                val latLng = LatLng(location.latitude, location.longitude)
//                waypoints.add(latLng)
//            }
//        }
//        Log.e("lanlng",waypoints.toString())
//        if (waypoints.size < 2) {
//
//            return
//        }
//
//        val apiKey = R.string.GOOGLE_API_KEY
//        val origin = "origin=${waypoints[0].latitude},${waypoints[0].longitude}"
//        val destination = "destination=${waypoints.last().latitude},${waypoints.last().longitude}"
//        val waypointsParam = waypoints.subList(1, waypoints.size - 1)
//            .joinToString(separator = "|") { "via:${it.latitude},${it.longitude}" }
//        val waypointsStr = if (waypointsParam.isNotEmpty()) "waypoints=$waypointsParam" else ""
//
//        val urlStr = "https://maps.googleapis.com/maps/api/directions/json?$origin&$destination&$waypointsStr&key=${getString(apiKey)}"
//
//
//        val queue = Volley.newRequestQueue(context)
//        val request = JsonObjectRequest(
//            Request.Method.GET, urlStr, null,
//            Response.Listener { response ->
//                Log.e("response",response.toString())
//                val routes = response.getJSONArray("routes")
//                if (routes != null && routes.length() > 0) {
//                    val route = routes.getJSONObject(0)
//                    val overviewPolyline = route.getJSONObject("overview_polyline")
//                    val encodedPolyline = overviewPolyline.getString("points")
//                    val waypointsStr = waypoints.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }
//                    val intentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${waypoints.last().latitude},${waypoints.last().longitude}&waypoints=$waypointsStr")
//
//                    val intent = Intent(Intent.ACTION_VIEW, intentUri)
//                    intent.setPackage("com.google.android.apps.maps")
//                    context.startActivity(intent)
//                }
//            },
//            Response.ErrorListener { error ->
//                error.printStackTrace()
//            }
//        )
//
//        queue.add(request)
//    }


}




