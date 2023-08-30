package com.example.couriermanagerkotlin.activities.courier

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.deliveryAddresses
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getShipmentsByCourier
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.pickupAddresses
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.shipments
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.updateOrderStatus
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.eStatus.Companion.setToNext

import com.example.couriermanagerkotlin.listViewAdapters.ShipmentsAdapter
import com.example.couriermanagerkotlin.objects.Shipment

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject


class CourierShipmentList : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView

    lateinit var shrd: SharedPreferences
    lateinit var shipmentsList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var search: SearchView
    var searchShipmentList = ArrayList<Shipment>()

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_list_view)

        Places.initialize(applicationContext, getString(R.string.GOOGLE_API_KEY))

        shipmentsList = findViewById(R.id.shipmentListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
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
                    startActivity(Intent(this@CourierShipmentList, CourierShipmentList::class.java))
                    finish()
                    true
                }

                R.id.calculateRoute -> {
                    if (pickupAddresses.size > 0){
                        startOptimizedNavigation(pickupAddresses)
                    }else{
                        startOptimizedNavigation(deliveryAddresses)
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
                    this@CourierShipmentList,
                    shipments[position].orderId,
                    shipments[position].status
                )
                Toast.makeText(
                    this@CourierShipmentList,
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
                searchShipmentList.clear()
                for (tmpShipment in shipments) {
                    if (tmpShipment.pickupEmail.lowercase().contains(p0!!.lowercase())
                        ||
                        tmpShipment.deliveryEmail.lowercase().contains(p0!!.lowercase())
                        ||
                        tmpShipment.pickupPhone.lowercase().contains(p0!!.lowercase())
                        ||
                        tmpShipment.deliveryPhone.lowercase().contains(p0!!.lowercase())
                    ) {
                        searchShipmentList.add(tmpShipment)
                    }
                }
                shipmentsList.adapter = ShipmentsAdapter(this@CourierShipmentList, searchShipmentList)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

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
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Check if there's enough addresses for the navigation calculation,
     * if not, pop toast's.
     */
    private fun startOptimizedNavigation(addresses: ArrayList<String>) {
        if (addresses.size < 2) {
            Toast.makeText(this, "Not enough addresses for navigation", Toast.LENGTH_SHORT).show()
            return
        }

        val geocoder = Geocoder(this)
        val waypoints = ArrayList<LatLng>()

        for (address in addresses) {
            val results: List<Address>? = geocoder.getFromLocationName(address, 1)
            if (results != null && results.isNotEmpty()) {
                val location = results[0]
                waypoints.add(LatLng(location.latitude, location.longitude))
            }
        }

        if (waypoints.size < 2) {
            Toast.makeText(this, "Not enough waypoints for navigation", Toast.LENGTH_SHORT).show()
            return
        }

        // Request an optimized route using Google Directions API
        requestOptimizedRoute(waypoints)
    }

    /**
     * Create URL for google direction's api, with the received addresses as waypoint's.
     */
    private fun requestOptimizedRoute(waypoints: List<LatLng>) {
        val origin = waypoints.first()
        Log.e("origin",origin.toString())
        val destination = waypoints.last()
        Log.e("destination",destination.toString())
        val waypointsExceptOriginAndDestination = waypoints.subList(1, waypoints.size)
        Log.e("waypointsExceptOriginAndDestination",waypointsExceptOriginAndDestination.toString())
        val waypointsParam = waypointsExceptOriginAndDestination.joinToString("|") { "${it.latitude},${it.longitude}" }
        Log.e("waypointsParam",waypointsParam.toString())
        val apiKey = getString(R.string.GOOGLE_API_KEY)
        val url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&waypoints=optimize:true|$waypointsParam" +
                "&key=$apiKey"

        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val optimizedWaypoints = parseOptimizedWaypoints(response)

                if (optimizedWaypoints.isNotEmpty()) {
                    // Construct intentUri using optimizedWaypoints
                    val intentUri = buildNavigationUri(optimizedWaypoints)

                    // Launch navigation using intentUri
                    val intent = Intent(Intent.ACTION_VIEW, intentUri)
                    intent.setPackage("com.google.android.apps.maps") // Use the package name of Google Maps
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Google Maps app not installed.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            { error ->
                // Handle error
                Toast.makeText(this, "Error retrieving route: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

    /**
     * Convert google response into list of coordinate's.
     */
    private fun parseOptimizedWaypoints(response: JSONObject): List<LatLng> {
        val optimizedWaypoints = ArrayList<LatLng>()

        val routes = response.getJSONArray("routes")
        if (routes.length() > 0) {
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            for (i in 0 until legs.length()) {
                val leg = legs.getJSONObject(i)
                val startLocation = leg.getJSONObject("start_location")
                val lat = startLocation.getDouble("lat")
                val lng = startLocation.getDouble("lng")
                optimizedWaypoints.add(LatLng(lat, lng))
            }
        }

        return optimizedWaypoints
    }

    /**
     * Convert the waypoint's into URI, which send to google api.
     */
    private fun buildNavigationUri(waypoints: List<LatLng>): Uri {
        val intentUriBuilder = StringBuilder("google.navigation:q=${waypoints.last().latitude},${waypoints.last().longitude}&waypoints=")
        for (i in 0 until waypoints.size - 1) {
            intentUriBuilder.append("${waypoints[i].latitude},${waypoints[i].longitude}")
            if (i < waypoints.size - 2) {
                intentUriBuilder.append("|")
            }
        }
        return Uri.parse(intentUriBuilder.toString())
    }
}
