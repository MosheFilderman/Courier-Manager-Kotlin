package com.example.couriermanagerkotlin

import android.content.Context
import android.location.Location
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.pow
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode


class GoogleUtilities {

    companion object {
        val coordinates = ArrayList<Double>()

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

    }


}