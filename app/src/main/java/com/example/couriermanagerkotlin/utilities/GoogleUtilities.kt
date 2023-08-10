package com.example.couriermanagerkotlin.utilities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.couriermanagerkotlin.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.TravelMode

class GoogleUtilities {

    companion object {
        var coordinates = ArrayList<Double>()
        var waypoints = ArrayList<LatLng>()

        fun validateAddressWithVolley(context: Context, addressToValidate: String, apiKey: String, errorMessage: TextView) {
            val baseUrl = "https://maps.googleapis.com/maps/api/geocode/json"
            val url = "${baseUrl}?address=${addressToValidate}&key=${apiKey}"
            val requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)
            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    val status = response.optString("status", "")
                    val results = response.optJSONArray("results")

                    if (status == "OK" && results != null && results.length() > 0) {
                        // Address is valid

                        // check if it's another usage of validation function
                        if(coordinates.size == 4) {
                            coordinates.clear()
                        }
                        val addressComponents = results.getJSONObject(0).optJSONArray("address_components")
                        if (addressComponents != null) {
                            // Check if the address belongs to Israel (country code: IL)
                            for (i in 0 until addressComponents.length()) {
                                val component = addressComponents.getJSONObject(i)
                                val types = component.optJSONArray("types")
                                if (types != null && types.length() > 0) {
                                    for (j in 0 until types.length()) {
                                        if (types.getString(j) == "country" && component.getString("short_name") == "IL") {
                                            // Address belongs to Israel
                                            val location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                                            Log.i("Coordinates Latitude", "Latitude: ${location.optDouble("lat", 0.0)}" )
                                            Log.i("Coordinates Longitude", "Longitude: ${location.optDouble("lng", 0.0)}" )
                                            coordinates.add(location.optDouble("lat", 0.0))
                                            coordinates.add(location.optDouble("lng", 0.0))
                                            Log.i("Coordinates Array","$coordinates")
                                        } else {
                                            // Address is not valid or not from Israel
//                                            errorMessage.text = "The entered address isn't from IL"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { error ->
                // Handle network error
                Log.e("Error occurred", error.toString())
            }
            requestQueue.add(request)
        }

        data class Location(val latitude: Double, val longitude: Double)

        data class DistanceAndDuration(val distance: Int, val duration: Int, val pickupLocation: Location, val deliveryLocation: Location)

        fun calculateDistanceDurationAndCoordinates(apiKey: String, pickupAddress: String, deliveryAddress: String): DistanceAndDuration? {
            val context = GeoApiContext.Builder()
                .apiKey(apiKey)
                .build()

            try {
                val directionsResult = DirectionsApi.newRequest(context)
                    .mode(TravelMode.DRIVING)
                    .origin(pickupAddress)
                    .destination(deliveryAddress)
                    .await()

                if (directionsResult.routes.isNotEmpty()) {
                    val route = directionsResult.routes[0].legs[0]
                    val distanceMeters = route.distance.inMeters.toInt()
                    val durationSeconds = route.duration.inSeconds.toInt()

                    val pickupLocation = Location(route.startLocation.lat, route.startLocation.lng)
                    val deliveryLocation = Location(route.endLocation.lat, route.endLocation.lng)

                    return DistanceAndDuration(distanceMeters, durationSeconds, pickupLocation, deliveryLocation)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

//        private fun launchNavigation(context: Context) {
//            var intent: Intent
//
//            if (waypoints.size > 0) {
//                val intentUri = Uri.parse("google.navigation:q=${waypoints.last().latitude},${waypoints.last().longitude}")
//                intent = Intent(Intent.ACTION_VIEW, intentUri)
//                intent.setPackage("com.google.android.apps.maps") // Use the package name of Google Maps
//                if (intent.resolveActivity(context.packageManager) != null) {
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(context.applicationContext, "Google Maps app not installed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        private fun showShortestRouteOnMapView(context: Context, addresses: List<String>, mapView: MapView) {
            waypoints.clear()

            if (addresses.size < 2) {
                Toast.makeText(context, "Not enough addresses", Toast.LENGTH_SHORT).show()
                return
            }

            val geocoder = Geocoder(context)

            for (address in addresses) {
                val results: List<Address>? = geocoder.getFromLocationName(address, 1)
                if (results != null && results.isNotEmpty()) {
                    val location = results[0]
                    Log.i("street geocoder", location.toString())
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.i("Lat Lng",latLng.toString())
                    waypoints.add(latLng)
                }
            }

            if (waypoints.size < 2) {
                Toast.makeText(context, "Not enough way points", Toast.LENGTH_SHORT).show()
                return
            }

            val apiKey = context.getString(R.string.GOOGLE_API_KEY)
            val waypointsStr = waypoints.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }
            val urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin=${waypoints.first().latitude},${waypoints.first().longitude}&destination=${waypoints.last().latitude},${waypoints.last().longitude}&waypoints=$waypointsStr&key=${apiKey}"

            val queue = Volley.newRequestQueue(context)
            val request = JsonObjectRequest(
                Request.Method.GET, urlStr, null,
                Response.Listener { response ->
                    val routes = response.optJSONArray("routes")
                    if (routes != null && routes.length() > 0) {
                        val route = routes.getJSONObject(0)
                        val overviewPolyline = route.getJSONObject("overview_polyline")
                        val encodedPolyline = overviewPolyline.getString("points")

                        mapView.getMapAsync { googleMap ->
                            val decodedPath = PolyUtil.decode(encodedPolyline)
                            val polylineOptions = PolylineOptions()
                                .addAll(decodedPath)
                                .color(Color.BLUE)
                                .width(10f)

                            googleMap.clear()
                            googleMap.addPolyline(polylineOptions)

                            val boundsBuilder = LatLngBounds.builder()
                            for (latLng in decodedPath) {
                                boundsBuilder.include(latLng)
                            }

                            val bounds = boundsBuilder.build()
                            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                            googleMap.animateCamera(cameraUpdate)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                }
            )

            queue.add(request)
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
}