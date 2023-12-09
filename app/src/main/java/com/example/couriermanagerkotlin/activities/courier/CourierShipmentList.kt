package com.example.couriermanagerkotlin.activities.courier

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.text.util.Linkify
import android.view.Menu
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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.activities.EditUserDetails
import com.example.couriermanagerkotlin.eNums.eStatus
import com.example.couriermanagerkotlin.eNums.eStatus.Companion.setToNext
import com.example.couriermanagerkotlin.listViewAdapters.ShipmentsAdapter
import com.example.couriermanagerkotlin.objects.Shipment
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.getShipmentsByCourier
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.routeAddresses
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.shipments
import com.example.couriermanagerkotlin.utilities.DBUtilities.Companion.updateOrderStatus
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class CourierShipmentList : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView

    private var scannedContent: String? = null
    lateinit var shrd: SharedPreferences
    lateinit var shipmentsList: ListView
    lateinit var emptyListMsg: TextView
    lateinit var search: SearchView
    var searchShipmentList = ArrayList<Shipment>()


    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_shipment_list)

        shipmentsList = findViewById(R.id.shipmentListView)
        emptyListMsg = findViewById(R.id.emptyListMsg)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)


        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.calculateRoute -> {

                    if (routeAddresses.size > 0) {
                        openGoogleMapsWithRouteToNearest(
                            this@CourierShipmentList,
                            routeAddresses,
                            getString(R.string.GOOGLE_API_KEY)
                        )
                    } else {
                        Toast.makeText(
                            this@CourierShipmentList, "No Addresses found", Toast.LENGTH_LONG
                        ).show()
                    }

                    true
                }

                R.id.editInfo -> {
                    startActivity(Intent(this@CourierShipmentList, EditUserDetails::class.java))
                    drawerLayout.close()
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
                this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        shipmentsList.setOnItemClickListener { parent, view, position, id ->
            showShipmentFullInfo(shipments[position])
        }

        getShipmentsByCourier(
            this, shrd.getString("email", "none").toString(), shipmentsList, emptyListMsg
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
                    if (p0 != null) {
                        if (
                            tmpShipment.pickupPhone.startsWith(p0)
                            || tmpShipment.deliveryPhone.startsWith(p0)
                            || tmpShipment.deliveryEmail.lowercase().startsWith(p0.lowercase())
                            || tmpShipment.pickupEmail.lowercase().startsWith(p0.lowercase())
                            || tmpShipment.deliveryName.lowercase().startsWith(p0.lowercase())
                            || tmpShipment.pickupName.lowercase().startsWith(p0.lowercase())
                            || tmpShipment.deliveryStreet.lowercase().startsWith(p0.lowercase())
                            || tmpShipment.pickupStreet.lowercase().startsWith(p0.lowercase())
                        ){
                            searchShipmentList.add(tmpShipment)
                        }
                    }
                }
                shipmentsList.adapter =
                    ShipmentsAdapter(this@CourierShipmentList, searchShipmentList)
                shipmentsList.setOnItemClickListener { parent, view, position, id ->
                    showShipmentFullInfo(searchShipmentList[position])
                }
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

        builder.setPositiveButton("Change Status") { dialogInterface, i ->
            shipment.status = setToNext(shipment.status)
            status.text = shipment.status.name
            updateOrderStatus(
                this@CourierShipmentList,
                shipment.orderId,
                shipment.status
            )
            Toast.makeText(
                this@CourierShipmentList, "We working on update your order", Toast.LENGTH_SHORT
            ).show()
            Thread.sleep(1000)
            sendSMS(shipment)
            getShipmentsByCourier(
                this, shrd.getString("email", "none").toString(), shipmentsList, emptyListMsg
            )
        }

        builder.setNeutralButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun openGoogleMapsWithRouteToNearest(
        context: Context,
        addresses: ArrayList<String>,
        apiKey: String
    ) {
        Toast.makeText(this@CourierShipmentList,"looking for the closest address",Toast.LENGTH_LONG).show()
        GlobalScope.launch(Dispatchers.IO) {
            val originLatLng = getCurrentLocationLatLng(context)

            val nearestAddress = findNearestAddress(context, originLatLng, addresses)

            withContext(Dispatchers.Main) {
                if (nearestAddress.isNotEmpty()) {
                    val uri = Uri.parse("google.navigation:q=${nearestAddress.replace(" ", "+")}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocationLatLng(context: Context): LatLng {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)

        if (provider != null) {
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                return LatLng(location.latitude, location.longitude)
            }
        }

        // Default to a fallback location if current location is not available
        return LatLng(0.0, 0.0)
    }

    interface DistanceCallback {
        fun onDistanceReceived(distance: Double)
        fun onError(message: String)
    }

    fun calculateDistance(
        context: Context,
        start: LatLng,
        end: LatLng,
        apiKey: String,
        callback: DistanceCallback
    ) {
        val url =
            "https://maps.googleapis.com/maps/api/directions/json" + "?origin=${start.latitude},${start.longitude}" + "&destination=${end.latitude},${end.longitude}" + "&mode=driving" + "&key=$apiKey"

        val requestQueue = Volley.newRequestQueue(context)
        val request =
            JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
                try {
                    val routes = response.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val route = routes.getJSONObject(0)
                        val legs = route.getJSONArray("legs")
                        if (legs.length() > 0) {
                            val leg = legs.getJSONObject(0)
                            val distanceObject = leg.getJSONObject("distance")
                            val distanceInMeters = distanceObject.getInt("value")
                            val distanceInKilometers = distanceInMeters.toDouble() / 1000.0
                            callback.onDistanceReceived(distanceInKilometers)
                        } else {
                            callback.onError("No legs found in the route.")
                        }
                    } else {
                        callback.onError("No routes found.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.onError("Error parsing distance data.")
                }
            }, Response.ErrorListener { error ->
                callback.onError("Error making distance request: ${error.message}")
            })

        requestQueue.add(request)
    }

    fun findNearestAddress(
        context: Context,
        originLatLng: LatLng,
        addresses: ArrayList<String>
    ): String {
        var nearestAddress = ""
        var shortestDistance = Double.MAX_VALUE

        for (address in addresses) {
            val destinationLatLng = getLatLngFromAddress(context, address)
            if (destinationLatLng != null) {
                calculateDistance(context,
                    originLatLng,
                    destinationLatLng,
                    getString(R.string.GOOGLE_API_KEY),
                    object : DistanceCallback {
                        override fun onDistanceReceived(distance: Double) {

                            if (distance < shortestDistance) {
                                shortestDistance = distance
                                nearestAddress = address

                            }
                            println("Distance between the two points: $distance kilometers")
                        }

                        override fun onError(message: String) {
                            // Handle errors here
                            println("Error: $message")
                        }
                    })
            }
        }
        Thread.sleep(2500)
        return nearestAddress
    }

    fun getLatLngFromAddress(context: Context, addressStr: String): LatLng? {
        val geocoder = Geocoder(context)
        try {
            val addresses: List<Address>? = geocoder.getFromLocationName(addressStr, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val latitude = address.latitude
                val longitude = address.longitude
                return LatLng(latitude, longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
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
            scannedContent = result.contents
            searchShipmentList.clear()
            for (tmpShipment in shipments) {
                if (tmpShipment.orderId == scannedContent) {
                    showShipmentFullInfo(tmpShipment)
                    return
                }
            }
            Toast.makeText(this@CourierShipmentList, "No matching order id", Toast.LENGTH_SHORT).show()
        }
    }


    fun sendSMS(shipment: Shipment) {
        // on the below line we are creating a try and catch block
        try {
            // on below line we are initializing sms manager.
            // as after android 10 the getDefault function no longer works
            // so we have to check that if our android version is greater
            // than or equal to android version 6.0 i.e SDK 23
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                // if SDK is greater that or equal to 23 then
                // this is how we will initialize the SmsManager
                smsManager = this.getSystemService(SmsManager::class.java)
            } else {
                // if user's SDK is less than 23 then
                // SmsManager will be initialized like this
                smsManager = SmsManager.getDefault()
            }
            // on below line we are sending text message.
            when(shipment.status){

                eStatus.COLLECTED -> {
                    smsManager.sendTextMessage("+972"+shipment.deliveryPhone.substring(1), null, " שלום ${shipment.deliveryName},ההזמנה מספר ${shipment.orderId.substring(0,8)} שלך נאספה ותסופק בימים הקרובים ", null, null)
                }
                eStatus.DELIVERED -> {
                    smsManager.sendTextMessage("+972"+shipment.deliveryPhone.substring(1), null, " שלום ${shipment.deliveryName} ,ההזמנה מספר ${shipment.orderId.substring(0,8)} הגיעה ליעד. ", null, null)
                    smsManager.sendTextMessage("+972"+shipment.pickupPhone.substring(1), null, " שלום ${shipment.pickupName} ,ההזמנה מספר ${shipment.orderId.substring(0,8)} הגיעה ליעד.  ", null, null)
                }

                else -> {

                }
            }

            // on below line we are displaying a toast message for message send,
            // Toast.makeText(applicationContext, phone.substring(1), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // on catch block we are displaying toast message for error.
            Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }

}
