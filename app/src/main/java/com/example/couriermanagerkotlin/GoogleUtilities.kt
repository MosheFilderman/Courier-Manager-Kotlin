package com.example.couriermanagerkotlin

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class GoogleUtilities {

    companion object {
        fun validateAddressWithVolley(
            context: Context,
            addressToValidate: String,
            apiKey: String,
            callback: (Pair<Double, Double>?) -> Unit
        ) {
            val baseUrl = "https://maps.googleapis.com/maps/api/geocode/json"
            val url = "${baseUrl}?address=${addressToValidate}&key=${apiKey}"
            val requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)
            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    val status = response.optString("status", "")
                    val results = response.optJSONArray("results")

                    if (status == "OK" && results != null && results.length() > 0) {
                        // Address is valid
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
                                            val latitude = location.optDouble("lat", 0.0)
                                            val longitude = location.optDouble("lng", 0.0)
                                            callback(Pair(latitude, longitude))
                                            return@Listener
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Address is not valid or not from Israel
                    callback(null)
                }
            ) { error ->
                // Handle network error
                Log.e("Error occurred", error.toString())
                callback(null)
            }

            requestQueue.add(request)
        }
    }




}