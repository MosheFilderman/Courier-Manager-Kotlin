package com.example.couriermanagerkotlin.activities.courier

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.couriermanagerkotlin.Login
import com.example.couriermanagerkotlin.R
import com.example.couriermanagerkotlin.utilities.GoogleUtilities
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.maps.android.PolyUtil

class CourierMapView : AppCompatActivity() {

    lateinit var shrd: SharedPreferences
    lateinit var navigationView: BottomNavigationView
    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_map_view)
//        var shrd: SharedPreferences = getSharedPreferences("shola", Context.MODE_PRIVATE)
//        val addresses = listOf("הקיבוצים 70 חיפה", "בר אילן 7 חיפה", "גורדון 4 קרית מוצקין")

//        mapView = findViewById(R.id.mapView)
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync { map ->
//            googleMap = map
//            // You can customize the map settings and add markers here
//        }
//
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        val locationRequest = LocationRequest.create().apply {
//            interval = 10000 // Update interval in milliseconds
//            fastestInterval = 5000 // Fastest update interval in milliseconds
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }

//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(p0: LocationResult) {
//                p0.lastLocation?.let { location ->
//                    // Update the map with the current location
//                    // For example, you can call a method to update the marker on the map
//                    updateMapWithLocation(location)
//                }
//            }
//        }

        // Request location updates
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }
//
//    private fun updateMapWithLocation(location: Location) {
//        val map = mapView.getMapAsync { googleMap ->
//            val latLng = LatLng(location.latitude, location.longitude)
//            googleMap.clear() // Clear existing markers
//            googleMap.addMarker(MarkerOptions().position(latLng))
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//        }
//    }

//    private fun showShortestRouteOnMapView(addresses: List<String>, mapView: MapView) {
//        if (addresses.size < 2) {
//            // Handle the case where there are not enough addresses
//            return
//        }
//
//        val geocoder = Geocoder(this)
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
//            // Handle the case where there are not enough waypoints
//            return
//        }
//
//        val apiKey = R.string.GOOGLE_API_KEY
//        val waypointsStr = waypoints.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }
//        val urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin=${waypoints.first().latitude},${waypoints.first().longitude}&destination=${waypoints.last().latitude},${waypoints.last().longitude}&waypoints=$waypointsStr&key=${getString(apiKey)}"
//
//        val queue = Volley.newRequestQueue(this)
//        val request = JsonObjectRequest(
//            Request.Method.GET, urlStr, null,
//            Response.Listener { response ->
//                val routes = response.optJSONArray("routes")
//                if (routes != null && routes.length() > 0) {
//                    val route = routes.getJSONObject(0)
//                    val overviewPolyline = route.getJSONObject("overview_polyline")
//                    val encodedPolyline = overviewPolyline.getString("points")
//
//                    mapView.getMapAsync { googleMap ->
//                        val decodedPath = PolyUtil.decode(encodedPolyline)
//                        val polylineOptions = PolylineOptions()
//                            .addAll(decodedPath)
//                            .color(Color.BLUE)
//                            .width(10f)
//
//                        googleMap.clear()
//                        googleMap.addPolyline(polylineOptions)
//
//                        val boundsBuilder = LatLngBounds.builder()
//                        for (latLng in decodedPath) {
//                            boundsBuilder.include(latLng)
//                        }
//
//                        val bounds = boundsBuilder.build()
//                        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
//                        googleMap.animateCamera(cameraUpdate)
//                    }
//                }
//            },
//            Response.ErrorListener { error ->
//                error.printStackTrace()
//            }
//        )
//
//        queue.add(request)
//    }

//    private fun launchNavigation() {
//        waypoints.clear()
//
//        if (addresses.size < 2) {
//            Toast.makeText(context, "Not enough addresses", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val geocoder = Geocoder(context)
//
//        for (address in addresses) {
//            val results: List<Address>? = geocoder.getFromLocationName(address, 1)
//            if (results != null && results.isNotEmpty()) {
//                val location = results[0]
//                Log.i("street geocoder", location.toString())
//                val latLng = LatLng(location.latitude, location.longitude)
//                Log.i("Lat Lng",latLng.toString())
//                GoogleUtilities.waypoints.add(latLng)
//            }
//        }
//
//        if (GoogleUtilities.waypoints.size < 2) {
//            Toast.makeText(context, "Not enough way points", Toast.LENGTH_SHORT).show()
//            return
//        }
//        Toast.makeText(this@CourierMapView, "before if.", Toast.LENGTH_SHORT).show()
//        if (::waypoints.isInitialized) {
//            Toast.makeText(this@CourierMapView, "true", Toast.LENGTH_SHORT).show()
//            val intentUri = Uri.parse("google.navigation:q=${waypoints.last().latitude},${waypoints.last().longitude}")
//            val intent = Intent(Intent.ACTION_VIEW, intentUri)
//            intent.setPackage("com.google.android.apps.maps") // Use the package name of Google Maps
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivity(intent)
//            } else {
//                Toast.makeText(this@CourierMapView, "Google Maps app not installed.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}


