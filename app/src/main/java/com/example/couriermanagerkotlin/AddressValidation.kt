package com.example.couriermanagerkotlin

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*

class AddressValidator(private val context: Context) {

        private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

        fun validateAddress(address: String): Address? {
            try {
                val addresses: List<Address> =
                    geocoder.getFromLocationName(address, 5) as List<Address> // Adjust the maxResults as per your requirement

                for (validatedAddress in addresses) {
                    if (validatedAddress.countryCode == "IL") {
                        return validatedAddress
                    }
                }
            } catch (e: IOException) {
                Log.e("AddressValidator", "Error geocoding address", e)
            }
            return null
        }



}

