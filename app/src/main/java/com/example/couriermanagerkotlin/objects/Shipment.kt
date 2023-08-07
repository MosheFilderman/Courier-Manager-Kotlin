package com.example.couriermanagerkotlin.objects

import com.example.couriermanagerkotlin.eStatus
import java.io.Serializable

class Shipment(
    val orderId: String,
    val pickupFirstName: String,
    val pickupLastName: String,
    val pickupPhone: String,
    val pickupEmail: String,
    val pickupCity: String,
    val pickupStreet: String,
    val pickupBuild: String,
    val deliveryName: String,
    val deliveryPhone: String,
    val deliveryEmail: String,
    val deliveryCity: String,
    val deliveryStreet: String,
    val deliveryBuild: String,
    var status: eStatus,
    val comment: String
    ) : Serializable {
}